package com.asalles.urlshortener.api.controller;

import com.asalles.urlshortener.api.dto.ShortenUrlRequest;
import com.asalles.urlshortener.api.dto.ShortenUrlResponse;
import com.asalles.urlshortener.application.usecase.RedirectUrlUseCase;
import com.asalles.urlshortener.application.usecase.ShortenUrlUseCase;
import com.asalles.urlshortener.application.util.UrlUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@Slf4j
public class UrlController {

  private final RedirectUrlUseCase redirectUrlUseCase;

  private final ShortenUrlUseCase shortenUrlUseCase;

  @PostMapping("/shorten")
  public Mono<ResponseEntity<ShortenUrlResponse>> shortenUrl(
    @Valid @RequestBody ShortenUrlRequest request,
    ServerHttpRequest serverRequest) {

    return shortenUrlUseCase.execute(request.url())
      .map(shortenedUrl -> {
        String baseUrl = UrlUtil.getBaseUrl(serverRequest);
        String fullShortUrl = baseUrl + "/" + shortenedUrl;
        return ResponseEntity.ok(ShortenUrlResponse.builder()
          .originalUrl(request.url())
          .shortenedUrl(shortenedUrl)
          .fullShortUrl(fullShortUrl)
          .build());
      })
      .onErrorResume(e -> {
        log.error("Error shortening URL: {}", e.getMessage());
        return Mono.just(ResponseEntity.badRequest()
          .body(ShortenUrlResponse.builder()
            .originalUrl(request.url())
            .shortenedUrl("Error")
            .fullShortUrl("Error")
            .build()));
      });
  }
}
