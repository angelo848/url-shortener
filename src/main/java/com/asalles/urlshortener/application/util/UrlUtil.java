package com.asalles.urlshortener.application.util;

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
}