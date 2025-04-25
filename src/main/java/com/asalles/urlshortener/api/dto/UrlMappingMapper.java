package com.asalles.urlshortener.api.dto;

import com.asalles.urlshortener.application.util.UrlUtil;
import com.asalles.urlshortener.core.entity.UrlMapping;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.http.server.reactive.ServerHttpRequest;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UrlMappingMapper {

  /**
   * Maps a UrlMapping entity to a ShortenUrlResponse DTO with full URL details
   *
   * @param urlMapping the entity to map
   * @param serverRequest used to extract the base URL
   * @return the mapped response DTO
   */
  public static ShortenUrlResponse toShortenUrlResponse(UrlMapping urlMapping, ServerHttpRequest serverRequest) {
    String baseUrl = UrlUtil.getBaseUrl(serverRequest);
    String fullShortUrl = baseUrl + "/" + urlMapping.getShortenedUrl();

    return ShortenUrlResponse.builder()
      .originalUrl(urlMapping.getOriginalUrl())
      .shortenedUrl(urlMapping.getShortenedUrl())
      .fullShortUrl(fullShortUrl)
      .build();
  }

  public static ShortenUrlResponse toShortenUrlResponse(String shortUrl, String originalUrl, String baseUrl) {
    String fullShortUrl = baseUrl + "/" + shortUrl;
    return ShortenUrlResponse.builder()
      .originalUrl(originalUrl)
      .shortenedUrl(shortUrl)
      .fullShortUrl(fullShortUrl)
      .build();
  }
}