package com.lovedev.common.messaging.publisher;

import com.lovedev.common.messaging.event.BaseEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

/**
 * Centralized event publisher for all Kafka events
 * Provides simple API for publishing events to Kafka topics
 */
@RequiredArgsConstructor
@Slf4j
public class EventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    /**
     * Publish an event synchronously to specified topic
     *
     * @param topic Kafka topic name
     * @param event Event to publish
     */
    public void publish(String topic, BaseEvent event) {
        try {
            log.info("Publishing event to topic {}: eventType={}, eventId={}",
                    topic, event.getEventType(), event.getEventId());

            kafkaTemplate.send(topic, event.getEventId(), event).get();

            log.info("Successfully published event to topic {}: eventId={}",
                    topic, event.getEventId());
        } catch (Exception e) {
            log.error("Failed to publish event to topic {}: eventId={}",
                    topic, event.getEventId(), e);
            throw new RuntimeException("Failed to publish event", e);
        }
    }

    /**
     * Publish an event asynchronously to specified topic
     *
     * @param topic Kafka topic name
     * @param event Event to publish
     * @return CompletableFuture with send result
     */
    public CompletableFuture<SendResult<String, Object>> publishAsync(String topic, BaseEvent event) {
        log.info("Publishing event asynchronously to topic {}: eventType={}, eventId={}",
                topic, event.getEventType(), event.getEventId());

        CompletableFuture<SendResult<String, Object>> future =
                kafkaTemplate.send(topic, event.getEventId(), event);

        future.whenComplete((result, ex) -> {
            if (ex == null) {
                log.info("Successfully published event to topic {}: eventId={}, partition={}, offset={}",
                        topic,
                        event.getEventId(),
                        result.getRecordMetadata().partition(),
                        result.getRecordMetadata().offset());
            } else {
                log.error("Failed to publish event to topic {}: eventId={}",
                        topic, event.getEventId(), ex);
            }
        });

        return future;
    }

    /**
     * Publish an event with custom key
     *
     * @param topic Kafka topic name
     * @param key Custom message key
     * @param event Event to publish
     */
    public void publishWithKey(String topic, String key, BaseEvent event) {
        try {
            log.info("Publishing event to topic {} with key {}: eventType={}, eventId={}",
                    topic, key, event.getEventType(), event.getEventId());

            kafkaTemplate.send(topic, key, event).get();

            log.info("Successfully published event to topic {} with key {}", topic, key);
        } catch (Exception e) {
            log.error("Failed to publish event to topic {} with key {}", topic, key, e);
            throw new RuntimeException("Failed to publish event", e);
        }
    }
}