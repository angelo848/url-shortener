package com.asalles.urlshortener.infrastructure.lambda;

import java.util.function.Function;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class UrlLambdaHandler {

  public static void main(String[] args){
  }

  @Bean
  public Function<String, String> urlShortenerLambda() {
    return value -> value.toUpperCase();
  }
}