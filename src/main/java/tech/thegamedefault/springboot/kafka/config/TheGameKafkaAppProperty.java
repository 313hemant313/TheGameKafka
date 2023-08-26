package tech.thegamedefault.springboot.kafka.config;


import java.util.List;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties("the-game-kafka")
public class TheGameKafkaAppProperty {

  List<KafkaClientProperty> clientProducerProperties;
  List<KafkaClientProperty> clientConsumerProperties;

}
