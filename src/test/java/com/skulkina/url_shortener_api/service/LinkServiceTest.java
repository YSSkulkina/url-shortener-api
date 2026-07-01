package com.skulkina.url_shortener_api.service;

import com.skulkina.url_shortener_api.dto.CreateLinkRequest;
import com.skulkina.url_shortener_api.dto.LinkResponse;
import com.skulkina.url_shortener_api.entity.Link;
import com.skulkina.url_shortener_api.entity.Role;
import com.skulkina.url_shortener_api.entity.User;
import com.skulkina.url_shortener_api.mapper.LinkMapper;
import com.skulkina.url_shortener_api.repository.LinkRepository;
import com.skulkina.url_shortener_api.security.CurrentUserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LinkServiceTest {

    @Mock
    private LinkRepository linkRepository;
    @Mock
    private ShortCodeGenerator shortCodeGenerator;
    @Mock
    private CurrentUserService currentUserService;
    @Mock
    private LinkMapper linkMapper;
    @InjectMocks
    private LinkService linkService;


    @Test
    void createLink() {
        //Arrange
        CreateLinkRequest request = new CreateLinkRequest("https://test.com", LocalDateTime.now().plusMinutes(10));

        User user = User.builder()
                .id(1L)
                .email("test@test.com")
                .role(Role.USER)
                .build();

        Link link = Link.builder()
                .id(1L)
                .shortCode("ABC123")
                .originalUrl("https://test.com")
                .user(user)
                .build();

        LinkResponse response = new LinkResponse(1L, "https://test.com", "ABC123", "localhost/ABC123", 3L, LocalDateTime.now(), LocalDateTime.now().plusMinutes(10));
        when(currentUserService.getCurrentUser()).thenReturn(user);
        when(shortCodeGenerator.generate()).thenReturn("ABC123");
        when(linkRepository.save(any())).thenReturn(link);
        when(linkMapper.toResponse(any(), any())).thenReturn(response);
        //Act
        LinkResponse result = linkService.createLink(request);
        //Assert

        ArgumentCaptor<Link> linkCaptor = ArgumentCaptor.forClass(Link.class);
        verify(linkRepository).save(linkCaptor.capture());
        Link savedLink = linkCaptor.getValue();

        assertEquals("https://test.com", savedLink.getOriginalUrl());
        assertEquals("ABC123", savedLink.getShortCode());
        assertEquals(user, savedLink.getUser());
        assertEquals(0L, savedLink.getClickCount());
        assertEquals(response, result);
        assertEquals(1L, result.id());
        assertEquals("https://test.com", result.originalUrl());
        assertEquals("ABC123", result.shortCode());

        verify(shortCodeGenerator).generate();
        verify(currentUserService).getCurrentUser();
        verify(linkMapper).toResponse(link, null);
        verify(linkRepository).existsByShortCode("ABC123");
    }
    @Test
    void createLink_whenShortCodeAlreadyExists_generatesNewCode() {
        //Arrange
        CreateLinkRequest request = new CreateLinkRequest("https://test.com", LocalDateTime.now().plusMinutes(10));

        User user = User.builder()
                .id(1L)
                .email("test@test.com")
                .role(Role.USER)
                .build();

        Link link = Link.builder()
                .id(1L)
                .shortCode("XYZ789")
                .originalUrl("https://test.com")
                .user(user)
                .build();

        LinkResponse response = new LinkResponse(
                1L,
                "https://test.com",
                "XYZ789",
                "localhost/XYZ789",
                3L,
                LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(10));
        when(currentUserService.getCurrentUser()).thenReturn(user);
        when(shortCodeGenerator.generate())
                .thenReturn("ABC123")
                .thenReturn("XYZ789");
        when(linkRepository.existsByShortCode("ABC123"))
                .thenReturn(true);

        when(linkRepository.existsByShortCode("XYZ789"))
                .thenReturn(false);

        when(linkRepository.save(any())).thenReturn(link);
        when(linkMapper.toResponse(any(), any())).thenReturn(response);
        //Act
        LinkResponse result = linkService.createLink(request);
        //Assert
        ArgumentCaptor<Link> linkCaptor = ArgumentCaptor.forClass(Link.class);
        verify(linkRepository).save(linkCaptor.capture());
        Link savedLink = linkCaptor.getValue();
        assertEquals("https://test.com", savedLink.getOriginalUrl());
        assertEquals("XYZ789", savedLink.getShortCode());

        assertEquals(response, result);
        assertEquals("XYZ789", result.shortCode());

        verify(shortCodeGenerator, times(2)).generate();
        verify(linkRepository).existsByShortCode("ABC123");
        verify(linkRepository).existsByShortCode("XYZ789");

    }
}