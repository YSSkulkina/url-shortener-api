package com.skulkina.url_shortener_api.mapper;

import com.skulkina.url_shortener_api.dto.LinkResponse;
import com.skulkina.url_shortener_api.entity.Link;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface LinkMapper {
    @Mapping(target = "id", source = "link.id")
    @Mapping(target = "originalUrl", source = "link.originalUrl")
    @Mapping(target = "shortCode", source = "link.shortCode")
    @Mapping(target = "clickCount", source = "link.clickCount")
    @Mapping(target = "createdAt", source = "link.createdAt")
    @Mapping(target = "expiresAt", source = "link.expiresAt")
    @Mapping(target = "shortUrl", expression = "java(baseUrl + \"/\" + link.getShortCode())")
    LinkResponse toResponse(Link link, String baseUrl);

}
