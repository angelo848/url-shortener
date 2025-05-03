package com.asalles.urlshortener.infrastructure.lambda;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.asalles.urlshortener.api.controller.UrlController;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component("redirectUrlFunction")
@Slf4j
@RequiredArgsConstructor
public class RedirectUrlFunction implements Function<APIGatewayProxyRequestEvent, Mono<APIGatewayProxyResponseEvent>> {

  private final ObjectMapper objectMapper;

  private final UrlController urlController;

  @Override
  public Mono<APIGatewayProxyResponseEvent> apply(APIGatewayProxyRequestEvent event) {
    if (!HttpMethod.GET.name().equals(event.getHttpMethod())) {
      return Mono.just(new APIGatewayProxyResponseEvent()
        .withStatusCode(400)
        .withBody("Invalid HTTP method. Only GET is supported for redirect url function."));
    }

    return urlController.redirectUrl(event.getPathParameters().get("shortenedUrl"), ServerHttpRequestFactory.create(event))
      .map(response -> new APIGatewayProxyResponseEvent()
        .withStatusCode(response.getStatusCode().value())
        .withHeaders(response.getHeaders().toSingleValueMap())
        .withBody(String.valueOf(response.getBody()))
      )
      .onErrorResume(e -> {
        log.error("Error processing request: {}", e.getMessage(), e);
        return Mono.just(new APIGatewayProxyResponseEvent()
          .withStatusCode(500)
          .withBody("Internal server error: " + e.getMessage()));
      });
  }
}
