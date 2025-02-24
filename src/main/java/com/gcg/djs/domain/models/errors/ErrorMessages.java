package com.gcg.djs.domain.models.errors;

public final class ErrorMessages {
    public static final String CREATE_JOB_NULL = "CreateJob parameter cannot be null.";
    public static final String JOB_NOT_FOUND = "Job with ID %s not found.";
    public static final String JOP_ID_NULL = "Job ID must not be null";
    public static final String JOB_NAME_INVALID = "Job name must not be null, empty, or whitespace.";
    public static final String JOB_DESCRIPTION_INVALID = "Job description must not be null, empty, or whitespace.";
    public static final String JOB_BIN_LOCATION_INVALID = "Job bin location must not be null, empty, or whitespace.";
    public static final String JOB_UPDATE_NAME_INVALID = "Job update name must not be null, empty, or whitespace.";
    public static final String JOB_UPDATE_DESCRIPTION_INVALID = "Job update description must not be null, empty, or whitespace.";
    public static final String JOB_NEXT_EXECUTION_INVALID = "Next Execution date must not be null";
    public static final String UNEXPECTED_ERROR = "Unexpected error occurred";

    public static final String CREATE_RECURRING_JOB_NULL = "CreateRecurringJob parameter cannot be null.";
    public static final String RECURRING_JOP_ID_NULL = "Recurring job ID must not be null";
    public static final String RECURRING_JOB_NAME_INVALID = "Recurring job name must not be null, empty, or whitespace.";
    public static final String RECURRING_JOB_DESCRIPTION_INVALID = "Recurring job description must not be null, empty, or whitespace.";
    public static final String RECURRING_JOB_CRON_INVALID = "Recurring job cron must not be null, empty, or whitespace.";
    public static final String RECURRING_JOB_NOT_FOUND = "Recurring job with ID %s not found.";
}
