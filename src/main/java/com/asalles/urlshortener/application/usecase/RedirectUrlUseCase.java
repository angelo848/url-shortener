package com.asalles.urlshortener.application.usecase;

import com.asalles.urlshortener.core.entity.UrlMapping;
import com.asalles.urlshortener.core.exception.UrlNotFoundException;
import com.asalles.urlshortener.core.repository.UrlRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class RedirectUrlUseCase {

  private final UrlRepository urlRepository;

  public Mono<UrlMapping> execute(String shortenedUrl) {
    return urlRepository.findByShortenedUrl(shortenedUrl)
      .flatMap(optionalMapping -> optionalMapping
        .map(Mono::just)
        .orElseGet(() -> Mono.error(new UrlNotFoundException("URL not found for code: " + shortenedUrl)))
      )
      .flatMap(urlMapping -> urlRepository.incrementAccessCount(urlMapping)
        .thenReturn(urlMapping)
      )
      .onErrorResume(Exception.class, e -> {
        if (e instanceof UrlNotFoundException) {
          return Mono.error(e);
        }
        return Mono.error(new RuntimeException("Error processing URL redirect", e));
      });
  }
}
