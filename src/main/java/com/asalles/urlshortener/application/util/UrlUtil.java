package com.asalles.urlshortener.application.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.http.server.reactive.ServerHttpRequest;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UrlUtil {

  private static final String HTTP = "http";
  private static final String HTTPS = "https";

  public static String getBaseUrl(ServerHttpRequest request) {
    String scheme = request.getURI().getScheme();
    String host = request.getURI().getHost();
    int port = request.getURI().getPort();

    if ((HTTP.equals(scheme) && port == 80) || (HTTPS.equals(scheme) && port == 443)) {
      return scheme + "://" + host;
    }

    return scheme + "://" + host + ":" + port;
  }
}