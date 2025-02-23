package com.gcg.djs.domain.models.errors;

public class UnExpectedException extends RuntimeException {
    public UnExpectedException() {
        super("Unexpected exception occurred");
    }
}
