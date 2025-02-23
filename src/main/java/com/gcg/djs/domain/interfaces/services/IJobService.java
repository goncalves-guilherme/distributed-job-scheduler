package com.gcg.djs.domain.interfaces.services;

import com.gcg.djs.domain.common.QueryParameters;
import com.gcg.djs.domain.models.errors.ValidationException;
import com.gcg.djs.domain.models.jobs.*;
import com.gcg.djs.domain.common.Page;

import java.util.UUID;

public interface IJobService {

    /**
     * Adds a new job to the system.
     *
     * @param job The details of the job to create.
     * @return The created Job object.
     * @throws ValidationException If the provided job details are invalid.
     */
    Job addJob(CreateJob job) throws ValidationException;

    /**
     * Modifies the details of an existing job.
     *
     * @param id The ID of the job to modify.
     * @param job The new job details to update.
     * @return The updated Job object.
     * @throws ValidationException If the provided update details are invalid.
     */
    Job modifyJob(UUID id, UpdateJob job) throws ValidationException;

    /**
     * Removes a job from the system.
     *
     * @param id The ID of the job to remove.
     * @return true if the job was successfully removed, false otherwise.
     * @throws ValidationException If the job cannot be removed due to validation issues.
     */
    boolean removeJob(UUID id) throws ValidationException;

    /**
     * Retrieves a job by its ID.
     *
     * @param id The ID of the job to retrieve.
     * @return The Job object corresponding to the given ID.
     */
    Job getJobById(UUID id) throws ValidationException;

    /**
     * Retrieves a paginated list of jobs, possibly filtered by certain criteria.
     *
     * @param page The page number to retrieve.
     * @param pageSize The number of jobs per page.
     * @param queryParameters Query parameters to apply to the list of jobs.
     * @return A Page containing a list of jobs for the specified page.
     * @throws ValidationException If the pagination or filter parameters are invalid.
     */
    Page<Job> searchJobs(int page, int pageSize, QueryParameters queryParameters) throws ValidationException;
}

