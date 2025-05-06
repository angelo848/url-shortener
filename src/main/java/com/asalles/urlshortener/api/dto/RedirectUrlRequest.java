package com.asalles.urlshortener.api.dto;

import java.util.Map;

public record RedirectUrlRequest(
    String shortenedUrl,
    Map<String, String> headers,
    String scheme,
    String host,
    Integer port,
    String stage
) {}
