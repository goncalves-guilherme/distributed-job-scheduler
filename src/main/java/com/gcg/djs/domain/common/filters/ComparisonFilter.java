package com.gcg.djs.domain.common.filters;

import org.apache.logging.log4j.util.Strings;

import java.util.Objects;

public abstract sealed class ComparisonFilter<T> implements Filter permits NumberFilter, InstantFilter {
    private final String fieldName;
    private final ComparisonOperator operator;
    private final T value;

    public ComparisonFilter(ComparisonOperator operator, String fieldName, T value) {
        this.fieldName = Objects.requireNonNull(Strings.trimToNull(fieldName));
        this.operator = Objects.requireNonNull(operator);
        this.value = Objects.requireNonNull(value);
    }

    public String getFieldName() {
        return fieldName;
    }

    public ComparisonOperator getOperator() {
        return operator;
    }

    public T getValue() {
        return value;
    }
}
