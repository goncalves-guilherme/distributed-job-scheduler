package com.gcg.djs.infrastructure.mongdb;

import com.gcg.djs.domain.common.Page;
import com.gcg.djs.domain.common.QueryBuilder;
import com.gcg.djs.domain.common.QueryParameters;
import com.gcg.djs.domain.interfaces.repositories.Repository;
import com.gcg.djs.domain.models.jobs.Job;
import com.gcg.djs.domain.models.jobs.JobError;
import com.gcg.djs.domain.models.jobs.JobStatus;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static com.mongodb.client.model.Filters.eq;

public class JobRepository implements Repository<Job> {
    private final MongoCollection<Document> jobCollection;

    public JobRepository(MongoDatabase mongoDatabase) {
        Objects.requireNonNull(mongoDatabase);

        this.jobCollection = mongoDatabase.getCollection("jobs");
    }

    @Override
    public Job create(Job job) {
        Objects.requireNonNull(job);

        Document jobDoc = jobToDocument(job);
        jobCollection.insertOne(jobDoc);
        return documentToJob(jobDoc);
    }

    @Override
    public Job update(Job job) {
        Objects.requireNonNull(job);

        Document updatedDoc = jobToDocument(job);
        jobCollection.updateOne(eq("_id", job.id().toString()), new Document("$set", updatedDoc));
        return job;
    }

    @Override
    public boolean delete(UUID id) {
        Objects.requireNonNull(id);

        var result = jobCollection.deleteOne(eq("_id", id.toString()));
        return result.getDeletedCount() > 0;
    }

    @Override
    public Job getById(UUID id) {
        Objects.requireNonNull(id);

        Document doc = jobCollection.find(eq("_id", id.toString())).first();
        return doc != null ? documentToJob(doc) : null;
    }

    @Override
    public Page<Job> getPage(int page, int pageSize, QueryParameters parameters) {
        Objects.requireNonNull(parameters);

        var filters = QueryBuilder.buildQuery(parameters, MongoFilterConverter.instance);

        List<Job> jobs = jobCollection
                .find(filters)
                .skip((page - 1) * pageSize)
                .limit(pageSize)
                .map(JobRepository::documentToJob)
                .into(new java.util.ArrayList<>());

        long totalCount = jobCollection.countDocuments(filters);

        return new Page<>(page, pageSize, totalCount, jobs);
    }

    private static Document jobToDocument(Job job) {
        return new Document("_id", job.id().toString())
                .append("name", job.name())
                .append("description", job.description())
                .append("binLocation", job.binLocation())
                .append("status", job.status().toString())
                .append("retries", job.retries())
                .append("error", job.error() != null ? jobErrorToDocument(job.error()) : null)
                .append("createdDate", job.createdDate() != null ? Date.from(job.createdDate()) : null)
                .append("modifiedDate", job.modifiedDate() != null ? Date.from(job.modifiedDate()) : null)
                .append("executionStart", job.executionStart() != null ? Date.from(job.executionStart()) : null)
                .append("executionEnd", job.executionEnd() != null ? Date.from(job.executionEnd()) : null)
                .append("nextExecution", job.nextExecution() != null ? Date.from(job.nextExecution()) : null);
    }


    private static Document jobErrorToDocument(JobError error) {
        return new Document("errorMessage", error.errorMessage())
                .append("errorType", error.errorType())
                .append("errorTimestamp", error.errorTimestamp().toString());
    }

    private static Job documentToJob(Document doc) {
        return new Job.Builder()
                .jobId(UUID.fromString(doc.getString("_id")))
                .name(doc.getString("name"))
                .description(doc.getString("description"))
                .binLocation(doc.getString("binLocation"))
                .status(JobStatus.valueOf(doc.getString("status")))
                .createdDate(doc.getDate("createdDate") != null ? doc.getDate("createdDate").toInstant() : null)
                .modifiedDate(doc.getDate("modifiedDate") != null ? doc.getDate("modifiedDate").toInstant() : null)
                .executionStart(doc.getDate("executionStart") != null ? doc.getDate("executionStart").toInstant() : null)
                .executionEnd(doc.getDate("executionEnd") != null ? doc.getDate("executionEnd").toInstant() : null)
                .nextExecution(doc.getDate("nextExecution") != null ? doc.getDate("nextExecution").toInstant() : null)
                .retries(doc.getInteger("retries"))
                .error(doc.get("error") != null ? documentToJobError((Document) doc.get("error")) : null)
                .build();
    }


    private static JobError documentToJobError(Document doc) {
        String errorMessage = doc.getString("errorMessage");
        String errorType = doc.getString("errorType");
        Instant errorTimestamp = Instant.parse(doc.getString("errorTimestamp"));
        return new JobError(errorMessage, errorType, errorTimestamp);
    }
}