package com.asalles.urlshortener.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import java.util.Map;

public record ShortenUrlRequest(
    @NotBlank(message = "URL cannot be empty")
    @Pattern(regexp = "^(https?|ftp)://[^\\s/$.?#].[^\\s]*$", message = "Invalid URL format")
    String url,
    Map<String, String> headers,
    String scheme,
    String host,
    Integer port,
    String stage
) {}

