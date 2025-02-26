package com.gcg.djs.integration.services;

import com.gcg.djs.domain.interfaces.external.ILog;
import com.gcg.djs.domain.services.jobs.JobService;
import com.gcg.djs.domain.services.scheduler.JobSchedulerService;
import com.gcg.djs.infrastructure.kafka.JobMessageQueue;
import com.gcg.djs.infrastructure.kafka.KafkaProducer;
import com.gcg.djs.infrastructure.mongdb.JobRepository;
import com.mongodb.client.MongoClients;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

public class JobSchedulerServiceTests {

    @Mock
    private ILog log;

    @Test
    public void te(){
        var mongoClient = MongoClients.create("mongodb://localhost:27017");
        var mongoDatabase = mongoClient.getDatabase("jobschedulerdb");

        var jobRepository = new JobRepository(mongoDatabase);
        var jobService = new JobService(jobRepository, this.log);

        var messageQueue = new JobMessageQueue(
                new KafkaProducer("localhost:9092", "schedulerTests"));

        var jobSchedulerService = new JobSchedulerService(jobRepository, messageQueue);

        jobSchedulerService.enqueueNextJobsPage();
    }
}
