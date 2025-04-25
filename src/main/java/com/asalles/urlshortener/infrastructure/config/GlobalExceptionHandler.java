package com.asalles.urlshortener.infrastructure.config;

import com.asalles.urlshortener.core.exception.ErrorResponseDTO;
import com.asalles.urlshortener.core.exception.UrlNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.util.InvalidUrlException;
import reactor.core.publisher.Mono;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(UrlNotFoundException.class)
  public Mono<ResponseEntity<ErrorResponseDTO>> handleUrlNotFoundException(UrlNotFoundException ex) {
    ErrorResponseDTO errorResponse = new ErrorResponseDTO(HttpStatus.NOT_FOUND.value(), ex.getMessage());
    return Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse));
  }

  @ExceptionHandler(InvalidUrlException.class)
  public Mono<ResponseEntity<ErrorResponseDTO>> handleInvalidUrlExceptionException(InvalidUrlException ex) {
    ErrorResponseDTO errorResponse = new ErrorResponseDTO(HttpStatus.NOT_FOUND.value(), ex.getMessage());
    return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse));
  }
}
