package com.gcg.djs.infrastructure.mongodb;

import com.gcg.djs.domain.common.filters.ComparisonOperator;
import com.gcg.djs.domain.common.filters.NumberFilter;
import com.gcg.djs.domain.common.filters.ComparisonFilter;
import com.gcg.djs.domain.common.filters.LogicalFilter;
import com.gcg.djs.domain.common.filters.LogicalOperator;
import com.gcg.djs.infrastructure.mongdb.MongoFilterConverter;
import org.bson.conversions.Bson;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class MongoFilterConverterTests {

    private final MongoFilterConverter converter = MongoFilterConverter.instance;

    @Test
    public void convertComparison_equalOperator_shouldReturnEqualFilter() {
        // Arrange
        ComparisonFilter<?> comparisonFilter = new NumberFilter(
                "FieldName", ComparisonOperator.EQUAL, 5);

        // Act
        Bson actualBson = converter.convertComparison(comparisonFilter);

        // Assert
        assertNotNull(actualBson);
        assertEquals("Filter{fieldName='FieldName', value=5}", actualBson.toString());
    }

    @Test
    public void convertComparison_greaterThanOperator_shouldReturnGreaterThanFilter() {
        // Arrange
        ComparisonFilter<?> comparisonFilter = new NumberFilter(
                "FieldName", ComparisonOperator.GREATER_THAN, 5);

        // Act
        Bson actualBson = converter.convertComparison(comparisonFilter);

        // Assert
        assertNotNull(actualBson);
        assertEquals("Operator Filter{fieldName='FieldName', operator='$gt', value=5}", actualBson.toString());
    }

    @Test
    public void convertComparison_greaterThanOrEqualOperator_shouldReturnGreaterThanOrEqualFilter() {
        // Arrange
        ComparisonFilter<?> comparisonFilter = new NumberFilter("FieldName", ComparisonOperator.GREATER_THAN_OR_EQUAL, 5);

        // Act
        Bson actualBson = converter.convertComparison(comparisonFilter);

        // Assert
        assertNotNull(actualBson);
        assertEquals("Operator Filter{fieldName='FieldName', operator='$gte', value=5}", actualBson.toString());
    }

    @Test
    public void convertComparison_lessThanOperator_shouldReturnLessThanFilter() {
        // Arrange
        ComparisonFilter<?> comparisonFilter = new NumberFilter("FieldName", ComparisonOperator.LESS_THAN, 5);

        // Act
        Bson actualBson = converter.convertComparison(comparisonFilter);

        // Assert
        assertNotNull(actualBson);
        assertEquals("Operator Filter{fieldName='FieldName', operator='$lt', value=5}", actualBson.toString());
    }

    @Test
    public void convertComparison_lessThanOrEqualOperator_shouldReturnLessThanOrEqualFilter() {
        // Arrange
        ComparisonFilter<?> comparisonFilter = new NumberFilter("FieldName", ComparisonOperator.LESS_THAN_OR_EQUAL, 5);

        // Act
        Bson actualBson = converter.convertComparison(comparisonFilter);

        // Assert
        assertNotNull(actualBson);
        assertEquals("Operator Filter{fieldName='FieldName', operator='$lte', value=5}", actualBson.toString());
    }

    @Test
    public void convertLogical_andOperator_shouldReturnAndFilter() {
        // Arrange
        LogicalFilter logicalFilter = new LogicalFilter(LogicalOperator.AND);

        // Act
        Bson actualBson = converter.convertLogical(logicalFilter, List.of());

        // Assert
        assertNotNull(actualBson);
        assertEquals("And Filter{filters=[]}", actualBson.toString());
    }

    @Test
    public void convertLogical_orOperator_shouldReturnOrFilter() {
        // Arrange
        LogicalFilter logicalFilter = new LogicalFilter(LogicalOperator.OR);

        // Act
        Bson actualBson = converter.convertLogical(logicalFilter, List.of());

        // Assert
        assertNotNull(actualBson);
        assertEquals("Or Filter{filters=[]}", actualBson.toString());
    }
}
