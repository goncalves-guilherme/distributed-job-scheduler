package com.gcg.djs.infrastructure.kafka;

import com.gcg.djs.domain.interfaces.external.IMessageQueue;
import com.gcg.djs.domain.models.jobs.Job;
import org.apache.commons.lang3.NotImplementedException;

public class JobMessageQueue implements IMessageQueue<Job> {

    private final Producer<Job> producer;

    public JobMessageQueue(Producer<Job> producer) {
        this.producer = producer;
    }

    @Override
    public void EnqueueMessage(Job message) {
        producer.produce(message);
    }

    @Override
    public Job DequeueMessage() {
        throw new NotImplementedException();
    }

    @Override
    public void close() throws Exception {
        producer.close();
    }
}
