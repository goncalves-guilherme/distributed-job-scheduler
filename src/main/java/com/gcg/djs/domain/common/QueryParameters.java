package com.gcg.djs.domain.common;

import com.gcg.djs.domain.common.filters.Filter;
import com.gcg.djs.domain.common.sorts.Sort;

import java.util.List;
import java.util.Objects;

/**
 * Represents the parameters for a query, including filters and sorting.
 * This is a record class which is a simple, immutable data structure.
 *
 * <p>Query parameters are typically used to define the criteria for querying
 * data, including the filters to apply and the sorting order of the results.
 * The class ensures that both filters and sorts are provided and are non-null.
 *
 * @param filters A list of {@link Filter} objects used to apply conditions to the query.
 *                Cannot be null.
 * @param sorts A list of {@link Sort} objects specifying the sorting order for the query.
 *              Cannot be null.
 */
public record QueryParameters(List<Filter> filters, List<Sort> sorts) {

    /**
     * Constructor that validates that neither filters nor sorts are null.
     *
     * @throws NullPointerException if either filters or sorts is null.
     */
    public QueryParameters {
        Objects.requireNonNull(filters);
        Objects.requireNonNull(sorts);
    }
}
