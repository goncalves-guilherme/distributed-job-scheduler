package com.gcg.djs.infrastructure.kafka;

import com.gcg.djs.domain.models.jobs.Job;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;

public class KafkaQueueProducer implements AutoCloseable {

    private final KafkaProducer<String, byte[]> producer;
    private final String topic;

    public KafkaQueueProducer(String bootstrapServers, String topic) {
        this.topic = topic;

        // Set up Kafka producer properties
        Properties producerProperties = new Properties();
        producerProperties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        producerProperties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        producerProperties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JobSerializer.class.getName());

        // Initialize KafkaProducer with the properties
        this.producer = new KafkaProducer<>(producerProperties);
    }

    public void enqueueMessage(Job job) {
//        // Create a producer record to send the message
//        ProducerRecord<String, byte[]> record = new ProducerRecord<>(topic, job.id(), job.toByteArray());
//
//        // Send the message asynchronously
//        producer.send(record, (metadata, exception) -> {
//            if (exception != null) {
//                System.err.println("Error sending message to Kafka: " + exception.getMessage());
//            } else {
//                System.out.println("Message sent successfully to topic " + metadata.topic());
//            }
//        });
    }

    @Override
    public void close() {
        producer.close();
    }
}
