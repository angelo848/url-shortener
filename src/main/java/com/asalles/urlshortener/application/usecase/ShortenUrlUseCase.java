package com.asalles.urlshortener.application.usecase;

import com.asalles.urlshortener.application.service.UrlMappingFactory;
import com.asalles.urlshortener.application.util.UrlUtil;
import com.asalles.urlshortener.core.entity.UrlMapping;
import com.asalles.urlshortener.core.repository.UrlRepository;
import java.net.URI;
import java.net.URISyntaxException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.util.InvalidUrlException;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class ShortenUrlUseCase {

  private final UrlRepository urlRepository;

  public Mono<String> execute(String originalUrl) {
    try {
      new URI(originalUrl);
    } catch (URISyntaxException e) {
      return Mono.error(new InvalidUrlException("Invalid URL format"));
    }

    return urlRepository.findByOriginalUrl(originalUrl)
      .flatMap(optUrlMapping -> {
        if (optUrlMapping.isPresent()) {
          return Mono.just(optUrlMapping.get().getShortenedUrl());
        }

        String shortenedUrl = UrlUtil.generateShortCode();
        UrlMapping mapping = UrlMappingFactory.createUrlMapping(originalUrl, shortenedUrl, 30);

        return urlRepository.save(mapping)
          .thenReturn(shortenedUrl);
      });
  }
}
