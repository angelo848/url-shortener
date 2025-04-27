package com.asalles.urlshortener.infrastructure.lambda;

import java.util.Arrays;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.util.ObjectUtils;

@Slf4j
@SpringBootApplication
public class UrlLambdaHandler {

  public static void main(String[] args){
    log.info("==> Starting: LambdaApplication");
    if (!ObjectUtils.isEmpty(args)) {
      log.info("==>  args: {}", Arrays.asList(args));
    }
    SpringApplication.run(UrlLambdaHandler.class, args);
  }
}
