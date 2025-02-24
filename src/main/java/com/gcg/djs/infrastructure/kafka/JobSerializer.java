package com.gcg.djs.infrastructure.kafka;

import com.gcg.djs.domain.models.jobs.Job;
import org.apache.kafka.common.serialization.Serializer;

public class JobSerializer implements Serializer<Job> {
    @Override
    public byte[] serialize(String topic, Job job) {
        try {
            return null;//job.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error serializing Job object", e);
        }
    }
}
