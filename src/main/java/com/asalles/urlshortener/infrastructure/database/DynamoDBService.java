package com.asalles.urlshortener.infrastructure.database;

import com.asalles.urlshortener.application.service.StorageService;
import com.asalles.urlshortener.core.entity.UrlMapping;
import java.time.LocalDateTime;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.DeleteItemRequest;
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest;
import software.amazon.awssdk.services.dynamodb.model.GetItemResponse;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;

@Slf4j
@Repository
@Primary
public class DynamoDBService implements StorageService {

  private static final String KEY = "shortCode";

  private final String TABLE_NAME;

  private final String region;

  private final DynamoDbClient dynamoDbClient;

  public DynamoDBService(
      @Value("${aws.dynamo.table-name:url-shortener}") String tableName,
      @Value("${aws.dynamo.region:us-east-1}") String region,
      @Value("${aws.access-key}") String accessKey,
      @Value("${aws.secret-key}") String secretKey) {
    this.TABLE_NAME = tableName;
    this.region = region;
    this.dynamoDbClient = DynamoDbClient.builder()
      .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKey, secretKey)))
      .region(Region.of(this.region))
      .build();
  }

  @Override
  public UrlMapping getUrlMapping(String shortCode) {
    GetItemRequest request = GetItemRequest.builder()
      .tableName(TABLE_NAME)
      .key(Map.of(KEY, AttributeValue.builder().s(shortCode).build()))
      .build();

    GetItemResponse urlItem = dynamoDbClient.getItem(request);
    if (urlItem.hasItem()) {
      Map<String, AttributeValue> item = urlItem.item();
      return UrlMapping.builder()
        .originalUrl(item.get("urlMapping").m().get("originalUrl").s())
        .shortenedUrl(shortCode)
        .createdAt(LocalDateTime.parse(item.get("urlMapping").m().get("createdAt").s()))
        .expiresAt(LocalDateTime.parse(item.get("urlMapping").m().get("expiresAt").s()))
        .accessCount(Long.valueOf(item.get("urlMapping").m().get("accessCount").n()))
        .build();
    }

    return null;
  }

  @Override
  public UrlMapping findUrlMappingByOriginalUrl(String originalUrl) {
    // TODO: analyze if add a secondary index or remove this method as DynamoDB does not support queries by non-key attributes
    return null;
  }

  @Override
  public void storeUrlMapping(UrlMapping urlMapping) {
    Map<String, AttributeValue> item = Map.of(
      KEY, AttributeValue.builder().s(urlMapping.getShortenedUrl()).build(),
      "urlMapping", AttributeValue.builder().m(Map.of(
        "originalUrl", AttributeValue.builder().s(urlMapping.getOriginalUrl()).build(),
        "createdAt", AttributeValue.builder().s(urlMapping.getCreatedAt().toString()).build(),
        "expiresAt", AttributeValue.builder().s(urlMapping.getExpiresAt().toString()).build(),
        "accessCount", AttributeValue.builder().n(String.valueOf(urlMapping.getAccessCount())).build()
      )).build()
    );

    PutItemRequest request = PutItemRequest.builder()
      .tableName(TABLE_NAME)
      .item(item)
      .build();

    dynamoDbClient.putItem(request);
  }

  @Override
  public void deleteUrlMapping(UrlMapping urlMapping) {
    DeleteItemRequest request = DeleteItemRequest.builder()
      .tableName(TABLE_NAME)
      .key(Map.of(KEY, AttributeValue.builder().s(urlMapping.getShortenedUrl()).build()))
      .build();

    dynamoDbClient.deleteItem(request);
  }
}
