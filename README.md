# TheGameKafka- A Spring boot kafka client to support multi (broker, topic, consumer and producer) using spring configuration.

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

TheGameKafka uses a number of projects to work properly:

* [Spring Boot](https://spring.io/projects/spring-boot) - Open source Java-based framework
* [Maven](https://maven.apache.org/what-is-maven.html) Maven is a powerful project management tool that is based on POM (project object model)
* [Apache Kafka](https://kafka.apache.org) - Apache Kafka is an open-source distributed event streaming platform used by thousands of companies for high-performance data pipelines, streaming analytics, data integration, and mission-critical applications.

### Prerequisites
The following items should be installed in your system:
* Java 18 or newer.
* git command line tool (https://help.github.com/articles/set-up-git)
* Your preferred IDE
    * [IntelliJ IDEA](https://www.jetbrains.com/idea/)
    * [Eclipse](https://www.eclipse.org)
    * [Spring Tools Suite](https://spring.io/tools) (STS)
    * [VS Code](https://code.visualstudio.com)

    
### Usage
```sh
TheGameKafkaProducer<String, String> theGameKafkaProducer = theGameKafkaProducerFactory.getTheGameKafkaProducer(
    "testClient");
theGameKafkaProducer.send("this is a test msg from TheGameKafkaProducer");

TheGameKafkaConsumer<String, String> theGameKafkaConsumer = theGameKafkaConsumerFactory.getTheGameKafkaConsumer(
    "testClient");
theGameKafkaConsumer.listen(messageListener());

private MessageListener<String, String> messageListener() {
  return rec -> log.info("TheGameKafkaConsumer listened : {}", rec);
}
```

### Sample config
```
the-game-kafka:
  clientConsumerProperties:
    - clientName: "testClient"
      topic: "testTopic"
      enabled: true
      kafkaProperties:
        consumer:
          bootstrapServers: localhost:9094
          groupId: "tgk-group"
          autoOffset: earliest
          keyDeserializer: org.apache.kafka.common.serialization.StringDeserializer
          value-deserializer: org.springframework.kafka.support.serializer.ErrorHandlingDeserializer
          properties:
            spring:
              json:
                trusted:
                  packages: "*"
              deserializer:
                value:
                  delegate:
                    class: org.apache.kafka.common.serialization.StringDeserializer
  clientProducerProperties:
    - clientName: "testClient"
      topic: "testTopic"
      enabled: true
      kafkaProperties:
        producer:
          bootstrapServers: localhost:9094
```

### To Run
```sh
./mvnw spring-boot:run
```
### To Build
```sh
./mvnw clean install
```