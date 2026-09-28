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

import java.util.List;
import java.util.Objects;

/**
 * An user.
 */
@Schema(name = "User", description = "An user.")
@JsonTypeName("User")
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2026-09-23T16:40:44.415477300+05:30[Asia/Calcutta]", comments = "Generator version: 7.25.0")
public record UserDto(
    /**
     * The username
     */
    @NotNull
    @Size(min = 1, max = 80)
    @Schema(name = "username", example = "john.doe", description = "The username", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty("username")
    String username,

    /**
     * The password
     */
    @Size(min = 1, max = 80)
    @Schema(name = "password", example = "1234abc", description = "The password", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @JsonProperty("password")
    @Nullable String password,

    /**
     * Indicates if the user is enabled
     */
    @Schema(name = "enabled", example = "true", description = "Indicates if the user is enabled", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @JsonProperty("enabled")
    @Nullable Boolean enabled,

    /**
     * The roles of an user
     */
    @Valid
    @Schema(name = "roles", description = "The roles of an user", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @JsonProperty("roles")
    List<@Valid RoleDto> roles
) {

    @JsonCreator
    public UserDto {
        Objects.requireNonNull(username, "username must not be null");

        if (username.length() < 1 || username.length() > 80) {
            throw new IllegalArgumentException("username length must be between 1 and 80");
        }
        if (password != null && (password.length() < 1 || password.length() > 80)) {
            throw new IllegalArgumentException("password length must be between 1 and 80");
        }

        roles = roles == null ? List.of() : List.copyOf(roles);
    }
}
