package ru.sladkkov.jwtauthentification.exception;

public class JwtAuthenticationException extends RuntimeException {
    public JwtAuthenticationException() {
        super();
    }

    public JwtAuthenticationException(String message) {
        super(message);
    }
}
