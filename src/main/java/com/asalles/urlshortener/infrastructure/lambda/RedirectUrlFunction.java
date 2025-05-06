package com.asalles.urlshortener.infrastructure.lambda;

import com.asalles.urlshortener.api.controller.UrlController;
import com.asalles.urlshortener.api.dto.RedirectUrlRequest;
import com.asalles.urlshortener.api.dto.ShortenUrlResponse;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component("redirectUrlFunction")
@Slf4j
@RequiredArgsConstructor
public class RedirectUrlFunction implements Function<RedirectUrlRequest, Mono<ShortenUrlResponse>> {

  private final UrlController urlController;

  @Override
  public Mono<ShortenUrlResponse> apply(RedirectUrlRequest request) {
    return urlController.redirectUrl(request.shortenedUrl(), ServerHttpRequestFactory.create(request))
      .map(ResponseEntity::getBody)
      .onErrorResume(e -> {
        log.error("Error processing request: {}", e.getMessage(), e);
        return Mono.just(new ShortenUrlResponse(
          "Internal server error: " + e.getMessage(),
          null,
          null));
      });
  }
}
