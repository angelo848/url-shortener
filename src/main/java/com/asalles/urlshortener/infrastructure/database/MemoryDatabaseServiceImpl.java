package com.asalles.urlshortener.infrastructure.database;

import com.asalles.urlshortener.application.service.StorageService;
import com.asalles.urlshortener.core.entity.UrlMapping;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@Repository
@Slf4j
public class MemoryDatabaseServiceImpl implements StorageService {

  private final Map<String, UrlMapping> database = new ConcurrentHashMap<>();

  @Override
  public UrlMapping getUrlMapping(String shortCode) {
    return database.get(shortCode);
  }

  @Override
  public UrlMapping findUrlMappingByOriginalUrl(String originalUrl) {
    return database.values().stream()
      .filter(mapping -> mapping.getOriginalUrl().equals(originalUrl))
      .findFirst()
      .orElse(null);
  }

  @Override
  public void storeUrlMapping(UrlMapping urlMapping) {
    log.info("Storing URL mapping in memory database: {}", urlMapping);
    database.put(urlMapping.getShortenedUrl(), urlMapping);
  }

  @Override
  public void deleteUrlMapping(UrlMapping urlMapping) {
    database.remove(urlMapping.getShortenedUrl());
  }
}
