package org.springframework.samples.petclinic.rest.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Generated;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.Objects;

/**
 * A page of pet owners.
 */
@Schema(name = "OwnerPage", description = "A page of pet owners.")
@JsonTypeName("OwnerPage")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-09-23T16:40:44.415477300+05:30[Asia/Calcutta]", comments = "Generator version: 7.25.0")
public record OwnerPageDto(
    /**
     * Pet owners in the requested page.
     */
    @NotNull
    @Valid
    @Schema(name = "content", description = "Pet owners in the requested page.", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty("content")
    List<OwnerDto> content,

    /**
     * Zero-based page index.
     * minimum: 0
     */
    @NotNull
    @Min(value = 0)
    @Schema(name = "page", example = "0", description = "Zero-based page index.", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty("page")
    Integer page,

    /**
     * Requested page size.
     * minimum: 1
     */
    @NotNull
    @Min(value = 1)
    @Schema(name = "size", example = "5", description = "Requested page size.", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty("size")
    Integer size,

    /**
     * Total number of owners matching the request.
     * minimum: 0
     */
    @NotNull
    @Min(value = 0L)
    @Schema(name = "totalElements", example = "10", description = "Total number of owners matching the request.", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty("totalElements")
    Long totalElements,

    /**
     * Total number of pages matching the request.
     * minimum: 0
     */
    @NotNull
    @Min(value = 0)
    @Schema(name = "totalPages", example = "2", description = "Total number of pages matching the request.", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty("totalPages")
    Integer totalPages
) {

    @JsonCreator
    public OwnerPageDto {
        Objects.requireNonNull(content, "content must not be null");
        Objects.requireNonNull(page, "page must not be null");
        Objects.requireNonNull(size, "size must not be null");
        Objects.requireNonNull(totalElements, "totalElements must not be null");
        Objects.requireNonNull(totalPages, "totalPages must not be null");

        if (page < 0) {
            throw new IllegalArgumentException("page must be greater than or equal to 0");
        }
        if (size < 1) {
            throw new IllegalArgumentException("size must be greater than or equal to 1");
        }
        if (totalElements < 0) {
            throw new IllegalArgumentException("totalElements must be greater than or equal to 0");
        }
        if (totalPages < 0) {
            throw new IllegalArgumentException("totalPages must be greater than or equal to 0");
        }

        content = List.copyOf(content);
    }
}
