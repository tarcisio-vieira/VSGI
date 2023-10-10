package br.com.vsgi.core.type;

import org.springframework.http.HttpStatus;

public class CoreErrorException extends RuntimeException {

	private static final long serialVersionUID = -6172410191250025202L;

	public CoreErrorException(String message, HttpStatus status) {
		super(message);
	}
}
