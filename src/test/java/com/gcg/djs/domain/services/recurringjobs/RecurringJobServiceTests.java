package com.gcg.djs.domain.services.recurringjobs;

import com.gcg.djs.domain.common.Page;
import com.gcg.djs.domain.common.QueryParameters;
import com.gcg.djs.domain.interfaces.external.ICronService;
import com.gcg.djs.domain.interfaces.external.ILog;
import com.gcg.djs.domain.interfaces.repositories.Repository;
import com.gcg.djs.domain.models.errors.ErrorMessages;
import com.gcg.djs.domain.models.errors.UnExpectedException;
import com.gcg.djs.domain.models.errors.ValidationException;
import com.gcg.djs.domain.models.recurringjob.CreateRecurringJob;
import com.gcg.djs.domain.models.recurringjob.RecurringJob;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RecurringJobServiceTests {

    @Mock
    private Repository<RecurringJob> recurringRepository;

    @Mock
    private ICronService cronService;

    @Mock
    private ILog log;

    private RecurringJobService recurringJobService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        recurringJobService = new RecurringJobService(log, recurringRepository, cronService);
    }

    @Test
    void createRecurringJob_shouldCreateJobSuccessfully() throws ValidationException {
        // Arrange
        CreateRecurringJob createJob = new CreateRecurringJob(
                "0 0 * * *",
                "Recurring Job Name",
                "Description");

        Instant nextRun = Instant.now().plusSeconds(3600);
        when(cronService.calculateNextRun(createJob.cronExpression())).thenReturn(nextRun);
        when(recurringRepository.create(any(RecurringJob.class))).thenReturn(
                new RecurringJob(
                        UUID.randomUUID(),
                        createJob.cronExpression(),
                        createJob.name(),
                        createJob.description(),
                        Instant.now(),
                        Instant.now(),
                        true,
                        nextRun,
                        null));

        // Act
        RecurringJob createdJob = recurringJobService.createRecurringJob(createJob);

        // Assert
        assertNotNull(createdJob);
        assertEquals(nextRun, createdJob.nextRun());
        verify(recurringRepository).create(any(RecurringJob.class));
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"", " "})
    void createRecurringJob_shouldThrowValidationException_WhenStringFieldsAreBlank(String value) {
        // Arrange
        CreateRecurringJob createJob = new CreateRecurringJob(
                "0 0 * * *",
                value,
                value);
        String expectedErrorMessage = "Recurring job name must not be null, empty, or whitespace." +
                ", Recurring job description must not be null, empty, or whitespace.";

        // Act & Assert
        ValidationException exception = assertThrows(
                ValidationException.class, () -> recurringJobService.createRecurringJob(createJob));
        assertEquals(expectedErrorMessage, exception.getMessage());
        verify(recurringRepository, never()).create(any(RecurringJob.class));
    }

    @Test
    void createRecurringJob_shouldThrowUnexpectedException_WhenRepositoryThrowsException() {
        // Arrange
        CreateRecurringJob createJob = new CreateRecurringJob(
                "0 0 * * *",
                "Name",
                "Description");
        when(recurringRepository.create(any(RecurringJob.class))).thenThrow(new RuntimeException("Unexpected error"));

        // Act & Assert
        UnExpectedException exception = assertThrows(
                UnExpectedException.class, () -> recurringJobService.createRecurringJob(createJob));
        assertEquals("Unexpected exception occurred", exception.getMessage());
        verify(recurringRepository).create(any(RecurringJob.class));
        verify(log).logError(eq(ErrorMessages.UNEXPECTED_ERROR), any(RuntimeException.class));
    }

    @Test
    void getRecurringJobsPage_shouldReturnValidPage() throws ValidationException {
        // Arrange
        int page = 1;
        int pageSize = 10;
        QueryParameters filters = new QueryParameters(List.of(), List.of());
        Page<RecurringJob> expectedPage = new Page<>(page, pageSize, 1, List.of(new RecurringJob(UUID.randomUUID(), "0 0 * * *", "Name", "Description", Instant.now(), Instant.now(), true, Instant.now(), null)));
        when(recurringRepository.getPage(page, pageSize, filters)).thenReturn(expectedPage);

        // Act
        Page<RecurringJob> result = recurringJobService.getRecurringJobsPage(page, pageSize, filters);

        // Assert
        assertEquals(expectedPage, result);
        verify(recurringRepository).getPage(page, pageSize, filters);
    }

    @ParameterizedTest
    @CsvSource({
            "0, 10",
            "1, 0"
    })
    void getRecurringJobsPage_shouldThrowValidationException_WhenPageOrSizeIsInvalid(int page, int pageSize) {
        // Arrange
        QueryParameters filters = new QueryParameters(List.of(), List.of());

        // Act & Assert
        assertThrows(ValidationException.class, () -> recurringJobService.getRecurringJobsPage(page, pageSize, filters));
        verify(recurringRepository, never()).getPage(anyInt(), anyInt(), any(QueryParameters.class));
    }

    @Test
    void getRecurringJobById_shouldReturnJob() throws ValidationException {
        // Arrange
        UUID id = UUID.randomUUID();
        RecurringJob expectedJob = new RecurringJob(id, "0 0 * * *", "Name", "Description", Instant.now(), Instant.now(), true, Instant.now(), null);
        when(recurringRepository.getById(id)).thenReturn(expectedJob);

        // Act
        RecurringJob result = recurringJobService.getRecurringJobById(id);

        // Assert
        assertEquals(expectedJob, result);
        verify(recurringRepository).getById(id);
    }

    @Test
    void getRecurringJobById_shouldThrowValidationException_WhenIdIsNull() {
        // Act & Assert
        assertThrows(ValidationException.class, () -> recurringJobService.getRecurringJobById(null));
        verify(recurringRepository, never()).getById(any(UUID.class));
    }

    @Test
    void getNextRun_shouldReturnJobsReadyToRun() throws ValidationException {
        // Arrange
        int page = 1;
        int pageSize = 10;
        Page<RecurringJob> expectedPage = new Page<>(page, pageSize, 1, List.of(new RecurringJob(UUID.randomUUID(), "0 0 * * *", "Name", "Description", Instant.now(), Instant.now(), true, Instant.now().minusSeconds(10), null)));
        when(recurringRepository.getPage(eq(page), eq(pageSize), any(QueryParameters.class))).thenReturn(expectedPage);

        // Act
        Page<RecurringJob> result = recurringJobService.getNextRun(page, pageSize);

        // Assert
        assertEquals(expectedPage, result);
        verify(recurringRepository).getPage(eq(page), eq(pageSize), any(QueryParameters.class));
    }

    @Test
    void pauseJob_shouldPauseJobSuccessfully() throws ValidationException {
        // Arrange
        UUID id = UUID.randomUUID();
        RecurringJob job = new RecurringJob(id, "0 0 * * *", "Name", "Description", Instant.now(), Instant.now(), true, Instant.now(), null);
        when(recurringRepository.getById(id)).thenReturn(job);
        when(recurringRepository.update(any(RecurringJob.class))).thenReturn(job);

        // Act
        boolean result = recurringJobService.pauseJob(id);

        // Assert
        assertTrue(result);
        verify(recurringRepository).update(any(RecurringJob.class));
    }
}