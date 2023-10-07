package br.com.vsgi.core.domain.organization;

import jakarta.validation.constraints.NotBlank;

public record OrgDto(@NotBlank String name,String description) {
}
