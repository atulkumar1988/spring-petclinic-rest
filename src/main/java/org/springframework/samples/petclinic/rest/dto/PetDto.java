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
import java.util.List;
import java.util.Objects;

/**
 * A pet.
 */
@Schema(name = "Pet", description = "A pet.")
@JsonTypeName("Pet")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-09-23T15:01:46.863656600+05:30[Asia/Calcutta]", comments = "Generator version: 7.25.0")
public record PetDto(
    /**
     * The name of the pet.
     */
    @NotNull
    @Size(max = 30)
    @Schema(name = "name", example = "Leo", description = "The name of the pet.", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty("name")
    String name,

    /**
     * The date of birth of the pet.
     */
    @org.springframework.samples.petclinic.rest.validation.PetAgeValidation
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @NotNull
    @Valid
    @Schema(name = "birthDate", example = "2010-09-07", description = "The date of birth of the pet.", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty("birthDate")
    LocalDate birthDate,

    /**
     * Get type
     */
    @NotNull
    @Valid
    @Schema(name = "type", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty("type")
    PetTypeDto type,

    /**
     * The ID of the pet.
     * minimum: 0
     */
    @Min(value = 0)
    @Schema(name = "id", accessMode = Schema.AccessMode.READ_ONLY, example = "1", description = "The ID of the pet.", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty("id")
    Integer id,

    /**
     * The ID of the pet's owner.
     * minimum: 0
     */
    @Min(value = 0)
    @Schema(name = "ownerId", accessMode = Schema.AccessMode.READ_ONLY, example = "1", description = "The ID of the pet's owner.", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @JsonProperty("ownerId")
    @Nullable Integer ownerId,

    /**
     * Vet visit bookings for this pet.
     */
    @Valid
    @Schema(name = "visits", accessMode = Schema.AccessMode.READ_ONLY, description = "Vet visit bookings for this pet.", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty("visits")
    List<VisitDto> visits
) {

    @JsonCreator
    public PetDto {
        Objects.requireNonNull(name, "name must not be null");
        Objects.requireNonNull(birthDate, "birthDate must not be null");
        Objects.requireNonNull(type, "type must not be null");
        Objects.requireNonNull(id, "id must not be null");
        Objects.requireNonNull(visits, "visits must not be null");

        if (name.length() > 30) {
            throw new IllegalArgumentException("name must not exceed 30 characters");
        }
        if (id < 0) {
            throw new IllegalArgumentException("id must be greater than or equal to 0");
        }
        if (ownerId != null && ownerId < 0) {
            throw new IllegalArgumentException("ownerId must be greater than or equal to 0");
        }

        visits = List.copyOf(visits);
    }
}
