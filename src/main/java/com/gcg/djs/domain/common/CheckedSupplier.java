package com.gcg.djs.domain.common;

import com.gcg.djs.domain.models.errors.ValidationException;

/**
 * A functional interface for a supplier that can throw a {@link ValidationException}.
 *
 * <p>This interface extends the concept of a {@link java.util.function.Supplier} by allowing the
 * operation to throw a {@link ValidationException}, which makes it suitable for scenarios where
 * validation-related errors can occur during the supplier's execution.
 *
 * <p>This interface is intended to be used with lambda expressions or method references, enabling
 * concise handling of operations that may fail due to validation issues.
 *
 * @param <T> The type of the result supplied by this interface.
 * @see java.util.function.Supplier
 * @see ValidationException
 */
@FunctionalInterface
public interface CheckedSupplier<T> {
    /**
     * Gets a result, possibly throwing a {@link ValidationException}.
     * <p>
     * This is a functional interface designed for lambda expressions or method references.
     * It is similar to {@link java.util.function.Supplier} but allows the operation
     * to throw a {@link ValidationException} in case of validation-related errors.
     *
     * @return The result of the supplier's operation.
     * @throws ValidationException If a validation error occurs during the operation.
     */
    T get() throws ValidationException;
}