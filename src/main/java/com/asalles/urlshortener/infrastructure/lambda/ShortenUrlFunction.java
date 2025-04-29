package com.asalles.urlshortener.infrastructure.lambda;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.asalles.urlshortener.api.controller.UrlController;
import com.asalles.urlshortener.api.dto.ShortenUrlRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component("shortenUrlFunction")
@Slf4j
@RequiredArgsConstructor
public class ShortenUrlFunction implements Function<APIGatewayProxyRequestEvent, Mono<APIGatewayProxyResponseEvent>> {

  private final ObjectMapper objectMapper;

  private final UrlController urlController;

  @Override
  public Mono<APIGatewayProxyResponseEvent> apply(APIGatewayProxyRequestEvent apiGatewayProxyRequestEvent) {
    if (!HttpMethod.POST.name().equals(apiGatewayProxyRequestEvent.getHttpMethod())) {
      return Mono.just(new APIGatewayProxyResponseEvent()
        .withStatusCode(400)
        .withBody("Invalid HTTP method. Only POST is supported for the resource: ." + getClass().getSimpleName()));
    }
    try {
      ShortenUrlRequest shortenUrlRequest = objectMapper.readValue(apiGatewayProxyRequestEvent.getBody(), ShortenUrlRequest.class);

      ServerHttpRequest serverHttpRequest = ServerHttpRequestFactory.create(apiGatewayProxyRequestEvent);

      return urlController.shortenUrl(shortenUrlRequest, serverHttpRequest)
        .map(response -> new APIGatewayProxyResponseEvent()
          .withStatusCode(response.getStatusCode().value())
          .withBody(convertToJson(response.getBody())))
        .onErrorResume(e -> {
          log.error("Error in shorten url lambda function: {}", e.getMessage(), e);
          return Mono.just(new APIGatewayProxyResponseEvent()
            .withStatusCode(500)
            .withBody("Error: " + e.getMessage()));
        });
    } catch (Exception e) {
      log.error("Error processing request: {}", e.getMessage(), e);
      return Mono.just(new APIGatewayProxyResponseEvent()
        .withStatusCode(500)
        .withBody("Internal server error: " + e.getMessage()));
    }
  }

  private String convertToJson(Object object) {
    try {
      return objectMapper.writeValueAsString(object);
    } catch (Exception e) {
      log.error("Error converting to JSON: {}", e.getMessage());
      return "{}";
    }
  }
}
