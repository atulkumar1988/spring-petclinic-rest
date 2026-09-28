package org.springframework.samples.petclinic.rest.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Generated;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.jspecify.annotations.Nullable;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Editable fields of a vet visit.
 */
@Schema(name = "VisitFields", description = "Editable fields of a vet visit.")
@JsonTypeName("VisitFields")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-09-23T16:40:44.415477300+05:30[Asia/Calcutta]", comments = "Generator version: 7.25.0")
public record VisitFieldsDto(
    /**
     * The date of the visit.
     */
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @Valid
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
    String description
) {

    @JsonCreator
    public VisitFieldsDto {
        Objects.requireNonNull(description, "description must not be null");

        if (description.length() < 1 || description.length() > 255) {
            throw new IllegalArgumentException("description length must be between 1 and 255");
        }
    }
}
