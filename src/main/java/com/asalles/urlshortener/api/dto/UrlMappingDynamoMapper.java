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
    Map<String, AttributeValue> item = new HashMap<>();
    item.put(KEY, AttributeValue.builder().s(urlMapping.getShortenedUrl()).build());
    item.put("originalUrl", AttributeValue.builder().s(urlMapping.getOriginalUrl()).build());
    item.put("createdAt", AttributeValue.builder().s(urlMapping.getCreatedAt().toString()).build());
    item.put("expiresAt", AttributeValue.builder().s(urlMapping.getExpiresAt().toString()).build());
    item.put("accessCount", AttributeValue.builder().n(String.valueOf(urlMapping.getAccessCount())).build());

    return item;
  }

  public static UrlMapping fromDynamoItem(Map<String, AttributeValue> item, String shortCode) {
    if (item == null || item.isEmpty()) {
      return null;
    }

    return UrlMapping.builder()
      .shortenedUrl(shortCode)
      .originalUrl(item.get("originalUrl").s())
      .createdAt(LocalDateTime.parse(item.get("createdAt").s()))
      .expiresAt(LocalDateTime.parse(item.get("expiresAt").s()))
      .accessCount(Long.parseLong(item.get("accessCount").n()))
      .build();
  }
}