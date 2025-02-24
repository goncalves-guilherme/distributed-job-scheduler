package com.gcg.djs.infrastructure.kafka;

import com.gcg.djs.domain.interfaces.external.IMessageQueue;
import com.gcg.djs.domain.models.jobs.Job;
import org.apache.commons.lang3.NotImplementedException;
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

    private final String bootstrapServers = "localhost:9092";
    private final String topic = "jobQueue";

    private final KafkaQueueProducer producer;

    public KafkaQueue() {
        this.producer = new KafkaQueueProducer(bootstrapServers, topic);
    }

    @Override
    public void EnqueueMessage(Job message) {
        producer.enqueueMessage(message);
    }

    @Override
    public Job DequeueMessage() {
        throw new NotImplementedException();
    }

    @Override
    public void close() {
        producer.close();
    }
}
