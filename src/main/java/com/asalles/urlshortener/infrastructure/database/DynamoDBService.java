package com.asalles.urlshortener.infrastructure.database;

import com.asalles.urlshortener.api.dto.UrlMappingDynamoMapper;
import com.asalles.urlshortener.application.service.StorageService;
import com.asalles.urlshortener.core.entity.UrlMapping;
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
import software.amazon.awssdk.services.dynamodb.model.QueryRequest;
import software.amazon.awssdk.services.dynamodb.model.QueryResponse;

@Slf4j
@Repository
@Primary
public class DynamoDBService implements StorageService {

  private static final String KEY = "shortCode";
  private static final String ORIGINAL_URL_GSI = "originalUrl-index";

  private final String tableName;
  private final String region;
  private final DynamoDbClient dynamoDbClient;

  public DynamoDBService(
      @Value("${aws.dynamo.table-name:url-shortener}") String tableName,
      @Value("${aws.dynamo.region:us-east-1}") String region,
      @Value("${aws.access-key}") String accessKey,
      @Value("${aws.secret-key}") String secretKey) {
    this.tableName = tableName;
    this.region = region;
    this.dynamoDbClient = DynamoDbClient.builder()
      .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKey, secretKey)))
      .region(Region.of(this.region))
      .build();
  }

  @Override
  public UrlMapping getUrlMapping(String shortCode) {
    GetItemRequest request = GetItemRequest.builder()
      .tableName(tableName)
      .key(Map.of(KEY, AttributeValue.builder().s(shortCode).build()))
      .build();

    GetItemResponse urlItem = dynamoDbClient.getItem(request);
    if (urlItem.hasItem()) {
      return UrlMappingDynamoMapper.fromDynamoItem(urlItem.item(), shortCode);
    }

    return null;
  }

  @Override
  public UrlMapping findUrlMappingByOriginalUrl(String originalUrl) {
    try {
      QueryRequest queryRequest = QueryRequest.builder()
        .tableName(tableName)
        .indexName(ORIGINAL_URL_GSI)
        .keyConditionExpression("originalUrl = :originalUrl")
        .expressionAttributeValues(Map.of(
          ":originalUrl", AttributeValue.builder().s(originalUrl).build()
        ))
        .limit(1)
        .build();

      QueryResponse queryResponse = dynamoDbClient.query(queryRequest);

      if (queryResponse.hasItems() && !queryResponse.items().isEmpty()) {
        Map<String, AttributeValue> item = queryResponse.items().get(0);
        String shortCode = item.get(KEY).s();
        return UrlMappingDynamoMapper.fromDynamoItem(item, shortCode);
      }
      
      return null;
    } catch (Exception e) {
      log.error("Error querying DynamoDB GSI for originalUrl: {}", originalUrl, e);
      return null;
    }
  }

  @Override
  public void storeUrlMapping(UrlMapping urlMapping) {
    Map<String, AttributeValue> item = UrlMappingDynamoMapper.toDynamoItem(urlMapping);

    PutItemRequest request = PutItemRequest.builder()
      .tableName(tableName)
      .item(item)
      .build();

    dynamoDbClient.putItem(request);
  }

  @Override
  public void deleteUrlMapping(UrlMapping urlMapping) {
    DeleteItemRequest request = DeleteItemRequest.builder()
      .tableName(tableName)
      .key(Map.of(KEY, AttributeValue.builder().s(urlMapping.getShortenedUrl()).build()))
      .build();

    dynamoDbClient.deleteItem(request);
  }
}
