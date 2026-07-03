package com.cherohn.taskManager.repositoy;

import com.cherohn.taskManager.model.Task;
import com.cherohn.taskManager.model.User;
import com.cherohn.taskManager.model.TaskStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TaskRepository extends JpaRepository<Task, UUID> {

    Page<Task> findByUser(User user, Pageable pageable);
    Page<Task> findByUserAndStatus(User user, TaskStatus taskStatus, Pageable pageable);
    Page<Task> findByStatus(TaskStatus status, Pageable pageable);
}
