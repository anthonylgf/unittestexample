package com.example.unittestexample;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class UnittestexampleApplication {

  public static void main(String[] args) {
    SpringApplication.run(UnittestexampleApplication.class, args);
  }
}
