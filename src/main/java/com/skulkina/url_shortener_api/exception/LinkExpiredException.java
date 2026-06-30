package com.skulkina.url_shortener_api.exception;

public class LinkExpiredException extends RuntimeException{
    public LinkExpiredException(String shortCode) {
        super("Link expired: " + shortCode);
    }

}
