package com.gcg.djs.domain.common;

import com.gcg.djs.domain.common.filters.*;
import org.bson.conversions.Bson;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class QueryBuilderTests {
    @Test
    public void buildQuery_withComparisonFilter_shouldReturnExpectedQuery() {
        // Arrange
        var instant = Instant.now();

        var filters = List.of(
                new LogicalFilter(LogicalOperator.OR),
                new NumberFilter(ComparisonOperator.EQUAL, "a", 5),
                new LogicalFilter(LogicalOperator.AND),
                new NumberFilter(ComparisonOperator.GREATER_THAN, "a", 10),
                new InstantFilter(ComparisonOperator.GREATER_THAN, "a", instant),
                new StringFilter(StringOperator.LIKE, "str", "random")
        );

        var queryParameters = new QueryParameters(filters, List.of());

        var expectedFilter = "strLIKErandom&a>" + instant.toString() + "&a>10||a==5";

        // Act
        var actual = QueryBuilder.buildQuery(queryParameters, FilterConverterMock.instance);

        // Assert
        assertEquals(expectedFilter, actual);
    }


    private final static class FilterConverterMock implements FilterConverter<String> {
        public static final FilterConverterMock instance = new FilterConverterMock();

        @Override
        public String convertComparison(ComparisonFilter<?> comparisonFilter) {
            if (comparisonFilter instanceof NumberFilter numberFilter) {
                return numberFilter.getFieldName() +
                        comparisonFilter.getOperator().toString() +
                        numberFilter.getValue();
            } else if (comparisonFilter instanceof InstantFilter instantFilter) {
                return instantFilter.getFieldName() +
                        comparisonFilter.getOperator().toString() +
                        instantFilter.getValue().toString();
            }
            return "";
        }

        @Override
        public String convertLogical(LogicalFilter logicalFilter, List<String> filters) {
            String operator = logicalFilter.getOperator().toString();
            return String.join(operator, filters);
        }

        @Override
        public String convertString(StringFilter stringFilter) {
            return stringFilter.getFieldName() + stringFilter.getOperator() + stringFilter.getValue();
        }
    }
}