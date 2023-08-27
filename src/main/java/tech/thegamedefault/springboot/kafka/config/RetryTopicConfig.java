package tech.thegamedefault.springboot.kafka.config;

import java.util.List;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.kafka.annotation.KafkaListenerAnnotationBeanPostProcessor;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerEndpointRegistrar;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.config.MethodKafkaListenerEndpoint;
import org.springframework.kafka.core.ConsumerFactory.Listener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.retrytopic.RetryTopicConfiguration;
import org.springframework.kafka.retrytopic.RetryTopicConfigurationBuilder;
import org.springframework.kafka.retrytopic.RetryTopicConfigurationSupport;
import org.springframework.kafka.retrytopic.RetryTopicConfigurer;
import org.springframework.kafka.retrytopic.RetryTopicConfigurer.EndpointProcessor;
import tech.thegamedefault.springboot.kafka.exception.NonRetryableException;

@Configuration
public class RetryTopicConfig extends RetryTopicConfigurationSupport {

  @Bean
  RetryTopicConfiguration retryConfig(KafkaTemplate<String, String> template) {
    return RetryTopicConfigurationBuilder.newInstance().fixedBackOff(3000).maxAttempts(4)
        .notRetryOn(NonRetryableException.class).autoCreateTopicsWith(2, (short) 1)
        .create(template);
  }

  @Bean
  @Order(0)
  SmartInitializingSingleton dynamicRetry(RetryTopicConfigurer configurer,
      RetryTopicConfiguration config, KafkaListenerAnnotationBeanPostProcessor<?, ?> bpp,
      KafkaListenerContainerFactory<?> factory, Listener listener,
      KafkaListenerEndpointRegistry registry) {

    return () -> {
      KafkaListenerEndpointRegistrar registrar = bpp.getEndpointRegistrar();
      MethodKafkaListenerEndpoint<String, String> mainEndpoint = new MethodKafkaListenerEndpoint<>();
      EndpointProcessor endpointProcessor = endpoint -> {
        // customize as needed (e.g. apply attributes to retry endpoints).
        if (!endpoint.equals(mainEndpoint)) {
          endpoint.setConcurrency(1);
        }
        // these are required
        endpoint.setMessageHandlerMethodFactory(bpp.getMessageHandlerMethodFactory());
        endpoint.setTopics("topic");
        endpoint.setId("id");
        endpoint.setGroupId("group");
      };
      mainEndpoint.setBean(listener);
      try {
        mainEndpoint.setMethod(Listener.class.getDeclaredMethod("onMessage", ConsumerRecord.class));
      } catch (NoSuchMethodException | SecurityException ex) {
        throw new IllegalStateException(ex);
      }
      mainEndpoint.setConcurrency(2);
      mainEndpoint.setTopics("topic");
      mainEndpoint.setId("id");
      mainEndpoint.setGroupId("group");
      configurer.processMainAndRetryListeners(endpointProcessor, mainEndpoint, config, registrar,
          factory, "kafkaListenerContainerFactory");
    };
  }

  @Override
  protected void configureCustomizers(CustomizersConfigurer customizersConfigurer) {
    customizersConfigurer.customizeErrorHandler(eh -> eh.setSeekAfterError(false));
    customizersConfigurer.customizeDeadLetterPublishingRecoverer(
        lc -> lc.setAppendOriginalHeaders(true));
  }

  @Override
  protected void manageNonBlockingFatalExceptions(
      List<Class<? extends Throwable>> nonBlockingFatalExceptions) {
    nonBlockingFatalExceptions.add(NonRetryableException.class);
  }

}
