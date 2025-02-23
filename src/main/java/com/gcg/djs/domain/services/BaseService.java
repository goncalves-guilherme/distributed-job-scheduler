package com.gcg.djs.domain.services;

import com.gcg.djs.domain.interfaces.external.ILog;
import com.gcg.djs.domain.common.CheckedSupplier;
import com.gcg.djs.domain.models.errors.UnExpectedException;
import com.gcg.djs.domain.models.errors.ValidationException;

import java.util.Objects;

/**
 * The {@link BaseService} class is an abstract base class for services that require logging
 * and error handling functionality. It provides common methods that can be utilized by subclasses
 * to handle operations with automatic logging and exception handling.
 * <p>
 * This class defines a generic error-handling mechanism to wrap operations in a try-catch block,
 * ensuring that unexpected exceptions are logged and rethrown as {@link UnExpectedException}.
 * Subclasses can leverage this functionality by calling {@link #executeWithErrorHandler} in their methods.
 */
public abstract class BaseService {
    protected final ILog log;

    /**
     * Constructor for {@link BaseService} that initializes the log instance.
     *
     * @param log The {@link ILog} instance used for logging.
     * @throws NullPointerException if the provided {@code log} is {@code null}.
     */
    public BaseService(ILog log){
        this.log = Objects.requireNonNull(log);
    }

    /**
     * Executes the given operation and handles any exceptions that may occur.
     * If a {@link ValidationException} is thrown, it is rethrown without modification.
     * Any other exceptions are logged and wrapped in an {@link UnExpectedException}.
     *
     * @param operation The operation to execute, represented as a {@link CheckedSupplier}.
     * @param <T> The return type of the operation.
     * @return The result of the operation if successful.
     * @throws ValidationException if the operation throws a {@link ValidationException}.
     * @throws UnExpectedException if an unexpected exception occurs during the operation.
     */
    protected final <T> T executeWithErrorHandler(CheckedSupplier<T> operation) throws ValidationException {
        try {
            return operation.get();
        } catch (ValidationException e) {
            throw e;
        } catch (Exception e) {
            this.log.logError("Unexpected error occurred", e);
            throw new UnExpectedException();
        }
    }
}
