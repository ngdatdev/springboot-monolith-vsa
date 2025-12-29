package com.vsa.ecommerce.common.repository;

import jakarta.persistence.EntityManager;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Default implementation of BaseRepository.
 */
public class BaseRepositoryImpl<T, ID extends Serializable> extends SimpleJpaRepository<T, ID>
        implements BaseRepository<T, ID> {

    private final EntityManager entityManager;

    public BaseRepositoryImpl(JpaEntityInformation<T, ?> entityInformation, EntityManager entityManager) {
        super(entityInformation, entityManager);
        this.entityManager = entityManager;
    }

    @Override
    @Transactional
    public void refresh(T entity) {
        entityManager.refresh(entity);
    }

    @Override
    @Transactional
    public void clear() {
        entityManager.clear();
    }

    @Override
    @Transactional
    public void detach(T entity) {
        entityManager.detach(entity);
    }

    @Override
    @Transactional
    public T saveIfNew(T entity) {
        if (entity == null) {
            return null;
        }
        entityManager.persist(entity);
        return entity;
    }

    @Override
    @Transactional
    public List<T> batchSave(List<T> entities) {
        List<T> saved = new ArrayList<>();
        int i = 0;
        for (T entity : entities) {
            entityManager.persist(entity);
            i++;
            if (i % 50 == 0) { // Flush and clear every 50 to avoid memory issues
                entityManager.flush();
                entityManager.clear();
            }
            saved.add(entity);
        }
        if (i % 50 != 0) {
            entityManager.flush();
            entityManager.clear();
        }
        return saved;
    }
}
