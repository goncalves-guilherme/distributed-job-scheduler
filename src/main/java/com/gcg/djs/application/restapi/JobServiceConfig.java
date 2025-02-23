package com.gcg.djs.application.restapi;

import com.gcg.djs.domain.interfaces.external.ILog;
import com.gcg.djs.domain.interfaces.repositories.Repository;
import com.gcg.djs.domain.interfaces.services.IJobService;
import com.gcg.djs.domain.models.jobs.Job;
import com.gcg.djs.domain.services.jobs.JobService;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JobServiceConfig {

    private final Repository<Job> jobRepository;

    @Autowired
    public JobServiceConfig(Repository<Job> jobRepository) {
        this.jobRepository = jobRepository;
    }

    @Bean
    public ILog log() {
        return new Slf4jLog(LoggerFactory.getLogger(JobService.class));
    }

    @Bean
    public IJobService jobService(ILog log) {
        return new JobService(jobRepository, log);
    }
}