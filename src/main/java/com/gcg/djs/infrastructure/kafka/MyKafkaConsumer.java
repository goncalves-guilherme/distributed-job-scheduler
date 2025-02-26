package com.gcg.djs.infrastructure.kafka;

import com.gcg.djs.domain.models.jobs.Job;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.ByteArrayDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

public final class MyKafkaConsumer implements AutoCloseable {

    private final KafkaConsumer<String, byte[]> consumer;

    public MyKafkaConsumer(String bootstrapServers, String groupId, String topic) {
        // Set up Kafka consumer properties
        Properties consumerProperties = new Properties();
        consumerProperties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        consumerProperties.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        consumerProperties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        consumerProperties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ByteArrayDeserializer.class.getName());
        consumerProperties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        // Initialize KafkaConsumer with the properties
        this.consumer = new KafkaConsumer<>(consumerProperties);
        this.consumer.subscribe(Collections.singletonList(topic));
    }

    public void consume() {
        while (true) {
            ConsumerRecords<String, byte[]> records = consumer.poll(Duration.ofMillis(100));
            for (ConsumerRecord<String, byte[]> record : records) {
                try {
                    // Deserialize byte array to Protobuf Job
                    JobOuterClass.Job protobufJob = JobOuterClass.Job.parseFrom(record.value());

                    // Convert Protobuf Job to domain Job
                    Job job = ProtobufConverter.protobufToDomain(protobufJob);

                    // Process the job
                    System.out.println("Received job: " + job);
                } catch (Exception e) {
                    System.err.println("Error processing message: " + e.getMessage());
                }
            }
        }
    }

    @Override
    public void close() {
        consumer.close();
    }
}
