package com.asalles.urlshortener.api.controller;

import com.asalles.urlshortener.api.dto.ShortenUrlRequest;
import com.asalles.urlshortener.api.dto.ShortenUrlResponse;
import com.asalles.urlshortener.api.dto.UrlMappingMapper;
import com.asalles.urlshortener.application.usecase.RedirectUrlUseCase;
import com.asalles.urlshortener.application.usecase.ShortenUrlUseCase;
import com.asalles.urlshortener.application.util.UrlUtil;
import com.asalles.urlshortener.core.exception.UrlNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
        return ResponseEntity.ok(UrlMappingMapper.toShortenUrlResponse(shortenedUrl, request.url(), baseUrl));
      })
      .onErrorResume(e -> {
        log.error("Error shortening URL: {}", e.getMessage());
        return Mono.error(new RuntimeException("Error processing redirect", e));
      });
  }

  @GetMapping("/{shortenedUrl}")
  public Mono<ResponseEntity<ShortenUrlResponse>> redirectUrl(@PathVariable String shortenedUrl,
                                                              ServerHttpRequest serverRequest) {
    return redirectUrlUseCase.execute(shortenedUrl)
      .map(urlMapping -> ResponseEntity.status(HttpStatus.FOUND)
        .header(HttpHeaders.LOCATION, urlMapping.getOriginalUrl())
        .body(UrlMappingMapper.toShortenUrlResponse(urlMapping, serverRequest)))
      .onErrorResume(e -> {
        log.error("Error redirecting URL: {}", e.getMessage());
        if (e instanceof UrlNotFoundException) {
          return Mono.error(e);
        }
        return Mono.error(new RuntimeException("Error processing redirect", e));
      });
  }
}
