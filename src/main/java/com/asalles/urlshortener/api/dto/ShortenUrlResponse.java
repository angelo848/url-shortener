package com.asalles.urlshortener.api.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import lombok.Builder;

@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record ShortenUrlResponse(String originalUrl, String shortenedUrl, String fullShortUrl) {
}
