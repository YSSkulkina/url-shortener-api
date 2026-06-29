package com.skulkina.url_shortener_api.controller;

import com.skulkina.url_shortener_api.service.LinkService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
@Slf4j
@RestController
@RequiredArgsConstructor
public class RedirectController {
    private final LinkService linkService;

    @GetMapping("/{shortCode}")
    @ResponseStatus(HttpStatus.FOUND)
    public void redirect(@PathVariable String shortCode, HttpServletResponse response) throws IOException {
        log.info("Redirect request: {}", shortCode);
        String originalUrl = linkService.getOriginalUrlAndIncrementClickCount(shortCode);
        response.sendRedirect(originalUrl);
    }

}
