package com.gcg.djs.integration.services;

import com.gcg.djs.domain.common.QueryParameters;
import com.gcg.djs.domain.common.filters.*;
import com.gcg.djs.domain.interfaces.external.ILog;
import com.gcg.djs.domain.interfaces.services.IJobService;
import com.gcg.djs.domain.models.errors.ValidationException;
import com.gcg.djs.domain.models.jobs.*;
import com.gcg.djs.domain.services.jobs.JobService;
import com.gcg.djs.infrastructure.mongdb.JobRepository;
import com.gcg.djs.integration.utils.JobTestUtils;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.gcg.djs.integration.utils.JobTestUtils.*;
import static com.gcg.djs.integration.utils.JobTestUtils.jobToDocument;
import static com.mongodb.client.model.Filters.eq;
import static org.junit.jupiter.api.Assertions.*;

public class JobServiceTests {
    private static final String TEST_MARK = "a1c2e483-078d-4b40-9252-219d35404414";

    private IJobService jobService;

    private final MongoClient mongoClient;
    private final MongoDatabase mongoDatabase;

    @Mock
    private ILog log;

    public JobServiceTests() {
        this.mongoClient = MongoClients.create("mongodb://localhost:27017");
        this.mongoDatabase = this.mongoClient.getDatabase("jobschedulerdb");
    }

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        var jobRepository = new JobRepository(this.mongoDatabase);
        this.jobService = new JobService(jobRepository, this.log);
    }

    @BeforeEach
    public void databaseCleanups() {
        if (this.mongoDatabase != null) {
            this.mongoDatabase.getCollection("jobs")
                    .deleteMany(Filters.regex("name", TEST_MARK));
        }
    }

    @AfterEach
    public void tearDown() {
        databaseCleanups();
        if (mongoClient != null) {
            mongoClient.close();
        }
    }

    @Test
    public void addJob_CreateJobWithValidParameters_JobIsCreated() throws ValidationException {
        // Arrange
        var createJob = new CreateJob(
                "Test Job 1 - " + TEST_MARK,
                "Test Job description 1",
                "/df",
                Instant.now());

        var expectedCreatedDate = removeInstantPrecision(Instant.now());

        // Act
        Job actualJob = this.jobService.addJob(createJob);

        // Assert
        assertEquals(createJob.name(), actualJob.name());
        assertEquals(createJob.description(), actualJob.description());
        assertEquals(createJob.binLocation(), actualJob.binLocation());
        assertEquals(removeInstantPrecision(createJob.nextExecution()), removeInstantPrecision(createJob.nextExecution()));
        assertEquals(JobStatus.CREATED, actualJob.status());
        assertEquals(expectedCreatedDate, removeInstantPrecision(actualJob.createdDate()));
        assertEquals(expectedCreatedDate, removeInstantPrecision(actualJob.modifiedDate()));
        assertEquals(0, actualJob.retries());

        Document jobDoc = this.mongoDatabase.getCollection("jobs")
                .find(eq("name", createJob.name()))
                .first();

        assertJobMatchesDocument(actualJob, jobDoc);
    }

    @SuppressWarnings("OptionalGetWithoutIsPresent")
    @Test
    public void modifyJob_UpdateJobWithValidParameters_JobIsUpdated() throws ValidationException {
        // Arrange
        var insertedJob = new Job(
                UUID.randomUUID(),
                "Test Job 1 - " + TEST_MARK,
                "Test Job description 1",
                "/df",
                JobStatus.CREATED,
                Instant.now(),
                Instant.now(),
                null,
                null,
                Instant.now(),
                0,
                null
        );

        this.mongoDatabase
                .getCollection("jobs")
                .insertOne(jobToDocument(insertedJob));

        var updateJob = new UpdateJob(
                Optional.of("Test Job 1 - NEW - " + TEST_MARK),
                Optional.of("Test Job description 1 - NEW"),
                Optional.of(JobStatus.EXECUTING),
                Optional.ofNullable(Instant.now().plusSeconds(60 * 60 * 24)),
                Optional.of(1),
                Optional.of(new JobError("Binary not found", "Error", Instant.now()))
        );

        var expectedJob = new Job(
                insertedJob.id(),
                updateJob.name().get(),
                updateJob.description().get(),
                insertedJob.binLocation(),
                updateJob.status().get(),
                insertedJob.createdDate(),
                Instant.now(),
                insertedJob.executionStart(),
                insertedJob.executionEnd(),
                updateJob.nextExecution().get(),
                updateJob.retries().get(),
                updateJob.error().get()
        );

        // Act
        this.jobService.modifyJob(insertedJob.id(), updateJob);

        // Assert
        var actualDocJob = this.mongoDatabase
                .getCollection("jobs")
                .find(eq("_id", insertedJob.id().toString()))
                .first();

        assertJobMatchesDocument(expectedJob, actualDocJob);
    }

    @Test
    public void modifyJob_NonExistingJob_ThrowsValidationException() {
        // Arrange
        var updateJob = new UpdateJob(
                Optional.of("Test Job 1 - NEW - " + TEST_MARK),
                Optional.of("Test Job description 1 - NEW"),
                Optional.of(JobStatus.EXECUTING),
                Optional.ofNullable(Instant.now().plusSeconds(60 * 60 * 24)),
                Optional.of(1),
                Optional.of(new JobError("Binary not found", "Error", Instant.now()))
        );

        // Act & Assert
        assertThrows(ValidationException.class, () ->
                this.jobService.modifyJob(UUID.randomUUID(), updateJob));
    }

    @Test
    public void getJobById_UpdateJobWithValidParameters_JobIsUpdated() throws ValidationException {
        // Arrange
        var insertedJob = new Job(
                UUID.randomUUID(),
                "Test Job 1 - " + TEST_MARK,
                "Test Job description 1",
                "/df",
                JobStatus.CREATED,
                Instant.now(),
                Instant.now(),
                null,
                null,
                Instant.now(),
                0,
                null
        );

        this.mongoDatabase
                .getCollection("jobs")
                .insertOne(jobToDocument(insertedJob));

        // Act
        this.jobService.getJobById(insertedJob.id());

        // Assert
        var actualDocJob = this.mongoDatabase
                .getCollection("jobs")
                .find(eq("_id", insertedJob.id().toString()))
                .first();

        assertJobMatchesDocument(insertedJob, actualDocJob);
    }

    @Test
    public void getJobById_NonExistingJob_ThrowsValidationException() {
        // Act & Assert
        assertThrows(ValidationException.class, () ->
                this.jobService.getJobById(UUID.randomUUID()));
    }

    @Test
    public void removeJob_UpdateJobWithValidParameters_JobIsUpdated() throws ValidationException {
        // Arrange
        var insertedJob = new Job(
                UUID.randomUUID(),
                "Test Job 1 - " + TEST_MARK,
                "Test Job description 1",
                "/df",
                JobStatus.CREATED,
                Instant.now(),
                Instant.now(),
                null,
                null,
                Instant.now(),
                0,
                null
        );

        this.mongoDatabase
                .getCollection("jobs")
                .insertOne(jobToDocument(insertedJob));

        // Act
        this.jobService.removeJob(insertedJob.id());

        // Assert
        var actualDocJob = this.mongoDatabase
                .getCollection("jobs")
                .find(eq("_id", insertedJob.id().toString()))
                .first();

        assertNull(actualDocJob);
    }

    @Test
    public void deleteJob_NonExistingJob_ThrowsValidationException() {
        // Act & Assert
        assertThrows(ValidationException.class, () ->
                this.jobService.removeJob(UUID.randomUUID()));
    }

    @Test
    public void searchJobsPage_ValidQueryParameters_ReturnsFilteredData() throws ValidationException {
        // Arrange
        List<Job> jobs = new ArrayList<>();

        List<Job> expectJobs = new ArrayList<>();

        for (int i = 1; i <= 10; i++) {
            var retries = 0;
            var createdDate = Instant.now().truncatedTo(ChronoUnit.SECONDS);

            if(i == 1 || i == 2) {
                retries = 5;
                createdDate = Instant.now().minusSeconds(60*24*10).truncatedTo(ChronoUnit.SECONDS);
            }

            var job = new Job(
                    UUID.randomUUID(),
                    "Test Job " + i + " - " + TEST_MARK,
                    "Test Job description " + i,
                    "/df",
                    JobStatus.CREATED,
                    createdDate,
                    Instant.now().truncatedTo(ChronoUnit.SECONDS),
                    null,
                    null,
                    Instant.now().truncatedTo(ChronoUnit.SECONDS),
                    retries,
                    null
            );

            jobs.add(job);

            if(i == 1 || i == 2) {
                expectJobs.add(job);
            }
        }

        this.mongoDatabase
                .getCollection("jobs")
                .insertMany(jobs.stream().map(JobTestUtils::jobToDocument).toList());

        var queryParameters = new QueryParameters(
                List.of(new LogicalFilter(LogicalOperator.AND),
                        new NumberFilter(ComparisonOperator.GREATER_THAN, "retries", 3),
                        new InstantFilter(ComparisonOperator.LESS_THAN, "createdDate", Instant.now())),
                List.of());

        // Act
        var actualResult = this.jobService.searchJobs(1, 100, queryParameters);

        // Assert
        assertEquals(2, actualResult.totalItems());
        assertEquals(1, actualResult.page());
        assertEquals(1, actualResult.totalPages());
        assertIterableEquals(expectJobs, actualResult.items());
    }
}
