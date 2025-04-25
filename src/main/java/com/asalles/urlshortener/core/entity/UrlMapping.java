package com.asalles.urlshortener.core.entity;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class UrlMapping {

  private String originalUrl;

  private String shortenedUrl;

  private LocalDateTime createdAt;

  private LocalDateTime expiresAt;

  private Long accessCount;

  public void incrementAccessCount() {
    this.accessCount++;
  }
}
