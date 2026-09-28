package org.springframework.samples.petclinic.rest.dto;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Generated;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Messages describing a validation error.
 */
@Schema(name = "ValidationMessage", description = "Messages describing a validation error.")
@JsonTypeName("ValidationMessage")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-09-23T16:40:44.415477300+05:30[Asia/Calcutta]", comments = "Generator version: 7.25.0")
public record ValidationMessageDto(
    /**
     * The validation message.
     */
    @Schema(name = "message", accessMode = Schema.AccessMode.READ_ONLY, example = "[Path '/lastName'] Instance type (null) does not match any allowed primitive type (allowed: ['string'])", description = "The validation message.", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty("message")
    String message,

    /**
     * A container for additional, undeclared properties.
     */
    Map<String, Object> additionalProperties
) {

    @JsonCreator
    public ValidationMessageDto {
        Objects.requireNonNull(message, "message must not be null");
        additionalProperties = additionalProperties == null ? new HashMap<>() : new HashMap<>(additionalProperties);
    }

    @JsonAnySetter
    public ValidationMessageDto putAdditionalProperty(String key, Object value) {
        additionalProperties.put(key, value);
        return this;
    }

    @JsonAnyGetter
    @Override
    public Map<String, Object> additionalProperties() {
        return additionalProperties;
    }

    public Object getAdditionalProperty(String key) {
        return additionalProperties.get(key);
    }
}
