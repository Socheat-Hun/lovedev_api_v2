package com.lovedev.common.messaging.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.lovedev.common.messaging.publisher.EventPublisher;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

/**
 * Auto-configuration for Kafka messaging
 */
@AutoConfiguration
@EnableKafka
@ConditionalOnClass(KafkaTemplate.class)
@EnableConfigurationProperties(MessagingProperties.class)
@ConditionalOnProperty(
        prefix = "app.messaging",
        name = "enabled",
        havingValue = "true",
        matchIfMissing = true
)
@Slf4j
public class KafkaAutoConfiguration {

    private final MessagingProperties messagingProperties;

    public KafkaAutoConfiguration(MessagingProperties messagingProperties) {
        this.messagingProperties = messagingProperties;
        log.info("📨 LoveDev Messaging Starter Auto-Configuration Enabled");
        log.info("📍 Kafka Bootstrap Servers: {}", messagingProperties.getBootstrapServers());
    }

    /**
     * ObjectMapper for JSON serialization/deserialization
     */
    @Bean
    @ConditionalOnMissingBean
    public ObjectMapper kafkaObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return mapper;
    }

    /**
     * Kafka Producer Configuration
     */
    @Bean
    @ConditionalOnMissingBean
    public ProducerFactory<String, Object> producerFactory(ObjectMapper kafkaObjectMapper) {
        log.info("🚀 Configuring Kafka Producer");

        Map<String, Object> config = new HashMap<>();
        MessagingProperties.ProducerProperties producer = messagingProperties.getProducer();

        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, messagingProperties.getBootstrapServers());
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        config.put(ProducerConfig.ACKS_CONFIG, producer.getAcks());
        config.put(ProducerConfig.RETRIES_CONFIG, producer.getRetries());
        config.put(ProducerConfig.BATCH_SIZE_CONFIG, producer.getBatchSize());
        config.put(ProducerConfig.LINGER_MS_CONFIG, producer.getLingerMs());
        config.put(ProducerConfig.BUFFER_MEMORY_CONFIG, producer.getBufferMemory());
        config.put(ProducerConfig.COMPRESSION_TYPE_CONFIG, producer.getCompressionType());
        config.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, producer.isEnableIdempotence());
        config.put(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION,
                producer.getMaxInFlightRequestsPerConnection());

        DefaultKafkaProducerFactory<String, Object> factory = new DefaultKafkaProducerFactory<>(config);
        factory.setValueSerializer(new JsonSerializer<>(kafkaObjectMapper));

        return factory;
    }

    /**
     * Kafka Template for sending messages
     */
    @Bean
    @ConditionalOnMissingBean
    public KafkaTemplate<String, Object> kafkaTemplate(ProducerFactory<String, Object> producerFactory) {
        log.info("📤 Creating Kafka Template");
        return new KafkaTemplate<>(producerFactory);
    }

    /**
     * Kafka Consumer Configuration
     */
    @Bean
    @ConditionalOnMissingBean
    public ConsumerFactory<String, Object> consumerFactory(ObjectMapper kafkaObjectMapper) {
        log.info("📥 Configuring Kafka Consumer");

        Map<String, Object> config = new HashMap<>();
        MessagingProperties.ConsumerProperties consumer = messagingProperties.getConsumer();

        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, messagingProperties.getBootstrapServers());
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
        config.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, JsonDeserializer.class.getName());
        config.put(JsonDeserializer.TRUSTED_PACKAGES, "com.lovedev.common.messaging.event");
        config.put(JsonDeserializer.VALUE_DEFAULT_TYPE, "com.lovedev.common.messaging.event.BaseEvent");
        config.put(ConsumerConfig.GROUP_ID_CONFIG, consumer.getGroupId());
        config.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, consumer.getAutoOffsetReset());
        config.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, consumer.isEnableAutoCommit());
        config.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, consumer.getMaxPollRecords());
        config.put(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG, consumer.getMaxPollIntervalMs());
        config.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, consumer.getSessionTimeoutMs());
        config.put(ConsumerConfig.HEARTBEAT_INTERVAL_MS_CONFIG, consumer.getHeartbeatIntervalMs());

        DefaultKafkaConsumerFactory<String, Object> factory = new DefaultKafkaConsumerFactory<>(config);

//        JsonDeserializer<Object> jsonDeserializer = new JsonDeserializer<>(kafkaObjectMapper);
//        jsonDeserializer.addTrustedPackages("com.lovedev.common.messaging.event");
//        factory.setValueDeserializer(new ErrorHandlingDeserializer<>(jsonDeserializer));

        return factory;
    }

    /**
     * Kafka Listener Container Factory
     */
    @Bean
    @ConditionalOnMissingBean
    public ConcurrentKafkaListenerContainerFactory<String, Object> kafkaListenerContainerFactory(
            ConsumerFactory<String, Object> consumerFactory) {
        log.info("📻 Creating Kafka Listener Container Factory");

        ConcurrentKafkaListenerContainerFactory<String, Object> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);
        factory.setConcurrency(3); // Number of concurrent consumers
        factory.getContainerProperties().setPollTimeout(3000);

        return factory;
    }

    /**
     * Event Publisher Bean
     * IMPORTANT: EventPublisher is created as a bean here so it can be autowired
     */
    @Bean
    @ConditionalOnMissingBean
    public EventPublisher eventPublisher(
            KafkaTemplate<String, Object> kafkaTemplate) {
        log.info("📮 Creating Event Publisher");
        return new EventPublisher(kafkaTemplate);
    }
}