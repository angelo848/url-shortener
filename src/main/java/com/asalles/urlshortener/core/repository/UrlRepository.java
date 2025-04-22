package com.asalles.urlshortener.core.repository;

import com.asalles.urlshortener.core.entity.UrlMapping;
import java.util.Optional;
import reactor.core.publisher.Mono;

public interface UrlRepository {

  Mono<Void> save(UrlMapping urlMapping);

  Mono<Optional<UrlMapping>> findByShortenedUrl(String shortenedUrl);

  Mono<Optional<UrlMapping>> findByOriginalUrl(String originalUrl);

  Mono<Void> delete(UrlMapping urlMapping);

  Mono<Void> incrementAccessCount(UrlMapping urlMapping);
}
