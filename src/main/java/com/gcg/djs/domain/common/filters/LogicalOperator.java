package com.gcg.djs.domain.common.filters;

public enum LogicalOperator {
    AND("&"),
    OR("||");

    private final String symbol;

    LogicalOperator(String symbol) {
        this.symbol = symbol;
    }

    @Override
    public String toString() {
        return symbol;
    }
}