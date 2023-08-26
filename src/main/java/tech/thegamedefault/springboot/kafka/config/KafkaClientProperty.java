package tech.thegamedefault.springboot.kafka.config;

import lombok.Data;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;

@Data
public class KafkaClientProperty {

  private String clientName;
  private String topic;
  private boolean enabled;
  private KafkaProperties kafkaProperties;

}
