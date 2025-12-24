package com.example.unittestexample.configs;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "spring.kafka")
@Getter
@Setter
public class KafkaProducerProperties {

  private String bootstrapServers;
  private Producer producer = new Producer();

  @Getter
  @Setter
  public static class Producer {
    private String bootstrapServers;
  }
}
