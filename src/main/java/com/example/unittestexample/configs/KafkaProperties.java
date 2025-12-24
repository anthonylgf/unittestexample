package com.example.unittestexample.configs;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "unittestexample.config.kafka")
@Getter
@Setter
public class KafkaProperties {

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
