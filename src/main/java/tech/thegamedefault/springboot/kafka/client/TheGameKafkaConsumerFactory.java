package tech.thegamedefault.springboot.kafka.client;

import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.stereotype.Component;
import tech.thegamedefault.springboot.kafka.container.TheGameKafkaConsumer;
import tech.thegamedefault.springboot.kafka.config.TheGameKafkaAppProperty;

@Component
public class TheGameKafkaConsumerFactory {

  private final GenericApplicationContext applicationContext;
  private final TheGameKafkaAppProperty appProperty;

  private static final String BEAN_NAME_POSTFIX = "-TheGameKafkaConsumer";

  @Autowired
  public TheGameKafkaConsumerFactory(GenericApplicationContext applicationContext,
      TheGameKafkaAppProperty appProperty) {
    this.applicationContext = applicationContext;
    this.appProperty = appProperty;
  }

  @PostConstruct
  public void createTheGameConsumerBeans() {
    appProperty.getClientConsumerProperties().forEach(clientProperty -> {
      String clientName = clientProperty.getClientName();
      GenericBeanDefinition beanDefinition = new GenericBeanDefinition();
      beanDefinition.setBeanClass(TheGameKafkaConsumer.class);
      beanDefinition.setInstanceSupplier(
          () -> createTheGameConsumer(clientName, clientProperty.getTopic(),
              clientProperty.getKafkaProperties()));
      this.applicationContext.registerBeanDefinition(clientName + BEAN_NAME_POSTFIX,
          beanDefinition);
    });
  }

  public <K, V> TheGameKafkaConsumer<K, V> getTheGameKafkaConsumer(String clientName) {
    return (TheGameKafkaConsumer<K, V>) this.applicationContext.getBean(
        clientName + BEAN_NAME_POSTFIX);
  }

  private <K, V> TheGameKafkaConsumer<K, V> createTheGameConsumer(String clientName, String topic,
      KafkaProperties kafkaProperties) {
    KafkaMessageListenerContainer<K, V> listenerContainer = registerAndGetContainer(topic,
        kafkaProperties);
    return new TheGameKafkaConsumer<>(clientName, topic, listenerContainer);
  }


  private <K, V> KafkaMessageListenerContainer<K, V> registerAndGetContainer(String topic,
      KafkaProperties kafkaProperties) {
    DefaultKafkaConsumerFactory<K, V> kafkaConsumerFactory = new DefaultKafkaConsumerFactory<>(
        kafkaProperties.buildConsumerProperties());
    ContainerProperties containerProperties = new ContainerProperties(topic);
    return new KafkaMessageListenerContainer<>(kafkaConsumerFactory, containerProperties);
  }

}
