package com.vsa.ecommerce.feature.user.list_users;

import com.vsa.ecommerce.domain.entity.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("ListUsersRepository")
public class ListUsersRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public List<User> findAll(int page, int size) {
        TypedQuery<User> query = entityManager.createQuery("SELECT u FROM User u ORDER BY u.id DESC", User.class);
        query.setFirstResult(page * size);
        query.setMaxResults(size);
        return query.getResultList();
    }
}
