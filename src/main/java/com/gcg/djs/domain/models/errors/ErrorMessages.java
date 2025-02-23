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
    public static final String JOB_PAGE_NUMBER_INVALID = "Page number must not be smaller or equal to 0";
    public static final String JOB_PAGE_SIZE_INVALID = "Page size must not be smaller or equal to 0";
    public static final String JOB_NEXT_EXECUTION_INVALID = "Next Execution date must not be null";
    public static final String UNEXPECTED_ERROR = "Unexpected error occurred";
}
