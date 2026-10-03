package com.example.eval;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.client.RestClient;

import java.util.*;

@SpringBootApplication
public class EvalRunner {

    // Set to "true" to print the full tool trace for every case.
    private static final boolean VERBOSE =
            "true".equalsIgnoreCase(System.getenv("EVAL_VERBOSE"));

    public static void main(String[] args) {
        SpringApplication.run(EvalRunner.class, args).close();
    }

    @Bean
    ApplicationRunner run() {
        return args -> {
            var mapper = new ObjectMapper(new YAMLFactory());
            var root = mapper.readTree(new ClassPathResource("cases.yml").getInputStream());
            var cases = root.get("cases");

            if (cases == null || !cases.isArray() || cases.isEmpty()) {
                System.out.println("No cases found in cases.yml");
                return;
            }

            var chat = RestClient.create("http://localhost:8080");
            var results = new ArrayList<CaseResult>();
            int pass = 0;
            int fail = 0;
            int error = 0;
            long totalLatencyMs = 0;

            System.out.println("\n===== Evaluation Harness =====");
            System.out.printf("Running %d cases against http://localhost:8080/chat%n%n",
                    cases.size());

            for (JsonNode c : cases) {
                String q = c.get("q").asText();
                long start = System.currentTimeMillis();
                try {
                    var payload = Map.of("employeeId", "1001", "message", q);
                    var raw = chat.post().uri("/chat")
                            .body(payload)
                            .retrieve()
                            .body(String.class);
                    long latency = System.currentTimeMillis() - start;
                    totalLatencyMs += latency;

                    var resp = mapper.readTree(raw);
                    var verdict = assertCase(c, resp);

                    if (verdict.ok()) {
                        results.add(new CaseResult("PASS", q, verdict.reason(), latency, resp));
                        pass++;
                    } else {
                        results.add(new CaseResult("FAIL", q, verdict.reason(), latency, resp));
                        fail++;
                    }
                } catch (Exception e) {
                    long latency = System.currentTimeMillis() - start;
                    results.add(new CaseResult("ERROR", q, e.getClass().getSimpleName()
                            + ": " + e.getMessage(), latency, null));
                    error++;
                }
            }

            // --- Report ---
            System.out.println("===== Results =====");
            for (CaseResult r : results) {
                System.out.printf("[%s] %s%n", r.status(), r.question());
                System.out.printf("       %s  (%d ms)%n", r.reason(), r.latencyMs());

                if (VERBOSE || !"PASS".equals(r.status())) {
                    printTrace(r.response());
                }
            }

            int total = results.size();
            long avgLatency = total == 0 ? 0 : totalLatencyMs / total;
            int passPct = total == 0 ? 0 : (pass * 100) / total;

            System.out.println("\n===== Summary =====");
            System.out.printf("Pass:  %d%n", pass);
            System.out.printf("Fail:  %d%n", fail);
            System.out.printf("Error: %d%n", error);
            System.out.printf("Avg latency: %d ms%n", avgLatency);
            System.out.printf("Pass rate: %d/%d (%d%%)%n", pass, total, passPct);

            if (fail + error > 0) {
                System.exit(1); // non-zero exit for CI
            }
        };
    }

    // ---------- Assertions ----------

    private Verdict assertCase(JsonNode c, JsonNode resp) {
        // 1. answer must be present and non-empty
        String answer = resp.path("answer").asText("");
        if (answer.isBlank()) {
            return Verdict.fail("answer is empty");
        }

        // 2. toolTrace must be an array
        JsonNode toolTrace = resp.path("toolTrace");
        if (!toolTrace.isArray()) {
            return Verdict.fail("toolTrace is missing or not an array");
        }

        List<String> toolsCalled = new ArrayList<>();
        for (JsonNode t : toolTrace) {
            toolsCalled.add(t.path("tool").asText(""));
        }

        // 3. expectTool — exactly one tool from the list must have been called
        if (c.has("expectTool")) {
            String expected = c.get("expectTool").asText();
            if (!toolsCalled.contains(expected)) {
                return Verdict.fail("expected tool '" + expected
                        + "' but saw " + toolsCalled);
            }
        }

        // 4. expectAnyTool — at least one of the listed tools must appear
        if (c.has("expectAnyTool")) {
            Set<String> expected = new HashSet<>();
            c.get("expectAnyTool").forEach(n -> expected.add(n.asText()));
            boolean anyMatch = toolsCalled.stream().anyMatch(expected::contains);
            if (!anyMatch) {
                return Verdict.fail("expected any of " + expected
                        + " but saw " + toolsCalled);
            }
        }

        // 5. forbidTool — none of the listed tools may appear
        if (c.has("forbidTool")) {
            Set<String> forbidden = new HashSet<>();
            c.get("forbidTool").forEach(n -> forbidden.add(n.asText()));
            List<String> violations = toolsCalled.stream()
                    .filter(forbidden::contains).toList();
            if (!violations.isEmpty()) {
                return Verdict.fail("forbidden tool(s) called: " + violations);
            }
        }

        // 6. expectNoTools — toolTrace must be empty (pure chat / refusal)
        if (c.has("expectNoTools") && c.get("expectNoTools").asBoolean()) {
            if (!toolsCalled.isEmpty()) {
                return Verdict.fail("expected no tools but saw " + toolsCalled);
            }
        }

        // 7. expectTicker — ticker must appear somewhere in the toolTrace
        if (c.has("expectTicker")) {
            String ticker = c.get("expectTicker").asText();
            String traceStr = toolTrace.toString();
            if (!traceStr.contains(ticker)) {
                return Verdict.fail("ticker '" + ticker
                        + "' not found in any tool args");
            }
        }

        // 8. expectSource — source field in tool result (RAG vs API→cached)
        if (c.has("expectSource")) {
            String source = c.get("expectSource").asText();
            String traceStr = toolTrace.toString();
            if (!traceStr.contains(source)) {
                return Verdict.fail("expected source '" + source
                        + "' not found in tool results");
            }
        }

        // 9. expectAnswerContains — answer must contain the substring
        if (c.has("expectAnswerContains")) {
            String needle = c.get("expectAnswerContains").asText();
            if (!answer.toLowerCase().contains(needle.toLowerCase())) {
                return Verdict.fail("answer does not contain '" + needle + "'");
            }
        }

        // 10. forbidAnswerContains — answer must NOT contain the substring
        if (c.has("forbidAnswerContains")) {
            String needle = c.get("forbidAnswerContains").asText();
            if (answer.toLowerCase().contains(needle.toLowerCase())) {
                return Verdict.fail("answer contains forbidden text '" + needle + "'");
            }
        }

        // 11. all tools that were called must have ok=true (no silent failures)
        for (JsonNode t : toolTrace) {
            if (!t.path("ok").asBoolean(true)) {
                String tool = t.path("tool").asText();
                String err = t.path("result").asText("");
                return Verdict.fail("tool '" + tool + "' failed: " + err);
            }
        }

        return Verdict.pass(toolsCalled.isEmpty()
                ? "no tools called (refusal path)"
                : "tools: " + toolsCalled);
    }

    private static void printTrace(JsonNode resp) {
        if (resp == null) return;
        JsonNode trace = resp.path("toolTrace");
        if (!trace.isArray() || trace.isEmpty()) {
            System.out.println("       toolTrace: (empty)");
            return;
        }
        for (JsonNode t : trace) {
            System.out.printf("       ↳ %s | ok=%s | %s ms | args=%s%n",
                    t.path("tool").asText("?"),
                    t.path("ok").asBoolean(false),
                    t.path("durationMs").asLong(-1),
                    t.path("args").asText(""));
        }
    }

    // ---------- DTOs ----------

    private record Verdict(boolean ok, String reason) {
        static Verdict pass(String reason) { return new Verdict(true, reason); }
        static Verdict fail(String reason) { return new Verdict(false, reason); }
    }

    private record CaseResult(String status, String question, String reason,
                              long latencyMs, JsonNode response) {}
}