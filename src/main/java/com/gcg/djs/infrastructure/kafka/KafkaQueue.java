package com.gcg.djs.infrastructure.kafka;

import com.gcg.djs.domain.interfaces.external.IMessageQueue;
import com.gcg.djs.domain.models.jobs.Job;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;
import java.util.UUID;

public class KafkaQueue implements IMessageQueue<Job> {

    private final String bootstrapServers = "localhost:9092";  // Kafka server(s)
    private final String topic = "jobQueue";  // Kafka topic name

    private final KafkaProducer<String, byte[]> producer;
    private final KafkaConsumer<String, byte[]> consumer;

    public KafkaQueue() {
        // Set up the Kafka producer
        Properties producerProperties = new Properties();
        producerProperties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        producerProperties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        producerProperties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JobSerializer.class.getName());
        this.producer = new KafkaProducer<>(producerProperties);

        // Set up the Kafka consumer
        Properties consumerProperties = new Properties();
        consumerProperties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        consumerProperties.put(ConsumerConfig.GROUP_ID_CONFIG, "job-consumer-group");
        consumerProperties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        consumerProperties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JobDeserializer.class.getName());
        this.consumer = new KafkaConsumer<>(consumerProperties);
    }

    @Override
    public void EnqueueMessage(Job message) {
        // Send a message to Kafka topic
        ProducerRecord<UUID, Job> record = new ProducerRecord<>(topic, message.id(), message);
//        producer.send(record, (metadata, exception) -> {
//            if (exception != null) {
//                exception.printStackTrace();
//            } else {
//                System.out.println("Message sent to Kafka with offset: " + metadata.offset());
//            }
//        });
    }

    @Override
    public Job DequeueMessage() {
        // Start consuming messages from Kafka topic
        consumer.subscribe(java.util.Collections.singletonList(topic));

//        while (true) {
//            var records = consumer.poll(java.time.Duration.ofMillis(1000));
//            if (!records.isEmpty()) {
//                // Process the message and return it
//                var record = records.iterator().next();
//                return record.value();
//            }
//        }
        return null;
    }

    // Serializer for Job class (you need to implement it)
    public static class JobSerializer implements Serializer<Job> {
        @Override
        public byte[] serialize(String topic, Job job) {
            // Implement serialization logic here, depending on how Job is structured
            return job.toString().getBytes();
        }
    }

    // Deserializer for Job class (you need to implement it)
    public static class JobDeserializer implements Deserializer<Job> {
        @Override
        public Job deserialize(String topic, byte[] data) {
            // Implement deserialization logic here, depending on how Job is structured
            // Returning a dummy Job instance for now as an example
            //return new Job();  // Replace this with actual deserialization logic
            return null;
        }
    }
}
