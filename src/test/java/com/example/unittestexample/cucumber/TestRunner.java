package com.example.unittestexample.cucumber;

import static io.cucumber.junit.platform.engine.Constants.*;

import org.junit.platform.suite.api.*;

@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("cucumber-features")
@ConfigurationParameters({
  @ConfigurationParameter(
      key = FEATURES_PROPERTY_NAME,
      value = "src/test/resources/cucumber-features"),
  @ConfigurationParameter(
      key = PLUGIN_PROPERTY_NAME,
      value =
          "summary,pretty,json:target/cucumber-reports/cucumber.json,html:target/cucumber-reports/cucumber.json"),
  @ConfigurationParameter(
      key = GLUE_PROPERTY_NAME,
      value =
          "com.example.unittestexample.cucumber.steps,com.example.unittestexample.cucumber.configuration"),
})
public class TestRunner {}
