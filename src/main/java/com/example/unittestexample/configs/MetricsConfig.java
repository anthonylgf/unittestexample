package com.example.unittestexample.configs;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.config.MeterFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MetricsConfig {

  @Bean
  MeterRegistryCustomizer<MeterRegistry> commonTags(
      @Value("${spring.application.name:unittestexample}") String app,
      @Value("${app.environment:local}") String env) {
    return registry ->
        registry
            .config()
            .commonTags(
                "application", app,
                "environment", env);
  }

  @Bean
  MeterFilter denyHighCardinalityTags() {
    return MeterFilter.denyNameStartsWith("jvm.classes.loaded.by.classloader");
  }
}
