package com.skulkina.url_shortener_api.service;

import com.skulkina.url_shortener_api.dto.CreateLinkRequest;
import com.skulkina.url_shortener_api.dto.LinkResponse;
import com.skulkina.url_shortener_api.entity.Link;
import com.skulkina.url_shortener_api.entity.Role;
import com.skulkina.url_shortener_api.entity.User;
import com.skulkina.url_shortener_api.exception.LinkNotFoundException;
import com.skulkina.url_shortener_api.repository.LinkRepository;
import com.skulkina.url_shortener_api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
@Slf4j
@Service
@RequiredArgsConstructor
public class LinkService {
    private final LinkRepository linkRepository;
    private final UserRepository userRepository;
    private final ShortCodeGenerator shortCodeGenerator;
    private final RedisTemplate<String, String> redisTemplate;

    private static final String CACHE_PREFIX = "short-url:";
    private static final Duration CACHE_TTL = Duration.ofMinutes(10);

    @Value("${app.base-url}")
    private String baseUrl;

    @Transactional
    public LinkResponse createLink(CreateLinkRequest request) {
        // Временный пользователь, пока не подключили JWT Filter
        User user = userRepository.findByEmail("demo@demo.com")
                .orElseGet(() -> userRepository.save(
                        User.builder()
                                .email("demo@demo.com")
                                .password("demo")
                                .role(Role.USER)
                                .createdAt(LocalDateTime.now())
                                .build()
                ));

        String shortCode = generateUniqueShortCode();

        Link link = Link.builder()
                .originalUrl(request.originalUrl())
                .shortCode(shortCode)
                .clickCount(0L)
                .createdAt(LocalDateTime.now())
                .user(user)
                .build();

        Link savedLink = linkRepository.save(link);

        return toResponse(savedLink);
    }

    @Transactional
    public String getOriginalUrlAndIncrementClickCount(String shortCode) {
        String cacheKey = CACHE_PREFIX + shortCode;

        log.info("Searching in Redis by key: {}", cacheKey);

        String cachedUrl = redisTemplate.opsForValue().get(cacheKey);
        log.info("Redis GET result. key={}, value={}", cacheKey, cachedUrl);
        if (cachedUrl != null) {
            log.info("Cache hit for key: {}", cacheKey);
            return cachedUrl;
        }

        log.info("Cache miss. Searching in PostgreSQL by shortCode: {}", shortCode);

        Link link = linkRepository.findByShortCode(shortCode)
                .orElseThrow(() -> new LinkNotFoundException(shortCode));

        link.setClickCount(link.getClickCount() + 1);

        redisTemplate.opsForValue().set(cacheKey, link.getOriginalUrl(), CACHE_TTL);
        String checkValue = redisTemplate.opsForValue().get(cacheKey);
        Long ttl = redisTemplate.getExpire(cacheKey);

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

    private LinkResponse toResponse(Link link) {
        return new LinkResponse(
                link.getId(),
                link.getOriginalUrl(),
                link.getShortCode(),
                baseUrl + "/" + link.getShortCode(),
                link.getClickCount(),
                link.getCreatedAt()
        );
    }

}
