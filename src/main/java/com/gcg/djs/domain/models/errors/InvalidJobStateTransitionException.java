package com.gcg.djs.domain.models.errors;

import com.gcg.djs.domain.models.jobs.JobStatus;

import java.util.stream.Collectors;
import java.util.stream.Stream;

public class InvalidJobStateTransitionException extends RuntimeException {
    public InvalidJobStateTransitionException(JobStatus current,
                                              JobStatus transition,
                                              Stream<JobStatus> validStatesForTransition) {

        super(buildMessage(current, transition, validStatesForTransition));
    }

    private static String buildMessage(
            JobStatus current,
            JobStatus transition,
            Stream<JobStatus> validStatesForTransition) {

        String validStates = validStatesForTransition
                .map(Object::toString)
                .collect(Collectors.joining(", "));

        return String.format(
                "Error: The status '%s' cannot be transitioned to '%s'. " +
                        "Valid states for transitioning to '%s' are: %s. ",
                current,
                transition,
                transition,
                validStates);
    }
}
