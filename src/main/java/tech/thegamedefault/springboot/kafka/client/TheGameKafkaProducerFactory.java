package tech.thegamedefault.springboot.kafka.client;

import javax.annotation.PostConstruct;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import tech.thegamedefault.springboot.kafka.container.TheGameKafkaProducer;
import tech.thegamedefault.springboot.kafka.config.KafkaClientProperty;
import tech.thegamedefault.springboot.kafka.config.TheGameKafkaAppProperty;

@Component
public class TheGameKafkaProducerFactory {

  private static final String BEAN_NAME_POSTFIX = "-TheGameKafkaProducer";
  private final GenericApplicationContext applicationContext;
  private final TheGameKafkaAppProperty appProperty;

  @Autowired
  public TheGameKafkaProducerFactory(GenericApplicationContext applicationContext,
      TheGameKafkaAppProperty appProperty) {
    this.applicationContext = applicationContext;
    this.appProperty = appProperty;
  }

  @PostConstruct
  public void init() {
    appProperty.getClientProducerProperties().stream().filter(KafkaClientProperty::isEnabled)
        .forEach(clientProperty -> {
          String clientName = clientProperty.getClientName();
          GenericBeanDefinition beanDefinition = new GenericBeanDefinition();
          beanDefinition.setBeanClass(TheGameKafkaProducer.class);
          beanDefinition.setInstanceSupplier(
              () -> createTheGameProducer(clientName, clientProperty.getTopic(),
                  clientProperty.getKafkaProperties()));
          this.applicationContext.registerBeanDefinition(clientName + BEAN_NAME_POSTFIX,
              beanDefinition);
        });
  }

  public <K, V> TheGameKafkaProducer<K, V> getTheGameKafkaProducer(String clientName) {
    return (TheGameKafkaProducer<K, V>) this.applicationContext.getBean(
        clientName + BEAN_NAME_POSTFIX);
  }

  private <K, V> TheGameKafkaProducer<K, V> createTheGameProducer(String clientName, String topic,
      KafkaProperties kafkaProperties) {
    return new TheGameKafkaProducer<>(clientName, topic, createTemplate(kafkaProperties));
  }

  private <K, V> KafkaTemplate<K, V> createTemplate(KafkaProperties kafkaProperties) {
    kafkaProperties.getProducer().getProperties()
        .put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
    kafkaProperties.getProducer().getProperties()
        .put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
    final DefaultKafkaProducerFactory<K, V> producerFactory = new DefaultKafkaProducerFactory<>(
        kafkaProperties.buildProducerProperties());
    return new KafkaTemplate<>(producerFactory);
  }

}
