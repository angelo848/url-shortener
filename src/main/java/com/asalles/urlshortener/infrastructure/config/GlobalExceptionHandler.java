package com.asalles.urlshortener.infrastructure.config;

import com.asalles.urlshortener.core.exception.ErrorResponseDTO;
import com.asalles.urlshortener.core.exception.UrlNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import reactor.core.publisher.Mono;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(UrlNotFoundException.class)
  public Mono<ResponseEntity<ErrorResponseDTO>> handleUrlNotFoundException(UrlNotFoundException ex) {
    ErrorResponseDTO errorResponse = new ErrorResponseDTO(HttpStatus.NOT_FOUND.value(), ex.getMessage());
    return Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse));
  }
}
