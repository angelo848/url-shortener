package com.asalles.urlshortener.application.service;

import com.asalles.urlshortener.core.entity.UrlMapping;

public interface StorageService {

  UrlMapping getUrlMapping(String shortCode);

  UrlMapping findUrlMappingByOriginalUrl(String originalUrl);

  void storeUrlMapping(UrlMapping urlMapping);

  void deleteUrlMapping(UrlMapping urlMapping);
}
