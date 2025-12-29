package com.vsa.ecommerce.common.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;

import java.io.Serializable;
import java.util.List;

/**
 * Base Repository interface to be extended by all specific repositories.
 * Adds common functionality beyond JpaRepository.
 *
 * @param <T>  Entity type
 * @param <ID> Entity ID type
 */
@NoRepositoryBean
public interface BaseRepository<T, ID extends Serializable> extends JpaRepository<T, ID>, JpaSpecificationExecutor<T> {

    /**
     * Refresh the state of the instance from the database,
     * overwriting changes made to the entity, if any.
     */
    void refresh(T entity);

    /**
     * Clear the persistence context, causing all managed entities to become
     * detached.
     */
    void clear();

    /**
     * Remove the given entity from the persistence context, causing a managed
     * entity to become detached.
     */
    void detach(T entity);

    /**
     * Save only if the entity is new.
     */
    T saveIfNew(T entity);

    /**
     * Bulk save (batch insert) bypasses some JPA overhead if implemented correctly.
     * (Standard saveAll uses loop).
     */
    List<T> batchSave(List<T> entities);
}
