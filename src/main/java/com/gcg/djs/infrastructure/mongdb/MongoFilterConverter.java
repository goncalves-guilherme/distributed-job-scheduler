package com.gcg.djs.infrastructure.mongdb;

import com.gcg.djs.domain.common.FilterConverter;
import com.gcg.djs.domain.common.filters.*;
import com.mongodb.client.model.Filters;
import org.bson.conversions.Bson;

import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

public final class MongoFilterConverter implements FilterConverter<Bson> {
    public static final MongoFilterConverter instance = new MongoFilterConverter();

    private MongoFilterConverter(){}

    private static final Map<LogicalOperator, Function<List<Bson>, Bson>> LOGICAL_OPERATOR_MAP = Map.of(
            LogicalOperator.AND, Filters::and,
            LogicalOperator.OR, Filters::or
    );

    private static final Map<ComparisonOperator, BiFunction<String, Object, Bson>> OPERATOR_MAP = Map.of(
            ComparisonOperator.EQUAL, Filters::eq,
            ComparisonOperator.GREATER_THAN, Filters::gt,
            ComparisonOperator.GREATER_THAN_OR_EQUAL, Filters::gte,
            ComparisonOperator.LESS_THAN, Filters::lt,
            ComparisonOperator.LESS_THAN_OR_EQUAL, Filters::lte
    );

    private static final Map<StringOperator, BiFunction<String, Object, Bson>> STRING_OPERATOR_MAP = Map.of(
            StringOperator.LIKE, Filters::eq
    );

    @Override
    public Bson convertString(StringFilter stringFilter) {
        String fieldName = stringFilter.getFieldName();
        StringOperator operator = stringFilter.getOperator();
        Object value = stringFilter.getValue();

        BiFunction<String, Object, Bson> filterFunction = STRING_OPERATOR_MAP.get(operator);
        if (filterFunction == null) {
            throw new UnsupportedOperationException("Unsupported operator: " + operator);
        }
        return filterFunction.apply(fieldName, value);
    }

    @Override
    public Bson convertComparison(ComparisonFilter<?> comparisonFilter) {
        String fieldName = comparisonFilter.getFieldName();
        ComparisonOperator operator = comparisonFilter.getOperator();
        Object value = comparisonFilter.getValue();

        BiFunction<String, Object, Bson> filterFunction = OPERATOR_MAP.get(operator);
        if (filterFunction == null) {
            throw new UnsupportedOperationException("Unsupported operator: " + operator);
        }
        return filterFunction.apply(fieldName, value);
    }

    @Override
    public Bson convertLogical(LogicalFilter logicalFilter, List<Bson> filters) {
        var filterFunction = LOGICAL_OPERATOR_MAP.get(logicalFilter.getOperator());
        if (filterFunction == null) {
            throw new UnsupportedOperationException("Unsupported operator: " + logicalFilter.getOperator());
        }

        return filterFunction.apply(filters);
    }
}
