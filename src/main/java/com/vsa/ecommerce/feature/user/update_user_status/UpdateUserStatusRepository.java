package com.vsa.ecommerce.feature.user.update_user_status;

import com.vsa.ecommerce.domain.entity.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository("UpdateUserStatusRepository")
public class UpdateUserStatusRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public Optional<User> findById(Long id) {
        return Optional.ofNullable(entityManager.find(User.class, id));
    }

    public void save(User user) {
        if (user.getId() == null) {
            entityManager.persist(user);
        } else {
            entityManager.merge(user);
        }
    }
}
