package com.asalles.urlshortener.infrastructure.lambda;

import com.asalles.urlshortener.api.dto.RedirectUrlRequest;
import com.asalles.urlshortener.api.dto.ShortenUrlRequest;
import com.asalles.urlshortener.application.util.UrlUtil;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.Objects;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.server.RequestPath;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.util.MultiValueMap;
import reactor.core.publisher.Flux;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ServerHttpRequestFactory {

  public static ServerHttpRequest create(RedirectUrlRequest request) {
    return new MinimalServerHttpRequest(request);
  }

  public static ServerHttpRequest create(ShortenUrlRequest request) {
    return new MinimalServerHttpRequest(request);
  }

  private static class MinimalServerHttpRequest implements ServerHttpRequest {
    private final URI uri;
    private final HttpHeaders headers;

    public MinimalServerHttpRequest(RedirectUrlRequest request) {
      try {
        this.uri = new URI(UrlUtil.getBaseUrlFromLambdaEvent(request));
        this.headers = new HttpHeaders();
        if (Objects.nonNull(request.headers())) {
          this.headers.setAll(request.headers());
        }
      } catch (URISyntaxException e) {
        throw new RuntimeException("Failed to create URI from RedirectUrlRequest", e);
      }
    }

    public MinimalServerHttpRequest(ShortenUrlRequest request) {
      try {
        this.uri = new URI(UrlUtil.getBaseUrlFromLambdaEvent(request));
        this.headers = new HttpHeaders();
        if (request.headers() != null) {
          this.headers.setAll(request.headers());
        }
      } catch (URISyntaxException e) {
        throw new RuntimeException("Failed to create URI from ShortenUrlRequest", e);
      }
    }

    @Override
    public URI getURI() {
      return uri;
    }

    @Override
    public Map<String, Object> getAttributes() {
      return Map.of();
    }

    // Minimal implementations of required methods
    @Override public String getId() { return "lambda-request"; }

    @Override
    public RequestPath getPath() {
      return null;
    }

    @Override
    public MultiValueMap<String, String> getQueryParams() {
      return null;
    }

    @Override
    public MultiValueMap<String, HttpCookie> getCookies() {
      return null;
    }

    @Override public HttpHeaders getHeaders() { return headers != null ? headers : new HttpHeaders(); }
    @Override public HttpMethod getMethod() { return null; }
    @Override public Flux<DataBuffer> getBody() { return Flux.empty(); }
  }
}
