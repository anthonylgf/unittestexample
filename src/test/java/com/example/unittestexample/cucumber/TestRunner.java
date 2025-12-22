package com.example.unittestexample.cucumber;

import static io.cucumber.junit.platform.engine.Constants.*;

import org.junit.platform.suite.api.*;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@Suite
@EnableAutoConfiguration(exclude = {KafkaAutoConfiguration.class})
@IncludeEngines("cucumber")
@SelectClasspathResource("cucumber-features")
@ConfigurationParameters({
  @ConfigurationParameter(
      key = FEATURES_PROPERTY_NAME,
      value = "src/test/resources/cucumber-features"),
  @ConfigurationParameter(
      key = PLUGIN_PROPERTY_NAME,
      value =
          "summary,pretty,json:target/cucumber-reports/cucumber.json,html:target/cucumber-reports/cucumber.html"),
  @ConfigurationParameter(
      key = GLUE_PROPERTY_NAME,
      value =
          "com.example.unittestexample.cucumber.steps,com.example.unittestexample.cucumber.configuration"),
})
public class TestRunner {}
