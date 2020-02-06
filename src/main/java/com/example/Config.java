package com.example;

import ca.uhn.fhir.rest.client.api.IClientInterceptor;
import ca.uhn.fhir.rest.client.interceptor.LoggingInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class Config {

  @Bean
  IClientInterceptor clientLoggingInterceptor() {
    return new LoggingInterceptor(true);
  }
}
