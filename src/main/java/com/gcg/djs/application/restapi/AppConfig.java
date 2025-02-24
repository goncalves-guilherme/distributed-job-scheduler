package com.gcg.djs.application.restapi;

import com.gcg.djs.domain.interfaces.external.ICronService;
import com.gcg.djs.domain.interfaces.external.ILog;
import com.gcg.djs.domain.interfaces.repositories.Repository;
import com.gcg.djs.domain.interfaces.services.IJobService;
import com.gcg.djs.domain.interfaces.services.IRecurringJobService;
import com.gcg.djs.domain.models.jobs.Job;
import com.gcg.djs.domain.models.recurringjob.RecurringJob;
import com.gcg.djs.domain.services.jobs.JobService;
import com.gcg.djs.domain.services.recurringjobs.RecurringJobService;
import com.gcg.djs.infrastructure.cron.CronService;
import com.gcg.djs.infrastructure.mongdb.JobRepository;
import com.gcg.djs.infrastructure.mongdb.RecurringJobRepository;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {
    @Value("${spring.data.mongodb.database}")
    private String mongoDBName;

    @Bean
    public MongoDatabase mongoDatabase(MongoClient mongoClient) {
        return mongoClient.getDatabase(mongoDBName);
    }

    @Bean
    public RecurringJobRepository recurringJob(MongoDatabase mongoDatabase) {
        return new RecurringJobRepository(mongoDatabase);
    }

    @Bean
    public JobRepository jobRepository(MongoDatabase mongoDatabase) {
        return new JobRepository(mongoDatabase);
    }

    @Bean
    public IJobService jobService(Repository<Job> jobRepository, ILog log) {
        return new JobService(jobRepository, log);
    }

    @Bean
    public IRecurringJobService recurringJobService(
            Repository<RecurringJob> recurringJobRepository, ILog log, ICronService cronService) {
        return new RecurringJobService(log, recurringJobRepository, cronService);
    }


    @Bean
    public ICronService cronService() {
        return new CronService();
    }

    @Bean
    public ILog log() {
        return new Slf4jLog(LoggerFactory.getLogger(JobService.class));
    }

}
