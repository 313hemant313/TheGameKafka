package tech.thegamedefault.springboot.kafka.container;

import lombok.Getter;
import org.apache.kafka.common.TopicPartition;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.kafka.listener.MessageListener;

public final class TheGameKafkaConsumer<K, V> {

  @Getter
  private final String clientName;
  @Getter
  private final String topic;

  private final KafkaMessageListenerContainer<K, V> listenerContainer;

  public TheGameKafkaConsumer(String clientName, String topic,
      KafkaMessageListenerContainer<K, V> listenerContainer) {
    this.clientName = clientName;
    this.topic = topic;
    this.listenerContainer = listenerContainer;
  }

  public void listen(MessageListener<K, V> messageListener) {
    listenerContainer.setupMessageListener(messageListener);
    listenerContainer.start();
  }

  public void stop() {
    listenerContainer.stop();
  }

  public void pause() {
    listenerContainer.pause();
  }

  public void pausePartition(int partition) {
    listenerContainer.pausePartition(new TopicPartition(topic, partition));
  }

  public void resume() {
    listenerContainer.resume();
  }

  public void resumePartition(int partition) {
    listenerContainer.resumePartition(new TopicPartition(topic, partition));
  }


}
