package org.springframework.samples.petclinic.rest.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Generated;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.jspecify.annotations.Nullable;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.Objects;

/**
 * A booking for a vet visit.
 */
@Schema(name = "Visit", description = "A booking for a vet visit.")
@JsonTypeName("Visit")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-09-23T15:01:46.863656600+05:30[Asia/Calcutta]", comments = "Generator version: 7.25.0")
public record VisitDto(
    /**
     * The date of the visit.
     */
    @Valid
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @Schema(name = "date", example = "2013-01-01", description = "The date of the visit.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @JsonProperty("date")
    @Nullable LocalDate date,

    /**
     * The description for the visit.
     */
    @NotNull
    @Size(min = 1, max = 255)
    @Schema(name = "description", example = "rabies shot", description = "The description for the visit.", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty("description")
    String description,

    /**
     * The ID of the visit.
     * minimum: 0
     */
    @Min(value = 0)
    @Schema(name = "id", accessMode = Schema.AccessMode.READ_ONLY, example = "1", description = "The ID of the visit.", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty("id")
    Integer id,

    /**
     * The ID of the pet.
     * minimum: 0
     */
    @NotNull
    @Min(value = 0)
    @Schema(name = "petId", example = "1", description = "The ID of the pet.", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty("petId")
    Integer petId
) {

    @JsonCreator
    public VisitDto {
        Objects.requireNonNull(description, "description must not be null");
        Objects.requireNonNull(id, "id must not be null");
        Objects.requireNonNull(petId, "petId must not be null");

        if (description.length() < 1 || description.length() > 255) {
            throw new IllegalArgumentException("description length must be between 1 and 255");
        }
        if (id < 0) {
            throw new IllegalArgumentException("id must be greater than or equal to 0");
        }
        if (petId < 0) {
            throw new IllegalArgumentException("petId must be greater than or equal to 0");
        }
    }
}
