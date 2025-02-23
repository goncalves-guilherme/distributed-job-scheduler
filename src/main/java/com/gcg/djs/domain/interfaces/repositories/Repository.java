package com.gcg.djs.domain.interfaces.repositories;

import com.gcg.djs.domain.common.Page;
import com.gcg.djs.domain.common.QueryParameters;

import java.util.UUID;

public interface Repository<T> {

    /**
     * Creates a new entity in the repository.
     *
     * @param entity The entity to be created.
     * @return The created entity, including any auto-generated fields (e.g., ID).
     */
    T create(T entity);

    /**
     * Updates an existing entity in the repository.
     *
     * @param entity The entity with updated data.
     * @return The updated entity.
     */
    T update(T entity);

    /**
     * Deletes an entity from the repository by its unique identifier.
     *
     * @param id The unique identifier of the entity to be deleted.
     * @return True if the entity was successfully deleted, false otherwise.
     */
    boolean delete(UUID id);

    /**
     * Retrieves an entity by its unique identifier.
     *
     * @param id The unique identifier of the entity.
     * @return The entity if found, or null if not found.
     */
    T getById(UUID id);

    /**
     * Retrieves a paginated list of entities based on the specified parameters.
     *
     * @param page The page number (starting from 1).
     * @param pageSize The number of items per page.
     * @param parameters Query parameters used to filter or sort the results.
     * @return A Page containing the entities for the requested page.
     */
    Page<T> getPage(int page, int pageSize, QueryParameters parameters);
}
