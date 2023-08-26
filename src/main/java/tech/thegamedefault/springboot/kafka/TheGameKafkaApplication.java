package tech.thegamedefault.springboot.kafka;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.listener.MessageListener;
import tech.thegamedefault.springboot.kafka.container.TheGameKafkaConsumer;
import tech.thegamedefault.springboot.kafka.container.TheGameKafkaProducer;
import tech.thegamedefault.springboot.kafka.client.TheGameKafkaConsumerFactory;
import tech.thegamedefault.springboot.kafka.client.TheGameKafkaProducerFactory;

@EnableConfigurationProperties
@SpringBootApplication
@EnableKafka
@Slf4j
public class TheGameKafkaApplication {

  public static void main(String[] args) {
    SpringApplication.run(TheGameKafkaApplication.class, args);
  }

  @Autowired
  TheGameKafkaProducerFactory theGameKafkaProducerFactory;
  @Autowired
  TheGameKafkaConsumerFactory theGameKafkaConsumerFactory;

  @EventListener(ApplicationReadyEvent.class)
  public void test() {
    TheGameKafkaProducer<String, String> theGameKafkaProducer = theGameKafkaProducerFactory.getTheGameKafkaProducer(
        "testClient");
    theGameKafkaProducer.send("this is a test msg from TheGameKafkaProducer");

    TheGameKafkaConsumer<String, String> theGameKafkaConsumer = theGameKafkaConsumerFactory.getTheGameKafkaConsumer(
        "testClient");
    theGameKafkaConsumer.listen(messageListener());
  }

  private MessageListener<String, String> messageListener() {
    return rec -> log.info("TheGameKafkaConsumer listened : {}", rec);
  }

}
