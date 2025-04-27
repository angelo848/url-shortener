package com.asalles.urlshortener.infrastructure.lambda;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import java.util.function.Function;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class TestFunction {

  @Bean
  public Function<APIGatewayProxyRequestEvent, String> apply() {
    return request -> "Hello, " + request.getBody();
  }
}
