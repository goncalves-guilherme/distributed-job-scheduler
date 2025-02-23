package com.gcg.djs.domain.interfaces.external;

/**
 * The {@link ILog} interface defines the contract for logging operations. It provides methods
 * for logging messages of different severities, such as general messages, errors, information,
 * and warnings. Implementations of this interface should define how these logs are handled,
 * whether it's to a file, console, or external logging service.
 */
public interface ILog {

    /**
     * Logs a general message with a default log level (e.g., INFO).
     *
     * @param message The message to log.
     */
    void log(String message);

    /**
     * Logs an error message along with the associated exception.
     * This is typically used for capturing error conditions in the application.
     *
     * @param message The error message to log.
     * @param throwable The exception or error associated with the log entry.
     */
    void logError(String message, Throwable throwable);

    /**
     * Logs a warning message. This is used to log potential issues or situations that could
     * require attention but do not necessarily indicate an error.
     *
     * @param message The warning message to log.
     */
    void logWarning(String message);
}