package com.gcg.djs.domain.common.filters;

public final class NumberFilter extends ComparisonFilter<Number> {
    public NumberFilter(String fieldName, ComparisonOperator operator, Number value) {
        super(fieldName, operator, value);
    }
}
