package br.com.vsgi.core.domain.user;

import jakarta.validation.constraints.NotBlank;

public record UserDto(@NotBlank String login,@NotBlank String password, String role) {
}
