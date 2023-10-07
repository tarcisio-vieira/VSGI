package br.com.vsgi.core.domain.client;

import jakarta.validation.constraints.NotBlank;

public record ClientDto(@NotBlank String name,String description) {
}
