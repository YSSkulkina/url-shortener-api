package com.skulkina.url_shortener_api.service;

import com.skulkina.url_shortener_api.dto.CreateLinkRequest;
import com.skulkina.url_shortener_api.dto.LinkResponse;
import com.skulkina.url_shortener_api.entity.Link;
import com.skulkina.url_shortener_api.entity.Role;
import com.skulkina.url_shortener_api.entity.User;
import com.skulkina.url_shortener_api.exception.LinkExpiredException;
import com.skulkina.url_shortener_api.exception.LinkNotFoundException;
import com.skulkina.url_shortener_api.mapper.LinkMapper;
import com.skulkina.url_shortener_api.repository.LinkRepository;
import com.skulkina.url_shortener_api.repository.UserRepository;
import com.skulkina.url_shortener_api.security.CurrentUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;


@Slf4j
@Service
@RequiredArgsConstructor
public class LinkService {
    private final LinkRepository linkRepository;
    private final ShortCodeGenerator shortCodeGenerator;
    private final CurrentUserService currentUserService;
    private final RedisTemplate<String, String> redisTemplate;

    private static final String CACHE_PREFIX = "short-url:";
    private static final Duration CACHE_TTL = Duration.ofMinutes(10);
    private final LinkMapper linkMapper;

    @Value("${app.base-url}")
    private String baseUrl;

    @Transactional
    public LinkResponse createLink(CreateLinkRequest request) {
        User user = currentUserService.getCurrentUser();

        String shortCode = generateUniqueShortCode();

        Link link = Link.builder()
                .originalUrl(request.originalUrl())
                .shortCode(shortCode)
                .clickCount(0L)
                .createdAt(LocalDateTime.now())
                .expiresAt(request.expiresAt())
                .user(user)
                .build();

        Link savedLink = linkRepository.save(link);

        return linkMapper.toResponse(savedLink, baseUrl);
    }

    @Transactional
    public String getOriginalUrlAndIncrementClickCount(String shortCode) {
        String cacheKey = CACHE_PREFIX + shortCode;

         String cachedUrl = redisTemplate.opsForValue().get(cacheKey);
         if (cachedUrl != null) {
            log.info("Cache hit for key: {}", cacheKey);
            return cachedUrl;
        }

         Link link = linkRepository.findByShortCode(shortCode)
                .orElseThrow(() -> new LinkNotFoundException(shortCode));

        link.setClickCount(link.getClickCount() + 1);

        redisTemplate.opsForValue().set(cacheKey, link.getOriginalUrl(), CACHE_TTL);
        String checkValue = redisTemplate.opsForValue().get(cacheKey);
        Long ttl = redisTemplate.getExpire(cacheKey);
        if (link.getExpiresAt() != null && link.getExpiresAt().isBefore(LocalDateTime.now())) {
            redisTemplate.delete(cacheKey);
            throw new LinkExpiredException(shortCode);
        }

        log.info("Saved to Redis. key={}, value={}, ttl={}", cacheKey, checkValue, ttl);

        return link.getOriginalUrl();
    }

    private String generateUniqueShortCode() {
        String shortCode;

        do {
            shortCode = shortCodeGenerator.generate();
        } while (linkRepository.existsByShortCode(shortCode));

        return shortCode;
    }

     @Transactional(readOnly = true)
    public List<LinkResponse> getCurrentUserLinks() {
        User user = currentUserService.getCurrentUser();

        return linkRepository.findAllByUserOrderByCreatedAtDesc(user)
                .stream()
                .map(link -> linkMapper.toResponse(link, baseUrl))
                .toList();
    }
    @Transactional
    public void deleteCurrentUserLink(Long id){
        User user = currentUserService.getCurrentUser();
        Link link = linkRepository.findById(id)
                .orElseThrow(() -> new LinkNotFoundException(id));
        if (!link.getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException("You do not have permission to delete this link");
        }
        String cacheKey = CACHE_PREFIX + link.getShortCode();
        linkRepository.delete(link);
        redisTemplate.delete(cacheKey);


    }

}
