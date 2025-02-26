package com.gcg.djs.domain.common.filters;

public final class NumberFilter extends ComparisonFilter<Number> {
    public NumberFilter(ComparisonOperator operator, String fieldName, Number value) {
        super(operator, fieldName, value);
    }
}
