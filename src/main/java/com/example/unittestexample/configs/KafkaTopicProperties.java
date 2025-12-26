package com.example.unittestexample.configs;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "unittestexample.config")
@Getter
@Setter
public class KafkaTopicProperties {

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
