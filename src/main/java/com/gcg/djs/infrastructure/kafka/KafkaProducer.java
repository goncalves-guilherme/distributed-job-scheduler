package com.gcg.djs.infrastructure.kafka;

import com.gcg.djs.domain.models.jobs.Job;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.ByteArraySerializer;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;

public final class KafkaProducer implements Producer<Job> {

    private final org.apache.kafka.clients.producer.KafkaProducer<String, byte[]> producer;
    private final String topic;

    public KafkaProducer(String bootstrapServers, String topic) {
        this.topic = topic;

        // Set up Kafka producer properties
        Properties producerProperties = new Properties();
        producerProperties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        producerProperties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        producerProperties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, ByteArraySerializer.class.getName());

        // Initialize KafkaProducer with the properties
        this.producer = new org.apache.kafka.clients.producer.KafkaProducer<>(producerProperties);
    }

    public void produce(Job job) {
        // Convert domain Job to Protobuf Job
        JobOuterClass.Job protobufJob = ProtobufConverter.toProtobufJob(job);

        // Serialize Protobuf Job to byte array
        byte[] serializedJob = protobufJob.toByteArray();

        // Create a producer record to send the message
        ProducerRecord<String, byte[]> record = new ProducerRecord<>(topic, job.id().toString(), serializedJob);

        // Send the message asynchronously
        producer.send(record, (metadata, exception) -> {
            if (exception != null) {
                System.err.println("Error sending message to Kafka: " + exception.getMessage());
            } else {
                System.out.println("Message sent successfully to topic " + metadata.topic());
            }
        });

        producer.flush();
    }

    @Override
    public void close() {
        producer.close();
    }
}
