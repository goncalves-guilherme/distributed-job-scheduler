package com.gcg.djs.domain.common;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * A record class representing a paginated set of results.
 *
 * <p>This class encapsulates the information related to a page of items, including the current page number,
 * the page size (i.e., the number of items per page), the total number of items across all pages, and the
 * actual items for the current page.
 *
 * <p>The class enforces validation rules to ensure the page number is greater than zero, the page size is
 * greater than zero, and the list of items is non-null.
 *
 * @param <T> The type of items in the page (e.g., a list of objects or entities).
 * @param page The current page number (1-based index).
 * @param pageSize The number of items per page.
 * @param totalItems The total number of items across all pages.
 * @param items The items on the current page.
 */
public record Page<T>(int page, int pageSize, long totalItems, List<T> items) {

    /**
     * Constructor that validates the page size and page number.
     * Throws {@link IllegalArgumentException} if pageSize is less than or equal to zero, or
     * if the page number is less than 1. Also ensures the items list is non-null.
     */
    public Page {
        var errors = validate(page, pageSize);

        if(!errors.isEmpty()) {
            throw new IllegalArgumentException(String.join(",", errors));
        }

        Objects.requireNonNull(items);
    }

    /**
     * Calculates the total number of pages based on the total number of items and the page size.
     *
     * <p>It uses {@link Math#ceil} to round up to the nearest whole number, ensuring that if there are
     * leftover items, an additional page is included.
     *
     * @return The total number of pages.
     */
    public int totalPages() {
        return (int) Math.ceil((double) totalItems / pageSize);
    }

    public static List<String> validate(int page, int pageSize) {
        List<String> errors  = new ArrayList<>();

        if(page <= 0) {
            errors.add(ErrorMessages.PAGE_NUMBER_INVALID);
        }
        if(pageSize <= 0) {
            errors.add(ErrorMessages.PAGE_SIZE_INVALID);
        }

        return errors;
    }
}
