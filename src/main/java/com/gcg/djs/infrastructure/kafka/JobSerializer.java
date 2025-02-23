package com.gcg.djs.infrastructure.kafka;

import com.gcg.djs.domain.models.jobs.Job;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serializer;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.UUID;

public class JobSerializer implements Serializer<Job> {
    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
        // No configuration needed
    }

    @Override
    public byte[] serialize(String topic, Job data) {
        if (data == null) {
            return null;
        }
        return data.toString().getBytes(StandardCharsets.UTF_8);
    }

    @Override
    public void close() {
        // No resources to close
    }
}
