package com.asalles.urlshortener.application.service;

import com.asalles.urlshortener.core.entity.UrlMapping;

public interface CacheService {

  UrlMapping getUrlMapping(String shortCode);

  void storeUrlMapping(UrlMapping urlMapping);
}
