package com.asalles.urlshortener.core.exception;

public class UrlNotFoundException extends RuntimeException {
  public UrlNotFoundException(String message) {
    super(message);
  }
}
