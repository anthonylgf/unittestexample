package com.example.unittestexample.configs;

import com.example.unittestexample.models.Aluno;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

@Configuration
@EnableKafka
@RequiredArgsConstructor
@EnableConfigurationProperties({KafkaProducerProperties.class, KafkaTopicProperties.class})
public class KafkaConfig {

  private final KafkaProducerProperties kafkaProducerProperties;

  @Bean
  @Primary
  public ConsumerFactory<String, Aluno> consumerFactory(final KafkaProperties kafkaProperties) {
    Map<String, Object> props = kafkaProperties.buildConsumerProperties();
    props.put(
        ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProducerProperties.getBootstrapServers());
    props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
    props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
    props.put(JsonDeserializer.TRUSTED_PACKAGES, "*");
    props.put(JsonDeserializer.VALUE_DEFAULT_TYPE, "com.example.unittestexample.models.Aluno");
    props.put(JsonDeserializer.USE_TYPE_INFO_HEADERS, false);
    return new DefaultKafkaConsumerFactory<>(props);
  }

  @Bean
  @Primary
  public ConcurrentKafkaListenerContainerFactory<String, Aluno> kafkaListenerContainerFactory(
      ConsumerFactory<String, Aluno> consumerFactory) {
    ConcurrentKafkaListenerContainerFactory<String, Aluno> listener =
        new ConcurrentKafkaListenerContainerFactory<>();
    listener.setConsumerFactory(consumerFactory);
    return listener;
  }

  @Bean
  @Primary
  public ProducerFactory<String, Aluno> producerFactory(final KafkaProperties kafkaProperties) {
    Map<String, Object> props = kafkaProperties.buildProducerProperties();
    props.put(
        ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProducerProperties.getBootstrapServers());
    props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
    props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
    props.put(JsonSerializer.ADD_TYPE_INFO_HEADERS, false);

    return new DefaultKafkaProducerFactory<>(props);
  }

  @Bean
  public KafkaTemplate<String, Aluno> kafkaTemplate(
      ProducerFactory<String, Aluno> producerFactory) {
    return new KafkaTemplate<>(producerFactory);
  }
}
