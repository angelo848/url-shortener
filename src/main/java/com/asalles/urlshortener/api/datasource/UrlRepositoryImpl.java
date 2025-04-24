package com.asalles.urlshortener.api.datasource;

import com.asalles.urlshortener.application.service.CacheService;
import com.asalles.urlshortener.application.service.StorageService;
import com.asalles.urlshortener.core.entity.UrlMapping;
import com.asalles.urlshortener.core.repository.UrlRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class UrlRepositoryImpl implements UrlRepository {

//  private final CacheService cacheService;
//
  private final StorageService storageService;

  @Override
  public Mono<Void> save(UrlMapping urlMapping) {
    return Mono.just(urlMapping)
      .flatMap(mapping -> {
        UrlMapping existingMapping = storageService.findUrlMappingByOriginalUrl(mapping.getOriginalUrl());
        if (existingMapping == null) {
          storageService.storeUrlMapping(mapping);
        }
        return Mono.empty();
      });
  }

  @Override
  public Mono<Optional<UrlMapping>> findByShortenedUrl(String shortenedUrl) {
    return null;
  }

  @Override
  public Mono<Optional<UrlMapping>> findByOriginalUrl(String originalUrl) {
    return Mono.just(Optional.empty());
  }

  @Override
  public Mono<Void> delete(UrlMapping urlMapping) {
    return Mono.empty();
  }

  @Override
  public Mono<Void> incrementAccessCount(UrlMapping urlMapping) {
    return Mono.empty();
  }
}
