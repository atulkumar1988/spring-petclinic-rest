package org.springframework.samples.petclinic.rest.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Generated;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.Objects;

/**
 * Fields of specialty of vets.
 */
@Schema(name = "Specialty", description = "Fields of specialty of vets.")
@JsonTypeName("Specialty")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-09-23T16:40:44.415477300+05:30[Asia/Calcutta]", comments = "Generator version: 7.25.0")
public record SpecialtyDto(
    /**
     * The ID of the specialty.
     * minimum: 0
     */
    @Min(value = 0)
    @Schema(name = "id", accessMode = Schema.AccessMode.READ_ONLY, example = "1", description = "The ID of the specialty.", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty("id")
    Integer id,

    /**
     * The name of the specialty.
     */
    @NotNull
    @Size(min = 1, max = 80)
    @Schema(name = "name", example = "radiology", description = "The name of the specialty.", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty("name")
    String name
) {

    @JsonCreator
    public SpecialtyDto {
        Objects.requireNonNull(id, "id must not be null");
        Objects.requireNonNull(name, "name must not be null");

        if (id < 0) {
            throw new IllegalArgumentException("id must be greater than or equal to 0");
        }
        if (name.length() < 1 || name.length() > 80) {
            throw new IllegalArgumentException("name length must be between 1 and 80");
        }
    }
}
