package tech.thegamedefault.springboot.kafka.interceptor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerInterceptor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;

@Slf4j
public class TheGameConsumerInterceptor<K, V> implements ConsumerInterceptor<K, V> {

  @Override
  public ConsumerRecords<K, V> onConsume(ConsumerRecords<K, V> consumerRecords) {
    Set<TopicPartition> partitions = consumerRecords.partitions();
    partitions.forEach(
        partition -> interceptRecordsFromPartition(consumerRecords.records(partition)));
    return consumerRecords;
  }

  private void interceptRecordsFromPartition(List<ConsumerRecord<K, V>> records) {
    records.forEach(rec -> {
      var myHeaders = new ArrayList<>();
      rec.headers().headers("MyHeader").forEach(myHeaders::add);
      log.info("My Headers: {}", myHeaders);
    });
  }

  @Override
  public void onCommit(Map<TopicPartition, OffsetAndMetadata> map) {

  }

  @Override
  public void close() {

  }

  @Override
  public void configure(Map<String, ?> map) {
  }

}
