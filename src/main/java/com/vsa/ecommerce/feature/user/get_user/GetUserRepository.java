package com.vsa.ecommerce.feature.user.get_user;

import com.vsa.ecommerce.domain.entity.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository("GetUserRepository")
public class GetUserRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public Optional<User> findById(Long id) {
        return Optional.ofNullable(entityManager.find(User.class, id));
    }
}
