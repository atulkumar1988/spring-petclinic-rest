package org.springframework.samples.petclinic.rest.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Generated;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.List;
import java.util.Objects;

/**
 * A veterinarian.
 */
@Schema(name = "Vet", description = "A veterinarian.")
@JsonTypeName("Vet")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-09-23T16:40:44.415477300+05:30[Asia/Calcutta]", comments = "Generator version: 7.25.0")
public record VetDto(
    /**
     * The first name of the vet.
     */
    @NotNull
    @Pattern(regexp = "^[\\p{L}]+([ '-][\\p{L}]+){0,2}$")
    @Size(min = 1, max = 30)
    @Schema(name = "firstName", example = "James", description = "The first name of the vet.", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty("firstName")
    String firstName,

    /**
     * The last name of the vet.
     */
    @NotNull
    @Pattern(regexp = "^[\\p{L}]+([ '-][\\p{L}]+){0,2}\\.?$")
    @Size(min = 1, max = 30)
    @Schema(name = "lastName", example = "Carter", description = "The last name of the vet.", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty("lastName")
    String lastName,

    /**
     * The specialties of the vet.
     */
    @NotNull
    @Valid
    @Schema(name = "specialties", description = "The specialties of the vet.", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty("specialties")
    List<@Valid SpecialtyDto> specialties,

    /**
     * The ID of the vet.
     * minimum: 0
     */
    @Min(value = 0)
    @Schema(name = "id", accessMode = Schema.AccessMode.READ_ONLY, example = "1", description = "The ID of the vet.", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty("id")
    Integer id
) {

    @JsonCreator
    public VetDto {
        Objects.requireNonNull(firstName, "firstName must not be null");
        Objects.requireNonNull(lastName, "lastName must not be null");
        Objects.requireNonNull(specialties, "specialties must not be null");
        Objects.requireNonNull(id, "id must not be null");

        if (firstName.length() < 1 || firstName.length() > 30) {
            throw new IllegalArgumentException("firstName length must be between 1 and 30");
        }
        if (lastName.length() < 1 || lastName.length() > 30) {
            throw new IllegalArgumentException("lastName length must be between 1 and 30");
        }
        if (id < 0) {
            throw new IllegalArgumentException("id must be greater than or equal to 0");
        }

        specialties = List.copyOf(specialties);
    }
}
