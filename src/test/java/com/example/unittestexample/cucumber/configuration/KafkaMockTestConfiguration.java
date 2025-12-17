package com.example.unittestexample.cucumber.configuration;

import static org.mockito.Mockito.mock;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.core.KafkaTemplate;

@Configuration
@Profile("test & !kafka")
public class KafkaMockTestConfiguration {

  @Bean
  public KafkaTemplate<String, Object> mockKafkaTemplate() {
    return mock(KafkaTemplate.class);
  }
}
