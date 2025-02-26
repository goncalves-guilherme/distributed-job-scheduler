package com.gcg.djs.domain.common.filters;

import java.time.Instant;

public final class InstantFilter extends ComparisonFilter<Instant> {
    public InstantFilter(ComparisonOperator operator, String fieldName, Instant value) {
        super(operator, fieldName, value);
    }
}