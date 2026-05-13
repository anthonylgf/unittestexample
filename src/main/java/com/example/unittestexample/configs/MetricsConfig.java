package com.example.unittestexample.configs;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.config.MeterFilter;
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MetricsConfig {

  @Bean
  MeterRegistryCustomizer<MeterRegistry> commonTags(MetricsProperties properties) {
    return registry ->
        registry
            .config()
            .commonTags(
                "application", properties.getName(),
                "environment", properties.getEnvironment());
  }

  @Bean
  MeterFilter denyHighCardinalityTags() {
    return MeterFilter.denyNameStartsWith("jvm.classes.loaded.by.classloader");
  }
}
