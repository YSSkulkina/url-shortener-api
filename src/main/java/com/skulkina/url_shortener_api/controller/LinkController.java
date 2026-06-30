package com.skulkina.url_shortener_api.controller;

import com.skulkina.url_shortener_api.dto.CreateLinkRequest;
import com.skulkina.url_shortener_api.dto.LinkResponse;
import com.skulkina.url_shortener_api.service.LinkService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/links")
@RequiredArgsConstructor
public class LinkController {
    private final LinkService linkService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public LinkResponse createLink(@Valid @RequestBody CreateLinkRequest request) {
        return linkService.createLink(request);
    }

    @GetMapping
    public List<LinkResponse> getCurrentUserLinks() {
        return linkService.getCurrentUserLinks();
    }
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteLink(@PathVariable Long id) {
        linkService.deleteCurrentUserLink(id);
    }

}
