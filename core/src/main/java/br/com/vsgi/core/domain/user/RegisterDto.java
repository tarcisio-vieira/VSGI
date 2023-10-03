package br.com.vsgi.core.domain.user;

public record RegisterDto(String login, String password, UserRole role) {
}
