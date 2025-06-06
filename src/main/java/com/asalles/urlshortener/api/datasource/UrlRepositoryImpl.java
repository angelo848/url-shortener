package com.asalles.urlshortener.api.datasource;

import com.asalles.urlshortener.application.service.StorageService;
import com.asalles.urlshortener.core.entity.UrlMapping;
import com.asalles.urlshortener.core.repository.UrlRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Component
@RequiredArgsConstructor
public class UrlRepositoryImpl implements UrlRepository {

//  private final CacheService cacheService;
//
  private final StorageService storageService;

  @Override
  public Mono<Void> save(UrlMapping urlMapping) {
    return Mono.fromCallable(() -> {
      storageService.storeUrlMapping(urlMapping);
      return null;
    })
    .subscribeOn(Schedulers.boundedElastic())
    .then();
  }

  @Override
  public Mono<Optional<UrlMapping>> findByShortenedUrl(String shortenedUrl) {
    return Mono.just(Optional.ofNullable(storageService.getUrlMapping(shortenedUrl)));
  }

  @Override
  public Mono<Optional<UrlMapping>> findByOriginalUrl(String originalUrl) {
    return Mono.just(Optional.ofNullable(storageService.findUrlMappingByOriginalUrl(originalUrl)));
  }

  @Override
  public Mono<Void> delete(UrlMapping urlMapping) {
    return Mono.fromRunnable(() ->
      storageService.deleteUrlMapping(urlMapping));
  }

  @Override
  public Mono<Void> incrementAccessCount(UrlMapping urlMapping) {
    return Mono.fromRunnable(() -> {
      urlMapping.incrementAccessCount();
      storageService.storeUrlMapping(urlMapping);
    });
  }
}
