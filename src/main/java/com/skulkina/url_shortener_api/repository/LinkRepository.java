package com.skulkina.url_shortener_api.repository;

import com.skulkina.url_shortener_api.entity.Link;
import com.skulkina.url_shortener_api.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LinkRepository extends JpaRepository<Link, Long> {
    Optional<Link> findByShortCode(String shortCode);

    boolean existsByShortCode(String shortCode);

    List<Link> findAllByUser(User user);

}
