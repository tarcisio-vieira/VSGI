package br.com.vsgi.core.domain.user;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotBlank;

public record UserDto(String id,@NotBlank String login,@NotBlank String password, String role, BigDecimal value) {
}
