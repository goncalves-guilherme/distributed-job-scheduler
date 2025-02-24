package com.gcg.djs.integration.utils;

import com.gcg.djs.domain.models.recurringjob.RecurringJob;
import org.bson.Document;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class RecurringJobTestUtils {

    public static LocalDateTime removeInstantPrecision(Instant instant) {
        if (instant == null) return null;
        return instant.atZone(ZoneId.systemDefault()).toLocalDateTime().withNano(0);
    }

    public static Instant toInstantSafely(Date date) {
        return Optional.ofNullable(date).map(Date::toInstant).orElse(null);
    }

    public static void assertRecurringJobMatchesDocument(RecurringJob job, Document doc) {
        if (job == null) {
            assertNotNull(doc);
            return;
        }

        assertEquals(job.cronExpression(), doc.getString("cronExpression"));
        assertEquals(job.name(), doc.getString("name"));
        assertEquals(job.description(), doc.getString("description"));
        assertEquals(job.active(), doc.getBoolean("active"));

        assertEquals(removeInstantPrecision(job.createdDate()), removeInstantPrecision(toInstantSafely(doc.getDate("createdDate"))));
        assertEquals(removeInstantPrecision(job.modifiedDate()), removeInstantPrecision(toInstantSafely(doc.getDate("modifiedDate"))));
        assertEquals(removeInstantPrecision(job.nextRun()), removeInstantPrecision(toInstantSafely(doc.getDate("nextRun"))));
        assertEquals(removeInstantPrecision(job.lastRun()), removeInstantPrecision(toInstantSafely(doc.getDate("lastRun"))));
    }

    public static Document recurringJobToDocument(RecurringJob recurringJob) {
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

    public static RecurringJob documentToRecurringJob(Document doc) {
        if (doc == null) {
            return null;
        }

        return new RecurringJob(
                UUID.fromString(doc.getString("_id")),
                doc.getString("cronExpression"),
                doc.getString("name"),
                doc.getString("description"),
                toInstantSafely(doc.getDate("createdDate")),
                toInstantSafely(doc.getDate("modifiedDate")),
                doc.getBoolean("active"),
                toInstantSafely(doc.getDate("nextRun")),
                toInstantSafely(doc.getDate("lastRun"))
        );
    }

    public static Instant truncateInstantToSeconds(Instant instant) {
        if (instant == null) return null;
        return instant.truncatedTo(ChronoUnit.SECONDS);
    }
}