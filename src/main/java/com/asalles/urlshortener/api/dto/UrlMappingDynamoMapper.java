package com.asalles.urlshortener.api.dto;

import com.asalles.urlshortener.core.entity.UrlMapping;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UrlMappingDynamoMapper {

  private static final String KEY = "shortCode";

  public static Map<String, AttributeValue> toDynamoItem(UrlMapping urlMapping) {
    Map<String, AttributeValue> attributes = new HashMap<>();
    attributes.put("originalUrl", AttributeValue.builder().s(urlMapping.getOriginalUrl()).build());
    attributes.put("createdAt", AttributeValue.builder().s(urlMapping.getCreatedAt().toString()).build());
    attributes.put("expiresAt", AttributeValue.builder().s(urlMapping.getExpiresAt().toString()).build());
    attributes.put("accessCount", AttributeValue.builder().n(String.valueOf(urlMapping.getAccessCount())).build());

    return Map.of(
      KEY, AttributeValue.builder().s(urlMapping.getShortenedUrl()).build(),
      "urlMapping", AttributeValue.builder().m(attributes).build()
    );
  }

  public static UrlMapping fromDynamoItem(Map<String, AttributeValue> item, String shortCode) {
    if (item == null || item.isEmpty()) {
      return null;
    }
    Map<String, AttributeValue> urlMapping = item.get("urlMapping").m();

    return UrlMapping.builder()
      .shortenedUrl(shortCode)
      .originalUrl(urlMapping.get("originalUrl").s())
      .createdAt(LocalDateTime.parse(urlMapping.get("createdAt").s()))
      .expiresAt(LocalDateTime.parse(urlMapping.get("expiresAt").s()))
      .accessCount(Long.parseLong(urlMapping.get("accessCount").n()))
      .build();
  }
}