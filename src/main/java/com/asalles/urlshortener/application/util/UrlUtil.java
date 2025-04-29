package com.asalles.urlshortener.application.util;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;
import java.util.Random;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.http.server.reactive.ServerHttpRequest;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UrlUtil {

  private static final String HTTP = "http";
  private static final String HTTPS = "https";
  private static final String ALPHABET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

  public static String getBaseUrl(ServerHttpRequest request) {
    String scheme = request.getURI().getScheme();
    String host = request.getURI().getHost();
    int port = request.getURI().getPort();

    if ((HTTP.equals(scheme) && port == 80) || (HTTPS.equals(scheme) && port == 443)) {
      return scheme + "://" + host;
    }

    return scheme + "://" + host + ":" + port;
  }

  public static String generateShortCode() {
    UUID uuid = UUID.randomUUID();

    long mostSigBits = Math.abs(uuid.getMostSignificantBits());

    return toBase62(mostSigBits);
  }

  private static String toBase62(long value) {
    if (value == 0) {
      return String.valueOf(ALPHABET.charAt(0));
    }

    StringBuilder sb = new StringBuilder();
    while (value > 0) {
      sb.append(ALPHABET.charAt((int) (value % 62)));
      value /= 62;
    }

    StringBuilder result = sb.reverse();
    if (result.length() > 8) {
      return result.substring(0, 8);
    }

    Random random = new Random();
    while (result.length() < 6) {
      result.insert(0, ALPHABET.charAt(random.nextInt(62)));
    }

    return result.toString();
  }

  public static String getBaseUrlFromLambdaEvent(APIGatewayProxyRequestEvent event) {
    if (event == null) {
      throw new IllegalArgumentException("API Gateway event cannot be null");
    }

    // Get headers safely
    Map<String, String> headers = event.getHeaders();
    headers = headers != null ? headers : Collections.emptyMap();

    // Determine host - check X-Forwarded-Host first (for proxies), then Host header
    String host = headers.getOrDefault("X-Forwarded-Host",
      headers.getOrDefault("Host", "localhost"));

    // Determine scheme - check X-Forwarded-Proto first, default to https
    String scheme = headers.getOrDefault("X-Forwarded-Proto", "https");

    // Get port if specified in headers
    String portStr = headers.get("X-Forwarded-Port");
    Integer port = null;
    if (portStr != null) {
      try {
        port = Integer.parseInt(portStr);
      } catch (NumberFormatException ignored) {
        // Invalid port format, will use default
      }
    }

    // Handle stage if present
    String stagePath = "";
    if (event.getRequestContext() != null) {
      String stage = event.getRequestContext().getStage();
      if (stage != null && !stage.isEmpty() && !stage.equals("$default")) {
        stagePath = "/" + stage;
      }
    }

    // Build the URL with optional port
    StringBuilder baseUrl = new StringBuilder(scheme).append("://").append(host);

    // Add port if needed (and not the default for the scheme)
    if (port != null && !((scheme.equals("http") && port == 80) || (scheme.equals("https") && port == 443))) {
      baseUrl.append(":").append(port);
    }

    return baseUrl.append(stagePath).toString();
  }
}