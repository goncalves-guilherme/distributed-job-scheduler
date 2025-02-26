package com.gcg.djs.domain.common.filters;

import java.util.Objects;

public final class StringFilter implements Filter {
    private final StringOperator operator;
    private final String fieldName;
    private final String value;

    public StringFilter(StringOperator operator, String fieldName, String value) {
        this.operator = Objects.requireNonNull(operator);
        this.value = value;
        this.fieldName = fieldName;
    }

    public StringOperator getOperator() {
        return operator;
    }

    public String getFieldName() {
        return fieldName;
    }

    public String getValue() {
        return value;
    }
}
