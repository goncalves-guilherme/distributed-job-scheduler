package com.gcg.djs.domain.models.errors;

import java.util.Collections;
import java.util.List;

public class ValidationException extends Exception {
    private final List<String> errors;

    public ValidationException(List<String> errors) {
        this.errors = Collections.unmodifiableList(errors);
    }

    @Override
    public String getMessage() {
        return String.join(", ", errors);
    }
}
