package br.com.vsgi.core.domain.user;

import jakarta.validation.constraints.NotBlank;

public record UserDto(@NotBlank String name, String description, @NotBlank String login,@NotBlank String password, UserRole role) {
}