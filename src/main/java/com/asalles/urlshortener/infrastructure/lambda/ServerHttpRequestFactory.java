package com.asalles.urlshortener.infrastructure.lambda;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.asalles.urlshortener.application.util.UrlUtil;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
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

  public static ServerHttpRequest create(APIGatewayProxyRequestEvent event) {
    return new MinimalServerHttpRequest(event);
  }

  private static class MinimalServerHttpRequest implements ServerHttpRequest {
    private final URI uri;

    public MinimalServerHttpRequest(APIGatewayProxyRequestEvent event) {
      try {
        String baseUrlFromLambdaEvent = UrlUtil.getBaseUrlFromLambdaEvent(event);

        this.uri = new URI(baseUrlFromLambdaEvent);
      } catch (URISyntaxException e) {
        throw new RuntimeException("Failed to create URI from Lambda event", e);
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

    @Override public HttpHeaders getHeaders() { return new HttpHeaders(); }
    @Override public HttpMethod getMethod() { return null; }
    @Override public Flux<DataBuffer> getBody() { return Flux.empty(); }
  }
}
