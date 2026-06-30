package com.skulkina.url_shortener_api.exception;

public class LinkNotFoundException extends RuntimeException {
    public LinkNotFoundException(String shortCode) {
        super("Link not found: " + shortCode);
    }

    public LinkNotFoundException(Long id) {
        super("Link not found: " + id);
    }

}
