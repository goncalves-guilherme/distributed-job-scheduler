package com.gcg.djs.domain.common;

import com.gcg.djs.domain.common.filters.*;
import com.gcg.djs.domain.common.sorts.SortOperator;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

public class QueryParameterBuilderTests {

    @Test
    public void build_withoutStartingWithLogicalFilter() {
        // Arrange
        var queryParamBuilder = new QueryParametersBuilder().greaterThan("age", 5);

        // Act & Assert
        RuntimeException exception = assertThrowsExactly(
                RuntimeException.class,
                queryParamBuilder::build
        );

        // Assert exception message
        assertEquals("Filters should be started with logical parameter", exception.getMessage());
    }

    @Test
    public void build_withEmptyLogicalFilter() {
        // Arrange
        var queryParamBuilder = new QueryParametersBuilder().or();

        // Act & Assert
        RuntimeException exception = assertThrowsExactly(
                RuntimeException.class,
                queryParamBuilder::build
        );

        // Assert exception message
        assertEquals("Logic operator cannot be empty", exception.getMessage());
    }

    @Test
    public void build_withValidQueryParameters() {
        // Arrange
        var queryParametersBuilder = new QueryParametersBuilder()
                .and()
                    .or() // Testing Instants
                        .equal("date", Instant.now())
                        .greaterThan("date2", Instant.now().plusSeconds(60))
                        .lessThan("date3", Instant.now().plusSeconds(60*2))
                        .greaterThanOrEqual("date4", Instant.now().plusSeconds(60*3))
                        .lessThanOrEqual("date5", Instant.now().plusSeconds(60*4))
                    .or() // Testing Numbers
                        .equal("age", 5)
                        .greaterThan("age2", 10)
                        .lessThan("age3", 15)
                        .greaterThanOrEqual("age4", 20)
                        .lessThanOrEqual("age5", 25)
                .sortByAsc("age")
                .sortByDesc("test");

        // Act
        var actual = queryParametersBuilder.build();

        // Assert
        assertNotNull(actual);
        assertEquals(13, actual.filters().size());

        assertLogicalFilter(actual.filters().get(0), LogicalOperator.AND);

        assertLogicalFilter(actual.filters().get(1), LogicalOperator.OR);
        assertInstantFilter(actual.filters().get(2), "date", ComparisonOperator.EQUAL);
        assertInstantFilter(actual.filters().get(3), "date2", ComparisonOperator.GREATER_THAN);
        assertInstantFilter(actual.filters().get(4), "date3", ComparisonOperator.LESS_THAN);
        assertInstantFilter(actual.filters().get(5), "date4", ComparisonOperator.GREATER_THAN_OR_EQUAL);
        assertInstantFilter(actual.filters().get(6), "date5", ComparisonOperator.LESS_THAN_OR_EQUAL);

        assertLogicalFilter(actual.filters().get(7), LogicalOperator.OR);
        assertNumberFilter(actual.filters().get(8), "age", ComparisonOperator.EQUAL, 5);
        assertNumberFilter(actual.filters().get(9), "age2", ComparisonOperator.GREATER_THAN, 10);
        assertNumberFilter(actual.filters().get(10), "age3", ComparisonOperator.LESS_THAN, 15);
        assertNumberFilter(actual.filters().get(11), "age4", ComparisonOperator.GREATER_THAN_OR_EQUAL, 20);
        assertNumberFilter(actual.filters().get(12), "age5", ComparisonOperator.LESS_THAN_OR_EQUAL, 25);

        assertEquals(2, actual.sorts().size());

        assertEquals("age", actual.sorts().get(0).fieldName());
        assertEquals(SortOperator.ASC, actual.sorts().get(0).operator());

        assertEquals("test", actual.sorts().get(1).fieldName());
        assertEquals(SortOperator.DESC, actual.sorts().get(1).operator());
    }

    private static void assertLogicalFilter(Filter logicalFilter, LogicalOperator expectedOperator) {
        assertInstanceOf(LogicalFilter.class, logicalFilter);
        LogicalFilter firstFilter = (LogicalFilter) logicalFilter;
        assertEquals(expectedOperator, firstFilter.getOperator());
    }

    private static void assertInstantFilter(
            Filter filter, String expectedField, ComparisonOperator expectedOperator) {

        assertInstanceOf(InstantFilter.class, filter);
        InstantFilter instantFilter = (InstantFilter) filter;
        assertEquals(expectedField, instantFilter.getFieldName());
        assertEquals(expectedOperator, instantFilter.getOperator());
    }

    private static void assertNumberFilter(
            Filter filter, String expectedField, ComparisonOperator expectedOperator, Number expectValue) {

        assertInstanceOf(NumberFilter.class, filter);
        NumberFilter numberFilter = (NumberFilter) filter;
        assertEquals(expectedField, numberFilter.getFieldName());
        assertEquals(expectedOperator, numberFilter.getOperator());
        assertEquals(numberFilter.getValue(), expectValue);
    }
}
