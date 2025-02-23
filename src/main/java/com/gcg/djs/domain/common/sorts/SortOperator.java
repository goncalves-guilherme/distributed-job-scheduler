package com.gcg.djs.domain.common.sorts;

/**
 * Represents the sorting direction for query results.
 * This enum defines the available sorting operators: ascending (ASC) and descending (DESC).
 */
public enum SortOperator {
    ASC("ASC"),
    DESC("DESC");

    private final String symbol;

    SortOperator(String symbol) {
        this.symbol = symbol;
    }

    @Override
    public String toString() {
        return symbol;
    }
}
