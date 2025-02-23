package com.gcg.djs.domain.services.jobs;

import com.gcg.djs.domain.common.Page;
import com.gcg.djs.domain.common.QueryParameters;
import com.gcg.djs.domain.interfaces.external.ILog;
import com.gcg.djs.domain.interfaces.repositories.Repository;
import com.gcg.djs.domain.models.errors.ErrorMessages;
import com.gcg.djs.domain.models.errors.UnExpectedException;
import com.gcg.djs.domain.models.errors.ValidationException;
import com.gcg.djs.domain.models.jobs.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.*;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class JobServiceTests {

    @Mock
    private Repository<Job> jobRepository;

    @Mock
    private ILog log;

    private JobService jobService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        jobService = new JobService(jobRepository, this.log);
    }

    @Test
    void addJob_shouldCreateJobSuccessfully() throws ValidationException {
        // Arrange
        CreateJob createJob = new CreateJob(
                "Job Name",
                "Description",
                "Location",
                Instant.now());

        Job expectedJob = new Job(
                UUID.randomUUID(),
                createJob.name(),
                createJob.description(),
                createJob.binLocation(),
                JobStatus.CREATED,
                Instant.now(),
                Instant.now(),
                null,
                null,
                Instant.now().plus(2, ChronoUnit.HOURS),
                0,
                null);

        when(jobRepository.create(any(Job.class)))
                .thenReturn(cloneJob(expectedJob));

        // Act
        Job createdJob = jobService.addJob(createJob);

        // Assert
        assertNotNull(createdJob);
        assertEquals(expectedJob, createdJob);

        verify(jobRepository).create(any(Job.class));
        verifyNoMoreInteractions(jobRepository);
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"", " "})
    void addJob_shouldThrowValidationException_WhenStringFieldsAreBlank(String value) {
        // Arrange
        CreateJob createJob = new CreateJob(
                value,
                value,
                value,
                Instant.now());

        var expectedErrorMessage = "Job name must not be null, empty, or whitespace." +
                ", Job description must not be null, empty, or whitespace." +
                ", Job bin location must not be null, empty, or whitespace.";

        // Act & Assert
        ValidationException exception = assertThrows(ValidationException.class, () -> jobService.addJob(createJob));
        assertEquals(expectedErrorMessage, exception.getMessage());
        verify(jobRepository, times(0)).create(any(Job.class));
        verifyNoMoreInteractions(jobRepository);
    }

    @Test
    void addJob_shouldThrowUnexpectedException_WhenUnExceptedExceptionOccur() {
        // Arrange
        CreateJob createJob = new CreateJob(
                "Value",
                "Value",
                "Value",
                null);

        var expectedErrorMessage = "Next Execution date must not be null";

        // Act & Assert
        ValidationException exception = assertThrows(ValidationException.class, () -> jobService.addJob(createJob));
        assertEquals(expectedErrorMessage, exception.getMessage());
        verify(jobRepository, times(0)).create(any(Job.class));
        verifyNoMoreInteractions(jobRepository);
    }

    @Test
    void addJob_shouldThrowUnexpectedException_WhenRepositoryThrowsException() {
        // Arrange
        CreateJob createJob = new CreateJob(
                "Job Name",
                "Description",
                "Location",
                Instant.now()
        );

        when(jobRepository.create(any(Job.class))).thenThrow(new RuntimeException("Unexpected error"));

        // Act & Assert
        UnExpectedException exception = assertThrows(UnExpectedException.class, () -> jobService.addJob(createJob));
        assertEquals("Unexpected exception occurred", exception.getMessage());

        verify(jobRepository, times(1)).create(any(Job.class));
        verifyNoMoreInteractions(jobRepository);

        verify(log, times(1))
                .logError(eq(ErrorMessages.UNEXPECTED_ERROR), any(RuntimeException.class));
    }

    @Test
    void modifyJob_shouldModifyJobSuccessfully() throws ValidationException {
        // Arrange
        UUID jobId = UUID.randomUUID();

        Job existingJob = new Job(
                jobId,
                "Old Job",
                "Old Description",
                "Location",
                JobStatus.CREATED,
                Instant.now(),
                Instant.now(),
                Instant.now(),
                Instant.now(),
                Instant.now(),
                0,
                null);

        JobError expectedError = new JobError("Error", "type", Instant.now());

        Instant twoHoursLater = Instant.now().plusSeconds(7200);

        UpdateJob updateJob = new UpdateJob(
                Optional.of("New Job"),
                Optional.of("New Description"),
                Optional.of(JobStatus.EXECUTING),
                Optional.of(twoHoursLater),
                Optional.of(4),
                Optional.of(expectedError));

        Job updatedJob = new Job(
                jobId,
                "New Job",
                "New Description",
                "Location",
                JobStatus.EXECUTING,
                Instant.now(),
                Instant.now(),
                Instant.now(),
                Instant.now(),
                twoHoursLater,
                updateJob.retries().orElse(0),
                new JobError(
                        expectedError.errorMessage(), expectedError.errorType(), expectedError.errorTimestamp()));

        when(jobRepository.getById(jobId)).thenReturn(existingJob);
        when(jobRepository.update(any(Job.class))).thenReturn(updatedJob);

        // Act
        Job result = jobService.modifyJob(jobId, updateJob);

        // Assert
        assertEquals("New Job", result.name());
        assertEquals("New Description", result.description());
        assertEquals(JobStatus.EXECUTING, result.status());
        assertEquals(twoHoursLater.truncatedTo(ChronoUnit.SECONDS), result.nextExecution().truncatedTo(ChronoUnit.SECONDS));
        assertEquals(4, result.retries());
        assertEquals(expectedError, result.error());

        verify(jobRepository, times(1)).getById(jobId);
        verify(jobRepository, times(1)).update(any(Job.class));
        verifyNoMoreInteractions(jobRepository);
    }

    @Test
    void modifyJob_shouldThrowValidationException_WhenJobNotFound() {
        // Arrange
        UUID id = UUID.randomUUID();

        UpdateJob updateJob = new UpdateJob(
                Optional.of("New Job"),
                Optional.of("New Description"),
                Optional.of(JobStatus.EXECUTING),
                Optional.empty(),
                Optional.empty(),
                Optional.empty());

        when(jobRepository.getById(id)).thenReturn(null);

        String expectedErrorMessage = getExpectedJobNotFoundMessage(id);

        // Act & Assert
        ValidationException exception = assertThrows(
                ValidationException.class, () -> jobService.modifyJob(id, updateJob));

        assertEquals(expectedErrorMessage, exception.getMessage());

        verify(jobRepository, times(1)).getById(id);
        verifyNoMoreInteractions(jobRepository);
    }

    @Test
    void modifyJob_shouldLogErrorAndThrowUnexpectedException_WhenRepositoryThrowsException() {
        // Arrange
        UUID jobId = UUID.randomUUID();

        when(jobRepository.getById(any(UUID.class))).thenThrow(new RuntimeException("Unexpected error"));

        UpdateJob updateJob = new UpdateJob(
                Optional.of("New Job"),
                Optional.of("New Description"),
                Optional.of(JobStatus.EXECUTING),
                Optional.empty(),
                Optional.empty(),
                Optional.empty());

        // Act & Assert
        UnExpectedException exception = assertThrows(
                UnExpectedException.class, () -> jobService.modifyJob(jobId, updateJob));

        assertEquals("Unexpected exception occurred", exception.getMessage());

        verify(jobRepository, times(1)).getById(jobId);
        verifyNoMoreInteractions(jobRepository);

        verify(log, times(1))
                .logError(eq(ErrorMessages.UNEXPECTED_ERROR), any(RuntimeException.class));
    }

    @Test
    void removeJob_shouldRemoveJobSuccessfully() throws ValidationException {
        // Arrange
        Job job = getDefaultJob();
        UUID jobId = job.id();

        when(jobRepository.getById(jobId)).thenReturn(job);
        when(jobRepository.delete(jobId)).thenReturn(true);

        // Act
        boolean result = jobService.removeJob(jobId);

        // Assert
        assertTrue(result);

        verify(jobRepository).delete(jobId);
        verify(jobRepository, times(1)).getById(jobId);
        verifyNoMoreInteractions(jobRepository);
    }

    @Test
    void removeJob_shouldThrowValidationException_WhenIdIsNull() {
        // Arrange
        String expectedErrorMessage = "Job ID must not be null";

        // Act & Assert
        ValidationException exception = assertThrows(
                ValidationException.class, () -> jobService.removeJob(null));

        assertEquals(expectedErrorMessage, exception.getMessage());

        verifyNoMoreInteractions(jobRepository);
    }

    @Test
    void removeJob_shouldThrowValidationException_WhenJobNotFound() {
        // Arrange
        UUID jobId = UUID.randomUUID();

        when(jobRepository.getById(jobId)).thenReturn(null);

        String expectedErrorMessage = getExpectedJobNotFoundMessage(jobId);

        // Act & Assert
        ValidationException exception = assertThrows(
                ValidationException.class, () -> jobService.removeJob(jobId));

        assertEquals(expectedErrorMessage, exception.getMessage());

        verify(jobRepository, times(1)).getById(jobId);
        verifyNoMoreInteractions(jobRepository);
    }

    @Test
    void removeJob_shouldLogErrorAndThrowUnexpectedException_WhenRepositoryThrowsException() {
        // Arrange
        UUID jobId = UUID.randomUUID();

        when(jobRepository.getById(any(UUID.class))).thenThrow(new RuntimeException("Unexpected error"));

        // Act & Assert
        UnExpectedException exception = assertThrows(
                UnExpectedException.class, () -> jobService.removeJob(jobId));

        assertEquals("Unexpected exception occurred", exception.getMessage());

        verify(jobRepository, times(1)).getById(jobId);
        verifyNoMoreInteractions(jobRepository);

        verify(log, times(1))
                .logError(eq(ErrorMessages.UNEXPECTED_ERROR), any(RuntimeException.class));
    }

    @Test
    void getJobById_shouldGetJobByIdSuccessfully() throws ValidationException {
        // Arrange
        Job expectedJob = getDefaultJob();
        UUID jobId = expectedJob.id();

        when(jobRepository.getById(jobId)).thenReturn(expectedJob);

        // Act
        Job actualJob = jobService.getJobById(jobId);

        // Assert
        assertEquals(expectedJob, actualJob);

        verify(jobRepository, times(1)).getById(jobId);
        verifyNoMoreInteractions(jobRepository);
    }

    @Test
    void getJobById_shouldThrowValidationException_WhenJobNotFound() {
        // Arrange
        UUID jobId = UUID.randomUUID();

        when(jobRepository.getById(jobId)).thenReturn(null);

        String expectedErrorMessage = getExpectedJobNotFoundMessage(jobId);

        // Act & Assert
        ValidationException exception = assertThrows(
                ValidationException.class, () -> jobService.getJobById(jobId));

        assertEquals(expectedErrorMessage, exception.getMessage());

        verify(jobRepository, times(1)).getById(jobId);
        verifyNoMoreInteractions(jobRepository);
    }

    @Test
    void getJobById_shouldLogErrorAndThrowUnexpectedException_WhenRepositoryThrowsException() {
        // Arrange
        UUID jobId = UUID.randomUUID();

        when(jobRepository.getById(any(UUID.class))).thenThrow(new RuntimeException("Unexpected error"));

        // Act & Assert
        UnExpectedException exception = assertThrows(
                UnExpectedException.class, () -> jobService.getJobById(jobId));

        assertEquals("Unexpected exception occurred", exception.getMessage());

        verify(jobRepository, times(1)).getById(jobId);
        verifyNoMoreInteractions(jobRepository);

        verify(log, times(1))
                .logError(eq(ErrorMessages.UNEXPECTED_ERROR), any(RuntimeException.class));
    }

    @Test
    void searchJobsPage_shouldReturnValid() throws ValidationException {
        // Arrange
        int page = 1;
        int pageSize = 10;
        QueryParameters filters = new QueryParameters(List.of(), List.of());

        Job job1 = new Job(
                UUID.randomUUID(),
                "Job 1",
                "Description 1",
                "Location 1",
                JobStatus.CREATED,
                Instant.now(),
                Instant.now(),
                null,
                null,
                Instant.now(),
                0,
                null
        );

        // Set up the mock repository to return a page with jobs
        Page<Job> jobPage = new Page<>(page, pageSize, 1, List.of(job1));
        when(jobRepository.getPage(page, pageSize, filters)).thenReturn(jobPage);

        // Act
        Page<Job> result = jobService.searchJobs(page, pageSize, filters);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.totalPages());
        verify(jobRepository, times(1)).getPage(page, pageSize, filters);
        verifyNoMoreInteractions(jobRepository);
    }

    @ParameterizedTest
    @CsvSource({
            "0, 10, number",   // Invalid page number
            "-1, 10, number",  // Invalid page number
            "1, 0, size",    // Invalid page size
            "1, -1, size"    // Invalid page size
    })

    void searchJobsPage_shouldThrowValidationException_WhenPageOrSizeIsInvalid(
            int page, int pageSize, String errorToken) {
        // Arrange
        QueryParameters filters = new QueryParameters(List.of(), List.of());

        String expectedErrorMessage = String.format("Page %s must not be smaller or equal to 0", errorToken);

        // Act & Assert
        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> jobService.searchJobs(page, pageSize, filters));

        assertEquals(expectedErrorMessage, exception.getMessage());
        verifyNoMoreInteractions(jobRepository);
    }

    @Test
    void searchJogPage_shouldLogErrorAndThrowUnexpectedException_WhenRepositoryThrowsException() {
        // Arrange
        when(jobRepository.getPage(any(Integer.class), any(Integer.class), any(QueryParameters.class)))
                .thenThrow(new RuntimeException("Unexpected error"));

        // Act & Assert
        UnExpectedException exception = assertThrows(
                UnExpectedException.class,
                () -> jobService.searchJobs(1, 2, new QueryParameters(List.of(), List.of())));

        assertEquals("Unexpected exception occurred", exception.getMessage());

        verify(jobRepository, times(1))
                .getPage(any(Integer.class), any(Integer.class), any(QueryParameters.class));
        verifyNoMoreInteractions(jobRepository);

        verify(log, times(1))
                .logError(eq(ErrorMessages.UNEXPECTED_ERROR), any(RuntimeException.class));
    }

    private static Job getDefaultJob(){
        return new Job(
                UUID.randomUUID(),
                "Job Name",
                "Description",
                "Location",
                JobStatus.CREATED,
                Instant.now(),
                Instant.now(),
                null,
                null,
                Instant.now(),
                0,
                null);
    }

    private static String getExpectedJobNotFoundMessage(UUID jobId) {
        return String.format("Job with ID %s not found.", jobId);
    }

    // We should not depend on the same expected instances created in the test methods.
    private static Job cloneJob(Job job) {
        return new Job(
                job.id(),
                job.name(),
                job.description(),
                job.binLocation(),
                job.status(),
                job.createdDate(),
                job.modifiedDate(),
                job.executionStart(),
                job.executionEnd(),
                job.nextExecution(),
                job.retries(),
                job.error()
        );
    }
}
