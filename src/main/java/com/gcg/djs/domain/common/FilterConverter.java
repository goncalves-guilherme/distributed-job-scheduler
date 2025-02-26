package com.gcg.djs.domain.common;

import com.gcg.djs.domain.common.filters.ComparisonFilter;
import com.gcg.djs.domain.common.filters.Filter;
import com.gcg.djs.domain.common.filters.LogicalFilter;
import com.gcg.djs.domain.common.filters.StringFilter;
import org.bson.conversions.Bson;

import java.util.List;

/**
 * Interface for converting filters of type {@link Filter} into a different representation.
 *
 * <p>This interface defines methods for converting two types of filters:
 * - {@link ComparisonFilter}, which involves comparisons between values.
 * - {@link LogicalFilter}, which combines multiple filters using logical operations (e.g., AND, OR).
 *
 * <p>Implementations of this interface should define how these filters are converted to another
 * type (denoted as {@link T}) suitable for further processing or transformation in the system.
 *
 * @param <T> The type to which filters are converted.
 */
public interface FilterConverter<T> {

    /**
     * Converts a {@link ComparisonFilter} to an instance of type {@link T}.
     * <p>
     * This method processes a comparison-based filter and transforms it into an appropriate
     * representation of type {@link T}. The implementation of this method defines how comparison
     * filters are handled and translated within the system.
     *
     * @param comparisonFilter The {@link ComparisonFilter} to be converted.
     * @return The converted filter of type {@link T}.
     */
    T convertComparison(ComparisonFilter<?> comparisonFilter);

    /**
     * Converts a {@link LogicalFilter} to an instance of type {@link T} using a list of filters.
     * <p>
     * This method processes a logical filter, which can combine multiple individual filters,
     * and converts it into an appropriate representation of type {@link T}. It handles logical
     * operations (such as AND, OR) applied to a list of filters.
     *
     * @param logicalFilter The {@link LogicalFilter} to be converted.
     * @param filters A list of filters of type {@link T} that the logical filter operates on.
     * @return The converted filter of type {@link T} resulting from the logical operation.
     */
    T convertLogical(LogicalFilter logicalFilter, List<T> filters);

    /**
     * Converts a {@link StringFilter} to a {@link Bson} representation.
     * <p>
     * This method processes a string-based filter and converts it into a {@link Bson} format
     * suitable for use in a MongoDB query or similar database operations.
     * It handles operations such as "LIKE" or other string-based comparisons.
     *
     * @param stringFilter The {@link StringFilter} to be converted.
     * @return The converted filter as a {@link Bson} instance, representing the string filter in query form.
     */
    T convertString(StringFilter stringFilter);
}