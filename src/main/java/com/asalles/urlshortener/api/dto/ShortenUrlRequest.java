package com.asalles.urlshortener.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record ShortenUrlRequest(@NotBlank(message = "URL cannot be empty")
                                @Pattern(regexp = "^(https?|ftp)://[^\\s/$.?#].[^\\s]*$", message = "Invalid URL format")
                                String url) {
}

