

package tech.thegamedefault.springboot.kafka.container;

import lombok.Getter;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.ListenableFuture;

public final class TheGameKafkaProducer<K, V> {

  private final KafkaTemplate<K, V> kafkaTemplate;

  @Getter
  private final String clientName;
  private final String topic;

  public TheGameKafkaProducer(String clientName, String topic, KafkaTemplate<K, V> kafkaTemplate) {
    this.clientName = clientName;
    this.topic = topic;
    this.kafkaTemplate = kafkaTemplate;
  }

  public ListenableFuture<SendResult<K, V>> send(V message) {
    return kafkaTemplate.send(topic, message);
  }

  public ListenableFuture<SendResult<K, V>> send(K messageKey, V message) {
    return kafkaTemplate.send(topic, messageKey, message);
  }

}

