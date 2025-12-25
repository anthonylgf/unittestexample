package com.example.unittestexample.configs;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@ConfigurationProperties(prefix = "unittestexample.config.kafka")
@EnableConfigurationProperties
@Getter
@Setter
public class KafkaTopicConfig {

  private Kafka kafka = new Kafka();

  @Getter
  @Setter
  public static class Kafka {
    private Topics topics = new Topics();
  }

  @Getter
  @Setter
  public static class Topics {
    private String unittestexampleAluno;
  }
}
