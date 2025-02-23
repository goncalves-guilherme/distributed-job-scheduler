package com.gcg.djs.application.restapi;

import com.gcg.djs.domain.interfaces.repositories.Repository;
import com.gcg.djs.domain.models.jobs.Job;
import com.gcg.djs.infrastructure.mongdb.JobRepository;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PreDestroy;

@Configuration
public class MongoRepositoryConfig {

    private MongoClient mongoClient;

    @Bean
    public MongoDatabase mongoDatabase() {
        String connectionString = "mongodb://localhost:27017";
        String databaseName = "jobschedulerdb";

        mongoClient = MongoClients.create(connectionString);
        return mongoClient.getDatabase(databaseName);
    }

    @PreDestroy
    public void closeMongoClient() {
        if (mongoClient != null) {
            mongoClient.close();
        }
    }

    @Bean
    public static Repository<Job> getJobRepository(MongoDatabase db){
        return new JobRepository(db);
    }
}
