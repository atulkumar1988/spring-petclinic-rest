package org.springframework.samples.petclinic.rest.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Generated;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;

/**
 * The schema for all error responses.
 */
@Schema(name = "ProblemDetail", description = "The schema for all error responses.")
@JsonTypeName("ProblemDetail")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-09-23T16:40:44.415477300+05:30[Asia/Calcutta]", comments = "Generator version: 7.25.0")
public record ProblemDetailDto(
    /**
     * Full URL that originated the error response.
     */
    @Valid
    @Schema(name = "type", accessMode = Schema.AccessMode.READ_ONLY, example = "http://localhost:9966/petclinic/api/owner", description = "Full URL that originated the error response.", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty("type")
    URI type,

    /**
     * The short error title.
     */
    @Schema(name = "title", accessMode = Schema.AccessMode.READ_ONLY, example = "NoResourceFoundException", description = "The short error title.", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty("title")
    String title,

    /**
     * HTTP status code
     * minimum: 400
     * maximum: 600
     */
    @Min(value = 400)
    @Max(value = 600)
    @Schema(name = "status", accessMode = Schema.AccessMode.READ_ONLY, example = "500", description = "HTTP status code", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty("status")
    Integer status,

    /**
     * The long error message.
     */
    @Schema(name = "detail", accessMode = Schema.AccessMode.READ_ONLY, example = "No static resource api/owner.", description = "The long error message.", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty("detail")
    String detail,

    /**
     * The time the error occurred.
     */
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @Valid
    @Schema(name = "timestamp", accessMode = Schema.AccessMode.READ_ONLY, example = "2024-11-23T13:59:21.382040700Z", description = "The time the error occurred.", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty("timestamp")
    OffsetDateTime timestamp,

    /**
     * Validation errors against the OpenAPI schema.
     */
    @NotNull
    @Valid
    @Schema(name = "schemaValidationErrors", description = "Validation errors against the OpenAPI schema.", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty("schemaValidationErrors")
    List<ValidationMessageDto> schemaValidationErrors
) {

    @JsonCreator
    public ProblemDetailDto {
        Objects.requireNonNull(type, "type must not be null");
        Objects.requireNonNull(title, "title must not be null");
        Objects.requireNonNull(status, "status must not be null");
        Objects.requireNonNull(detail, "detail must not be null");
        Objects.requireNonNull(timestamp, "timestamp must not be null");
        Objects.requireNonNull(schemaValidationErrors, "schemaValidationErrors must not be null");

        if (status < 400) {
            throw new IllegalArgumentException("status must be greater than or equal to 400");
        }
        if (status > 600) {
            throw new IllegalArgumentException("status must be less than or equal to 600");
        }

        schemaValidationErrors = List.copyOf(schemaValidationErrors);
    }
}
