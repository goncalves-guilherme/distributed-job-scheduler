package com.gcg.djs.integration.services;

import com.gcg.djs.domain.common.QueryParameters;
import com.gcg.djs.domain.common.filters.*;
import com.gcg.djs.domain.interfaces.external.ILog;
import com.gcg.djs.domain.interfaces.services.IRecurringJobService;
import com.gcg.djs.domain.models.errors.ValidationException;
import com.gcg.djs.domain.models.recurringjob.CreateRecurringJob;
import com.gcg.djs.domain.models.recurringjob.RecurringJob;
import com.gcg.djs.domain.services.recurringjobs.RecurringJobService;
import com.gcg.djs.infrastructure.cron.CronService;
import com.gcg.djs.infrastructure.mongdb.RecurringJobRepository;
import com.gcg.djs.integration.utils.RecurringJobTestUtils;
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
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.gcg.djs.integration.utils.RecurringJobTestUtils.assertRecurringJobMatchesDocument;
import static com.gcg.djs.integration.utils.RecurringJobTestUtils.recurringJobToDocument;
import static com.mongodb.client.model.Filters.eq;
import static org.junit.jupiter.api.Assertions.*;

public class RecurringJobServiceTests {
    private static final String TEST_MARK = "a1c2e483-078d-4b40-9252-219d35404414";

    private IRecurringJobService recurringJobService;

    private final MongoClient mongoClient;
    private final MongoDatabase mongoDatabase;

    @Mock
    private ILog log;

    public RecurringJobServiceTests() {
        this.mongoClient = MongoClients.create("mongodb://localhost:27017");
        this.mongoDatabase = this.mongoClient.getDatabase("jobschedulerdb");
    }

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        var recurringJobRepository = new RecurringJobRepository(this.mongoDatabase);
        this.recurringJobService = new RecurringJobService(this.log, recurringJobRepository, new CronService());
    }

    @BeforeEach
    public void databaseCleanups() {
        if (this.mongoDatabase != null) {
            this.mongoDatabase.getCollection("recurringJobs")
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
    public void createRecurringJob_CreateJobWithValidParameters_JobIsCreated() throws ValidationException {
        // Arrange
        var midnightCron = "0 0 * * *";

        var createJob = new CreateRecurringJob(
                midnightCron,
                "Test Recurring Job 1 - " + TEST_MARK,
                "Test Recurring Job description 1");

        LocalDateTime now = LocalDateTime.now();
        System.out.println("Current time: " + now);

        // Get the next midnight
        LocalDateTime nextMidnight = now.toLocalDate().atStartOfDay().plusDays(1);

        // Convert to Instant for the next cron job
        Instant nextRun = nextMidnight.atZone(ZoneId.systemDefault()).toInstant();

        // Act
        RecurringJob actualJob = this.recurringJobService.createRecurringJob(createJob);

        // Assert
        assertEquals(createJob.name(), actualJob.name());
        assertEquals(createJob.description(), actualJob.description());
        assertEquals(createJob.cronExpression(), actualJob.cronExpression());
        assertEquals(nextRun, actualJob.nextRun());
        assertTrue(actualJob.active());

        Document jobDoc = this.mongoDatabase.getCollection("recurringJobs")
                .find(eq("name", createJob.name()))
                .first();

        assertRecurringJobMatchesDocument(actualJob, jobDoc);
    }

    @Test
    public void getRecurringJobById_ExistingJob_ReturnsJob() throws ValidationException {
        // Arrange
        var insertedJob = new RecurringJob(
                UUID.randomUUID(),
                "0 0 * * *",
                "Test Recurring Job 1 - " + TEST_MARK,
                "Test Recurring Job description 1",
                Instant.now().truncatedTo(ChronoUnit.SECONDS),
                Instant.now().truncatedTo(ChronoUnit.SECONDS),
                true,
                Instant.now().plusSeconds(3600).truncatedTo(ChronoUnit.SECONDS),
                null
        );

        this.mongoDatabase
                .getCollection("recurringJobs")
                .insertOne(recurringJobToDocument(insertedJob));

        // Act
        RecurringJob actualJob = this.recurringJobService.getRecurringJobById(insertedJob.id());

        // Assert
        assertEquals(insertedJob, actualJob);

        Document jobDoc = this.mongoDatabase.getCollection("recurringJobs")
                .find(eq("_id", insertedJob.id().toString()))
                .first();

        assertRecurringJobMatchesDocument(actualJob, jobDoc);
    }

    @Test
    public void getRecurringJobById_NonExistingJob_ThrowsValidationException() {
        // Act & Assert
        assertThrows(ValidationException.class, () ->
                this.recurringJobService.getRecurringJobById(UUID.randomUUID()));
    }

    @Test
    public void getRecurringJobsPage_ValidQueryParameters_ReturnsFilteredData() throws ValidationException {
        // Arrange
        List<RecurringJob> jobs = new ArrayList<>();
        List<RecurringJob> expectedJobs = new ArrayList<>();

        for (int i = 1; i <= 10; i++) {
            var createdDate = Instant.now()
                    .plusSeconds(60 * 24 * 10).truncatedTo(ChronoUnit.SECONDS);

            if (i == 1 || i == 2) {
                createdDate = Instant.now()
                        .minusSeconds(60 * 24 * 10)
                        .truncatedTo(ChronoUnit.SECONDS);
            }

            var job = new RecurringJob(
                    UUID.randomUUID(),
                    "0 0 * * *",
                    "Test Recurring Job " + i + " - " + TEST_MARK,
                    "Test Recurring Job description " + i,
                    createdDate,
                    Instant.now().truncatedTo(ChronoUnit.SECONDS),
                    true,
                    Instant.now().truncatedTo(ChronoUnit.SECONDS),
                    null
            );

            jobs.add(job);

            if (i == 1 || i == 2) {
                expectedJobs.add(job);
            }
        }

        this.mongoDatabase
                .getCollection("recurringJobs")
                .insertMany(jobs.stream().map(RecurringJobTestUtils::recurringJobToDocument).toList());

        var queryParameters = new QueryParameters(
                List.of(new LogicalFilter(LogicalOperator.AND),
                        new InstantFilter(ComparisonOperator.LESS_THAN, "createdDate", Instant.now())),
                List.of());

        // Act
        var actualResult = this.recurringJobService.getRecurringJobsPage(1, 100, queryParameters);

        // Assert
        assertEquals(2, actualResult.totalItems());
        assertEquals(1, actualResult.page());
        assertEquals(1, actualResult.totalPages());
        assertIterableEquals(expectedJobs, actualResult.items());
    }

    @Test
    public void getNextRun_ValidPageAndSize_ReturnsPageWithJobs() throws ValidationException {
        // Arrange
        List<RecurringJob> jobs = new ArrayList<>();
        List<RecurringJob> jobsToRun = new ArrayList<>();

        for (int i = 1; i <= 10; i++) {
            var nextExecution = Instant.now().plusSeconds(60 * 60 * (i + 1));

            if(i % 2 == 0) {
                nextExecution = Instant.now().minusSeconds(60 * 60 * (i + 1));
            }

            var job = new RecurringJob(
                    UUID.randomUUID(),
                    "0 0 * * *",
                    "Test Recurring Job " + i + " - " + TEST_MARK,
                    "Test Recurring Job description " + i,
                    Instant.now().minusSeconds(60 * 60 * (i + 1)), // nextRun in the past
                    Instant.now().minusSeconds(60 * 60 * (i + 1)),
                    true,
                    nextExecution,
                    null
            );

            jobs.add(job);

            if(i % 2 == 0) {
                jobsToRun.add(job);
            }
        }

        this.mongoDatabase
                .getCollection("recurringJobs")
                .insertMany(jobs.stream().map(RecurringJobTestUtils::recurringJobToDocument).toList());

        var page = 1;
        var pageSize = jobsToRun.size();

        // Act
        var actualResult = this.recurringJobService.getNextRun(page, pageSize);

        // Assert
        assertEquals(page, actualResult.page());
        assertEquals(1, actualResult.totalPages());
        assertEquals(pageSize, actualResult.totalItems());
        assertTrue(actualResult.items().stream().allMatch(job -> job.nextRun().isBefore(Instant.now())));
    }

    @Test
    public void getNextRun_InvalidPage_ThrowsValidationException() {
        // Act & Assert
        assertThrows(ValidationException.class, () -> this.recurringJobService.getNextRun(0, 10));
        assertThrows(ValidationException.class, () -> this.recurringJobService.getNextRun(-1, 10));
    }

    @Test
    public void getNextRun_InvalidPageSize_ThrowsValidationException() {
        // Act & Assert
        assertThrows(ValidationException.class, () -> this.recurringJobService.getNextRun(1, -5));
        assertThrows(ValidationException.class, () -> this.recurringJobService.getNextRun(1, 0));
    }

    @Test
    public void getNextRun_NoJobsAvailable_ReturnsEmptyPage() throws ValidationException {
        // Arrange
        var page = 1;
        var pageSize = 10;

        // Act
        var actualResult = this.recurringJobService.getNextRun(page, pageSize);

        // Assert
        assertEquals(page, actualResult.page());
        assertEquals(0, actualResult.totalItems()); // No jobs in the database
        assertEquals(0, actualResult.items().size()); // No jobs on the page
    }

    @Test
    public void pauseJob_ExistingJob_JobIsPaused() throws ValidationException {
        // Arrange
        // Insert a recurring job with active status = true
        UUID jobId = UUID.randomUUID();
        var job = new RecurringJob(
                jobId,
                "0 0 * * *",
                "Test Recurring Job 1 - " + TEST_MARK,
                "Test Recurring Job description 1",
                Instant.now().minusSeconds(3600),
                Instant.now(),
                true,
                Instant.now().plusSeconds(3600),
                null
        );

        this.mongoDatabase
                .getCollection("recurringJobs")
                .insertOne(recurringJobToDocument(job));

        // Act
        boolean result = this.recurringJobService.pauseJob(jobId);

        // Assert
        assertTrue(result);
        RecurringJob pausedJob = this.recurringJobService.getRecurringJobById(jobId);
        assertFalse(pausedJob.active());
    }

    @Test
    public void pauseJob_NonExistingJob_ThrowsValidationException() {
        // Arrange
        UUID nonExistingJobId = UUID.randomUUID();

        // Act & Assert
        assertThrows(ValidationException.class, () -> this.recurringJobService.pauseJob(nonExistingJobId));
    }

    @Test
    public void pauseJob_JobAlreadyPaused_StillReturnsTrue() throws ValidationException {
        // Arrange
        UUID jobId = UUID.randomUUID();
        var job = new RecurringJob(
                jobId,
                "0 0 * * *",
                "Test Recurring Job 1 - " + TEST_MARK,
                "Test Recurring Job description 1",
                Instant.now().minusSeconds(3600),
                Instant.now(),
                false,
                Instant.now().plusSeconds(3600),
                null
        );

        this.mongoDatabase
                .getCollection("recurringJobs")
                .insertOne(recurringJobToDocument(job));

        // Act
        boolean result = this.recurringJobService.pauseJob(jobId);

        // Assert
        assertTrue(result); // The method should return true, as the job is already paused
        RecurringJob pausedJob = this.recurringJobService.getRecurringJobById(jobId);
        assertFalse(pausedJob.active()); // The job should remain paused
    }

    @Test
    public void pauseJob_InvalidJobId_ThrowsValidationException() {
        // Arrange
        UUID invalidJobId = UUID.randomUUID();

        // Act & Assert
        assertThrows(ValidationException.class, () -> this.recurringJobService.pauseJob(invalidJobId));
    }
}
