package com.gcg.djs.domain.common;

import com.gcg.djs.domain.common.filters.ComparisonFilter;
import com.gcg.djs.domain.common.filters.Filter;
import com.gcg.djs.domain.common.filters.LogicalFilter;
import com.gcg.djs.domain.common.filters.StringFilter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Stack;

/**
 * The {@link QueryBuilder} class is responsible for constructing queries based on provided
 * {@link QueryParameters} and a {@link FilterConverter}. It processes filters in the specified
 * order, handling both logical and comparison filters to build the final query.
 * <p>
 * This class uses a stack-based approach to handle logical and comparison filters. It converts
 * the filters into query components using the provided {@link FilterConverter}, allowing dynamic
 * query construction for various use cases.
 */
public final class QueryBuilder {
    /**
     * Builds a query using the provided {@link QueryParameters} and {@link FilterConverter}.
     * It processes filters and generates a query representation based on the filter types (logical
     * or comparison) and their order.
     * <p>
     * The method performs the following steps:
     * 1. It initializes an empty list of filters and a stack to handle filter processing.
     * 2. It iterates over the filters, popping elements from the stack.
     * 3. Depending on the filter type (logical or comparison), the appropriate conversion method
     *    is invoked on the {@link FilterConverter}.
     * 4. The filters are processed, and the final query representation is returned.
     *
     * @param parameters The {@link QueryParameters} containing the filters to be used for query building.
     * @param filterConverter The {@link FilterConverter} responsible for converting the filters into query components.
     * @param <T> The type of the query representation.
     * @return The final query constructed from the filters.
     * @throws NullPointerException If either {@code parameters} or {@code filterConverter} is {@code null}.
     * @throws RuntimeException If an unexpected filter type is encountered.
     */
    public static <T> T buildQuery(QueryParameters parameters,
                               FilterConverter<T> filterConverter) {

        Objects.requireNonNull(parameters);
        Objects.requireNonNull(filterConverter);

        List<T> filters = new ArrayList<>();

        Stack<Filter> filterStack = new Stack<>();
        filterStack.addAll(parameters.filters());

        while(!filterStack.isEmpty()) {
            var filter = filterStack.pop();

            if (filter instanceof LogicalFilter logicalFilter) {
                T logicalQuery = filterConverter.convertLogical(logicalFilter, filters);
                filters = new ArrayList<>();
                filters.add(logicalQuery);
            } else if (filter instanceof ComparisonFilter<?> comparisonFilter) {
                T comparisonQuery = filterConverter.convertComparison(comparisonFilter);
                filters.add(comparisonQuery);
            } else if (filter instanceof StringFilter stringFilter) {
                T stringQuery = filterConverter.convertString(stringFilter);
                filters.add(stringQuery);
            }
            else {
                throw new RuntimeException("Unexpected filter type.");
            }
        }

        return filters.get(0);
    }
}
