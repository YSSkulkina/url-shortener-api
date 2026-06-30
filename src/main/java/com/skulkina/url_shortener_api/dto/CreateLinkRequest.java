package com.skulkina.url_shortener_api.dto;

import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.URL;

import java.time.LocalDateTime;

public record CreateLinkRequest(
        @NotBlank(message = "Original URL is required")
        @URL(message = "Original URL must be valid")
        String originalUrl,
        LocalDateTime expiresAt
) {

}
