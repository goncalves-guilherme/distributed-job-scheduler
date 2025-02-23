package com.gcg.djs.integration.utils;

import com.gcg.djs.domain.models.jobs.Job;
import com.gcg.djs.domain.models.jobs.JobError;
import org.bson.Document;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.util.AssertionErrors.assertNull;

public class JobTestUtils {
    public static LocalDateTime removeInstantPrecision(Instant instant) {
        if(instant == null) return null;

        return instant.atZone(ZoneId.systemDefault()).toLocalDateTime().withNano(0);
    }

    public static Instant toInstantSafely(Date date) {
        return Optional.ofNullable(date).map(java.util.Date::toInstant).orElse(null);
    }

    public static void assertJobMatchesDocument(Job job, Document jobDoc) {
        if (job == null) {
            assertNotNull(jobDoc);
            return;
        }

        assertEquals(job.name(), jobDoc.getString("name"));
        assertEquals(job.description(), jobDoc.getString("description"));
        assertEquals(job.binLocation(), jobDoc.getString("binLocation"));
        assertEquals(job.status().toString(), jobDoc.getString("status"));
        assertEquals(job.retries(), jobDoc.getInteger("retries").intValue());

        if (job.error() != null) {
            Document errorDoc = (Document) jobDoc.get("error");
            assertNotNull(errorDoc);

            assertEquals(job.error().errorMessage(), errorDoc.getString("errorMessage"));
            assertEquals(job.error().errorType(), errorDoc.getString("errorType"));
            assertEquals(job.error().errorTimestamp(), Instant.parse(errorDoc.getString("errorTimestamp")));
        } else {
            assertNull("Job error should be null in the document", jobDoc.get("error"));
        }

        assertEquals(removeInstantPrecision(job.createdDate()), removeInstantPrecision(toInstantSafely(jobDoc.getDate("createdDate"))));
        assertEquals(removeInstantPrecision(job.modifiedDate()), removeInstantPrecision(toInstantSafely(jobDoc.getDate("modifiedDate"))));
        assertEquals(removeInstantPrecision(job.executionStart()), removeInstantPrecision(toInstantSafely(jobDoc.getDate("executionStart"))));
        assertEquals(removeInstantPrecision(job.executionEnd()), removeInstantPrecision(toInstantSafely(jobDoc.getDate("executionEnd"))));
        assertEquals(removeInstantPrecision(job.nextExecution()), removeInstantPrecision(toInstantSafely(jobDoc.getDate("nextExecution"))));
    }

    public static Document jobToDocument(Job job) {
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

    public static Document jobErrorToDocument(JobError error) {
        return new Document("errorMessage", error.errorMessage())
                .append("errorType", error.errorType())
                .append("errorTimestamp", error.errorTimestamp().toString());
    }
}
