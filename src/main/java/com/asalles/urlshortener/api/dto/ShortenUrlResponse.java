package com.asalles.urlshortener.api.dto;

import lombok.Builder;

@Builder
public record ShortenUrlResponse(String originalUrl, String shortenedUrl, String fullShortUrl) {
}
