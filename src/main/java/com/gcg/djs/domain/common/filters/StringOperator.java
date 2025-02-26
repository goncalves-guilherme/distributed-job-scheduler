package com.gcg.djs.domain.common.filters;

public enum StringOperator {
    LIKE("LIKE");

    private final String symbol;

    StringOperator(String symbol) {
        this.symbol = symbol;
    }

    @Override
    public String toString() {
        return symbol;
    }
}
