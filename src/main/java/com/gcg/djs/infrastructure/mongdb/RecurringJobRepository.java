package com.gcg.djs.infrastructure.mongdb;

import com.gcg.djs.domain.common.Page;
import com.gcg.djs.domain.common.QueryBuilder;
import com.gcg.djs.domain.common.QueryParameters;
import com.gcg.djs.domain.interfaces.repositories.Repository;
import com.gcg.djs.domain.models.recurringjob.RecurringJob;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static com.mongodb.client.model.Filters.eq;

public class RecurringJobRepository implements Repository<RecurringJob> {
    private final MongoCollection<Document> recurringJobCollection;

    public RecurringJobRepository(MongoDatabase mongoDatabase) {
        Objects.requireNonNull(mongoDatabase);
        this.recurringJobCollection = mongoDatabase.getCollection("recurringJobs");
    }

    @Override
    public RecurringJob create(RecurringJob recurringJob) {
        Objects.requireNonNull(recurringJob);
        Document recurringJobDoc = recurringJobToDocument(recurringJob);
        recurringJobCollection.insertOne(recurringJobDoc);
        return documentToRecurringJob(recurringJobDoc);
    }

    @Override
    public RecurringJob update(RecurringJob recurringJob) {
        Objects.requireNonNull(recurringJob);
        Document updatedDoc = recurringJobToDocument(recurringJob);
        recurringJobCollection.updateOne(eq("_id", recurringJob.id().toString()), new Document("$set", updatedDoc));
        return recurringJob;
    }

    @Override
    public boolean delete(UUID id) {
        Objects.requireNonNull(id);
        var result = recurringJobCollection.deleteOne(eq("_id", id.toString()));
        return result.getDeletedCount() > 0;
    }

    @Override
    public RecurringJob getById(UUID id) {
        Objects.requireNonNull(id);
        Document doc = recurringJobCollection.find(eq("_id", id.toString())).first();
        return doc != null ? documentToRecurringJob(doc) : null;
    }

    @Override
    public Page<RecurringJob> getPage(int page, int pageSize, QueryParameters parameters) {
        Objects.requireNonNull(parameters);
        var filters = QueryBuilder.buildQuery(parameters, MongoFilterConverter.instance);

        List<RecurringJob> recurringJobs = recurringJobCollection
                .find(filters)
                .skip((page - 1) * pageSize)
                .limit(pageSize)
                .map(RecurringJobRepository::documentToRecurringJob)
                .into(new java.util.ArrayList<>());

        long totalCount = recurringJobCollection.countDocuments(filters);
        return new Page<>(page, pageSize, totalCount, recurringJobs);
    }

    private static Document recurringJobToDocument(RecurringJob recurringJob) {
        return new Document("_id", recurringJob.id().toString())
                .append("cronExpression", recurringJob.cronExpression())
                .append("name", recurringJob.name())
                .append("description", recurringJob.description())
                .append("createdDate", recurringJob.createdDate() != null ? Date.from(recurringJob.createdDate()) : null)
                .append("modifiedDate", recurringJob.modifiedDate() != null ? Date.from(recurringJob.modifiedDate()) : null)
                .append("active", recurringJob.active())
                .append("nextRun", recurringJob.nextRun() != null ? Date.from(recurringJob.nextRun()) : null)
                .append("lastRun", recurringJob.lastRun() != null ? Date.from(recurringJob.lastRun()) : null);
    }

    private static RecurringJob documentToRecurringJob(Document doc) {
        return new RecurringJob(
                UUID.fromString(doc.getString("_id")),
                doc.getString("cronExpression"),
                doc.getString("name"),
                doc.getString("description"),
                doc.getDate("createdDate") != null ? doc.getDate("createdDate").toInstant() : null,
                doc.getDate("modifiedDate") != null ? doc.getDate("modifiedDate").toInstant() : null,
                doc.getBoolean("active"),
                doc.getDate("nextRun") != null ? doc.getDate("nextRun").toInstant() : null,
                doc.getDate("lastRun") != null ? doc.getDate("lastRun").toInstant() : null
        );
    }
}