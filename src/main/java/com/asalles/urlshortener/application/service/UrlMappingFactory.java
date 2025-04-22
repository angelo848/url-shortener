package com.asalles.urlshortener.application.service;

import com.asalles.urlshortener.core.entity.UrlMapping;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UrlMappingFactory {

  public static UrlMapping createUrlMapping(String originalUrl, String shortCode, int expirationDays) {
    LocalDateTime now = LocalDateTime.now();

    return new UrlMapping(originalUrl, shortCode, now, now.plusDays(expirationDays), 0L);
  }
}
