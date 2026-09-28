package org.springframework.samples.petclinic.rest.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Generated;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.Objects;

/**
 * Editable fields of a pet type.
 */
@Schema(name = "PetTypeFields", description = "Editable fields of a pet type.")
@JsonTypeName("PetTypeFields")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-09-23T16:40:44.415477300+05:30[Asia/Calcutta]", comments = "Generator version: 7.25.0")
public record PetTypeFieldsDto(
    /**
     * The name of the pet type.
     */
    @NotNull
    @Size(min = 1, max = 80)
    @Schema(name = "name", example = "cat", description = "The name of the pet type.", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty("name")
    String name
) {

    @JsonCreator
    public PetTypeFieldsDto {
        Objects.requireNonNull(name, "name must not be null");

        if (name.length() < 1 || name.length() > 80) {
            throw new IllegalArgumentException("name length must be between 1 and 80");
        }
    }
}
