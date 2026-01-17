package com.vsa.ecommerce.feature.system;

import com.vsa.ecommerce.domain.entity.PersistentTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PersistentTaskRepository extends JpaRepository<PersistentTask, Long> {

    @Query("SELECT t FROM PersistentTask t WHERE t.category = :category AND t.status = 'PENDING' AND (t.scheduledAt IS NULL OR t.scheduledAt <= :now) ORDER BY t.priority DESC, t.createdAt ASC")
    List<PersistentTask> findPendingTasks(String category, LocalDateTime now);

    List<PersistentTask> findByStatusAndRetryCountLessThan(String status, Integer maxRetries);
}
