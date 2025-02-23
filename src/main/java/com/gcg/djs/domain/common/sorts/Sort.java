package com.gcg.djs.domain.common.sorts;

import org.apache.logging.log4j.util.Strings;

import java.util.Objects;

public record Sort(String fieldName, SortOperator operator) {

    public Sort {
        Objects.requireNonNull(Strings.trimToNull(fieldName));
        Objects.requireNonNull(operator);
    }
}
