package br.com.vsgi.core.domain.authentication;

import br.com.vsgi.core.domain.user.UserRole;

public record RegisterDto(String login, String password, UserRole role) {
}
