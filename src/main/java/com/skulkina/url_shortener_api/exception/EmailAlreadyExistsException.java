package com.skulkina.url_shortener_api.exception;

public class EmailAlreadyExistsException extends RuntimeException {

    public EmailAlreadyExistsException(String email) {
        super("User with email already exists: " + email);
    }
}
