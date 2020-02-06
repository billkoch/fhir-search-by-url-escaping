package com.example;

import ca.uhn.fhir.rest.client.api.IClientInterceptor;
import ca.uhn.fhir.rest.client.interceptor.LoggingInterceptor;
import com.google.common.escape.Escaper;
import com.google.common.net.PercentEscaper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class Config {

  @Bean
  Escaper hackyEscaper() {
    return new PercentEscaper("-_.*", false);
  }

  @Bean
  IClientInterceptor clientLoggingInterceptor() {
    return new LoggingInterceptor(true);
  }
}
