package com.gcg.djs.domain.common;

import com.gcg.djs.domain.common.filters.*;
import com.gcg.djs.domain.common.sorts.Sort;
import com.gcg.djs.domain.common.sorts.SortOperator;
import org.apache.logging.log4j.util.Strings;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * A builder class to construct {@link QueryParameters} with a flexible set of filters and sorting options.
 * The builder allows you to chain various methods to add filters and sort conditions to a query.
 *
 * <p>This builder supports logical filters (AND, OR), comparison filters (EQUAL, GREATER_THAN, etc.) on both
 * {@link Number} and {@link Instant} types, as well as sorting the query results in ascending or descending order.
 * It enforces constraints, such as ensuring that filters start with a logical operator and that logical operators
 * cannot be empty.
 */
public final class QueryParametersBuilder {
    private final List<Filter> filters = new ArrayList<>();
    private final List<Sort> sorts = new ArrayList<>();

    /**
     * Adds a logical AND operator to the filters.
     * This method allows for combining subsequent filters with a logical AND condition.
     *
     * @return this builder instance, allowing method chaining.
     */
    public QueryParametersBuilder and() {
        filters.add(new LogicalFilter(LogicalOperator.AND));
        return this;
    }

    /**
     * Adds a logical OR operator to the filters.
     * This method allows for combining subsequent filters with a logical OR condition.
     *
     * @return this builder instance, allowing method chaining.
     */
    public QueryParametersBuilder or() {
        filters.add(new LogicalFilter(LogicalOperator.OR));
        return this;
    }

    /**
     * Adds an EQUAL comparison filter for a number field.
     *
     * @param fieldName The field name to compare.
     * @param number The number value to compare with.
     * @return this builder instance, allowing method chaining.
     */
    public QueryParametersBuilder equal(String fieldName, Number number) {
        addComparisonFilter(fieldName, ComparisonOperator.EQUAL, number);
        return this;
    }

    /**
     * Adds an EQUAL comparison filter for an Instant field.
     *
     * @param fieldName The field name to compare.
     * @param instant The Instant value to compare with.
     * @return this builder instance, allowing method chaining.
     */
    public QueryParametersBuilder equal(String fieldName, Instant instant) {
        addComparisonFilter(fieldName, ComparisonOperator.EQUAL, instant);
        return this;
    }

    /**
     * Adds a GREATER_THAN comparison filter for a number field.
     *
     * @param fieldName The field name to compare.
     * @param number The number value to compare with.
     * @return this builder instance, allowing method chaining.
     */
    public QueryParametersBuilder greaterThan(String fieldName, Number number) {
        addComparisonFilter(fieldName, ComparisonOperator.GREATER_THAN, number);
        return this;
    }

    /**
     * Adds a GREATER_THAN comparison filter for an Instant field.
     *
     * @param fieldName The field name to compare.
     * @param instant The Instant value to compare with.
     * @return this builder instance, allowing method chaining.
     */
    public QueryParametersBuilder greaterThan(String fieldName, Instant instant) {
        addComparisonFilter(fieldName, ComparisonOperator.GREATER_THAN, instant);
        return this;
    }

    /**
     * Adds a GREATER_THAN_OR_EQUAL comparison filter for a number field.
     *
     * @param fieldName The field name to compare.
     * @param number The number value to compare with.
     * @return this builder instance, allowing method chaining.
     */
    public QueryParametersBuilder greaterThanOrEqual(String fieldName, Number number) {
        addComparisonFilter(fieldName, ComparisonOperator.GREATER_THAN_OR_EQUAL, number);
        return this;
    }

    /**
     * Adds a GREATER_THAN_OR_EQUAL comparison filter for an Instant field.
     *
     * @param fieldName The field name to compare.
     * @param instant The Instant value to compare with.
     * @return this builder instance, allowing method chaining.
     */
    public QueryParametersBuilder greaterThanOrEqual(String fieldName, Instant instant) {
        addComparisonFilter(fieldName, ComparisonOperator.GREATER_THAN_OR_EQUAL, instant);
        return this;
    }

    /**
     * Adds a LESS_THAN comparison filter for a number field.
     *
     * @param fieldName The field name to compare.
     * @param number The number value to compare with.
     * @return this builder instance, allowing method chaining.
     */
    public QueryParametersBuilder lessThan(String fieldName, Number number) {
        addComparisonFilter(fieldName, ComparisonOperator.LESS_THAN, number);
        return this;
    }

    /**
     * Adds a LESS_THAN comparison filter for an Instant field.
     *
     * @param fieldName The field name to compare.
     * @param instant The Instant value to compare with.
     * @return this builder instance, allowing method chaining.
     */
    public QueryParametersBuilder lessThan(String fieldName, Instant instant) {
        addComparisonFilter(fieldName, ComparisonOperator.LESS_THAN, instant);
        return this;
    }

    /**
     * Adds a LESS_THAN_OR_EQUAL comparison filter for a number field.
     *
     * @param fieldName The field name to compare.
     * @param number The number value to compare with.
     * @return this builder instance, allowing method chaining.
     */
    public QueryParametersBuilder lessThanOrEqual(String fieldName, Number number) {
        addComparisonFilter(fieldName, ComparisonOperator.LESS_THAN_OR_EQUAL, number);
        return this;
    }

    /**
     * Adds a LESS_THAN_OR_EQUAL comparison filter for an Instant field.
     *
     * @param fieldName The field name to compare.
     * @param instant The Instant value to compare with.
     * @return this builder instance, allowing method chaining.
     */
    public QueryParametersBuilder lessThanOrEqual(String fieldName, Instant instant) {
        addComparisonFilter(fieldName, ComparisonOperator.LESS_THAN_OR_EQUAL, instant);
        return this;
    }

    /**
     * Adds an ascending sort condition on the given field.
     *
     * @param fieldName The field to sort by.
     * @return this builder instance, allowing method chaining.
     * @throws NullPointerException if the field name is null or empty.
     */
    public QueryParametersBuilder sortByAsc(String fieldName) {
        Objects.requireNonNull(Strings.trimToNull(fieldName), "Field name cannot be null or empty");
        sorts.add(new Sort(fieldName, SortOperator.ASC));
        return this;
    }

    /**
     * Adds a descending sort condition on the given field.
     *
     * @param fieldName The field to sort by.
     * @return this builder instance, allowing method chaining.
     * @throws NullPointerException if the field name is null or empty.
     */
    public QueryParametersBuilder sortByDesc(String fieldName) {
        Objects.requireNonNull(Strings.trimToNull(fieldName), "Field name cannot be null or empty");
        sorts.add(new Sort(fieldName, SortOperator.DESC));
        return this;
    }

    private void addComparisonFilter(String fieldName, ComparisonOperator operator, Object value) {
        Objects.requireNonNull(Strings.trimToNull(fieldName));
        Objects.requireNonNull(operator);
        Objects.requireNonNull(value);

        Filter filter;
        if (value instanceof Number) {
            filter = new NumberFilter(fieldName, operator, (Number) value);
        } else if (value instanceof Instant) {
            filter = new InstantFilter(fieldName, operator, (Instant) value);
        } else {
            throw new IllegalArgumentException("Unsupported value type: " + value.getClass());
        }

        filters.add(filter);
    }

    /**
     * Builds the {@link QueryParameters} object, validating the constructed filters and sorts.
     * Ensures that filters start with a logical operator and that logical operators cannot be empty.
     *
     * @return A new instance of {@link QueryParameters} containing the constructed filters and sorts.
     * @throws RuntimeException if the filter sequence is invalid (e.g., missing logical operator at the start).
     */
    public QueryParameters build() {
        // Validates that filters start with a logical operator and do not end with one
        if (!filters.isEmpty() && !(filters.get(0) instanceof LogicalFilter)) {
            throw new RuntimeException("Filters should be started with logical parameter");
        }

        if (!filters.isEmpty() && filters.get(filters.size() - 1) instanceof LogicalFilter) {
            throw new RuntimeException("Logic operator cannot be empty");
        }

        // Returns a new QueryParameters object based on the current filters and sorts
        return new QueryParameters(filters.stream().toList(), sorts);
    }
}
