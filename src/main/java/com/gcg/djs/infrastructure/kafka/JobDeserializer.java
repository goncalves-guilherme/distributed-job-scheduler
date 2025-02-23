package com.gcg.djs.infrastructure.kafka;

import com.gcg.djs.domain.models.jobs.Job;
import org.apache.kafka.common.serialization.Deserializer;

import java.nio.charset.StandardCharsets;
import java.util.Map;

public class JobDeserializer implements Deserializer<Job> {
    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
        // No configuration needed
    }

    @Override
    public Job deserialize(String topic, byte[] data) {
        if (data == null) {
            return null;
        }
        String jobString = new String(data, StandardCharsets.UTF_8);
        // Implement logic to convert the string back to a Job object
        // This is a placeholder and needs proper implementation
        return new Job.Builder().build();
    }

    @Override
    public void close() {
        // No resources to close
    }
}