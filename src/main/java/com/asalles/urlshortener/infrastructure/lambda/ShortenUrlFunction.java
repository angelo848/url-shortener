package com.asalles.urlshortener.infrastructure.lambda;

import com.asalles.urlshortener.api.controller.UrlController;
import com.asalles.urlshortener.api.dto.ShortenUrlRequest;
import com.asalles.urlshortener.api.dto.ShortenUrlResponse;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component("shortenUrlFunction")
@Slf4j
@RequiredArgsConstructor
public class ShortenUrlFunction implements Function<ShortenUrlRequest, Mono<ShortenUrlResponse>> {

  private final UrlController urlController;

  @Override
  public Mono<ShortenUrlResponse> apply(ShortenUrlRequest request) {
    return urlController.shortenUrl(request, ServerHttpRequestFactory.create(request))
      .map(ResponseEntity::getBody)
      .onErrorResume(e -> {
        log.error("Error in shorten url lambda function: {}", e.getMessage(), e);
        return Mono.just(ShortenUrlResponse.builder()
          .originalUrl("Internal server error: " + e.getMessage())
          .shortenedUrl(null)
          .fullShortUrl(null)
          .build());
      });
  }
}
