package com.gcg.djs.domain.common.filters;

import java.time.Instant;

public final class InstantFilter extends ComparisonFilter<Instant> {
    public InstantFilter(String fieldName, ComparisonOperator operator, Instant value) {
        super(fieldName, operator, value);
    }
}