package tech.thegamedefault.springboot.kafka.container;

import java.util.concurrent.TimeUnit;
import lombok.Getter;
import org.apache.kafka.common.TopicPartition;
import org.springframework.kafka.listener.BatchMessageListener;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.kafka.listener.MessageListener;
import tech.thegamedefault.springboot.kafka.config.ThreadPoolProvider;

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

  public void listen(BatchMessageListener<K, V> messageListener) {
    listenerContainer.setupMessageListener(messageListener);
    listenerContainer.start();
  }

  public String getListenerId() {
    return listenerContainer.getListenerId();
  }

  public void stop() {
    listenerContainer.stop();
  }

  public void pause() {
    listenerContainer.pause();
  }

  public void pauseWithResumeIn(long delayMinutes) {
    ThreadPoolProvider.getScheduler()
        .schedule(listenerContainer::resume, delayMinutes, TimeUnit.MINUTES);
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
