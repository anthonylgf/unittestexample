package com.example.unittestexample.cucumber.configuration;

import com.example.unittestexample.UnittestexampleApplication;
import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.context.ConfigDataApplicationContextInitializer;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.reactive.server.WebTestClient;

@ActiveProfiles("docker-local")
@SpringBootTest(
    classes = UnittestexampleApplication.class,
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@CucumberContextConfiguration
@ContextConfiguration(
    initializers = ConfigDataApplicationContextInitializer.class,
    classes = CucumberConfiguration.SpringConfiguration.class)
public class CucumberConfiguration {

  @Configuration
  @ComponentScan(basePackages = {"com.example.unittestexample"})
  static class SpringConfiguration {

    @Bean
    @Primary
    public WebTestClient getWebTestClient() {
      return WebTestClient.bindToServer().baseUrl("http://localhost:8083").build();
    }
  }
}
