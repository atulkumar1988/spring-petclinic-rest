package org.springframework.samples.petclinic.rest.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Generated;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.Objects;

/**
 * Editable fields of a pet owner.
 */
@Schema(name = "OwnerFields", description = "Editable fields of a pet owner.")
@JsonTypeName("OwnerFields")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-09-23T16:40:44.415477300+05:30[Asia/Calcutta]", comments = "Generator version: 7.25.0")
public record OwnerFieldsDto(
    /**
     * The first name of the pet owner.
     */
    @NotNull
    @Pattern(regexp = "^[\\p{L}]+([ '-][\\p{L}]+){0,2}$")
    @Size(min = 1, max = 30)
    @Schema(name = "firstName", example = "George", description = "The first name of the pet owner.", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty("firstName")
    String firstName,

    /**
     * The last name of the pet owner.
     */
    @NotNull
    @Pattern(regexp = "^[\\p{L}]+([ '-][\\p{L}]+){0,2}\\.?$")
    @Size(min = 1, max = 30)
    @Schema(name = "lastName", example = "Franklin", description = "The last name of the pet owner.", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty("lastName")
    String lastName,

    /**
     * The postal address of the pet owner.
     */
    @NotNull
    @Size(min = 1, max = 255)
    @Schema(name = "address", example = "110 W. Liberty St.", description = "The postal address of the pet owner.", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty("address")
    String address,

    /**
     * The city of the pet owner.
     */
    @NotNull
    @Size(min = 1, max = 80)
    @Schema(name = "city", example = "Madison", description = "The city of the pet owner.", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty("city")
    String city,

    /**
     * The telephone number of the pet owner.
     */
    @NotNull
    @Pattern(regexp = "^[0-9]*$")
    @Size(min = 1, max = 20)
    @Schema(name = "telephone", example = "6085551023", description = "The telephone number of the pet owner.", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty("telephone")
    String telephone
) {

    @JsonCreator
    public OwnerFieldsDto {
        Objects.requireNonNull(firstName, "firstName must not be null");
        Objects.requireNonNull(lastName, "lastName must not be null");
        Objects.requireNonNull(address, "address must not be null");
        Objects.requireNonNull(city, "city must not be null");
        Objects.requireNonNull(telephone, "telephone must not be null");

        if (firstName.length() < 1 || firstName.length() > 30) {
            throw new IllegalArgumentException("firstName length must be between 1 and 30");
        }
        if (lastName.length() < 1 || lastName.length() > 30) {
            throw new IllegalArgumentException("lastName length must be between 1 and 30");
        }
        if (address.length() < 1 || address.length() > 255) {
            throw new IllegalArgumentException("address length must be between 1 and 255");
        }
        if (city.length() < 1 || city.length() > 80) {
            throw new IllegalArgumentException("city length must be between 1 and 80");
        }
        if (telephone.length() < 1 || telephone.length() > 20) {
            throw new IllegalArgumentException("telephone length must be between 1 and 20");
        }
    }
}
