package com.gcg.djs.domain.common.filters;

import java.util.Objects;

public final class LogicalFilter implements Filter {
    private final LogicalOperator operator;

    public LogicalFilter(LogicalOperator operator) {
        this.operator = Objects.requireNonNull(operator);
    }

    public LogicalOperator getOperator() {
        return operator;
    }
}
