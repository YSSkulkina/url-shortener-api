package com.skulkina.url_shortener_api.dto;

import java.time.LocalDateTime;

public record LinkResponse(
        Long id,
        String originalUrl,
        String shortCode,
        String shortUrl,
        Long clickCount,
        LocalDateTime createdAt
) {

}
