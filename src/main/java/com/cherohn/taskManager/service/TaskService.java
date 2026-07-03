package com.cherohn.taskManager.service;

import com.cherohn.taskManager.dto.request.CreateTaskRequest;
import com.cherohn.taskManager.dto.request.UpdateTaskRequest;
import com.cherohn.taskManager.dto.response.TaskResponse;
import com.cherohn.taskManager.dto.response.PageResponse;
import com.cherohn.taskManager.exception.ForbiddenException;
import com.cherohn.taskManager.exception.ResourceNotFoundException;
import com.cherohn.taskManager.model.Task;
import com.cherohn.taskManager.model.TaskStatus;
import com.cherohn.taskManager.model.User;
import com.cherohn.taskManager.repositoy.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;

    public TaskResponse createTask(CreateTaskRequest request, User currentUser) {
        Task task = Task.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .priority(request.getPriority())
                .dueDate(request.getDueDate())
                .status(TaskStatus.TODO)
                .user(currentUser)
                .build();

        Task savedTask = taskRepository.save(task);
        return toResponse(savedTask);
    }

    public PageResponse<TaskResponse> getUserTasks(User currentUser, TaskStatus status, Pageable pageable) {
        Page<Task> tasks = (status != null) ? taskRepository.findByUserAndStatus(currentUser, status, pageable) : taskRepository.findByUser(currentUser, pageable);
        Page<TaskResponse> responsePage = tasks.map(this::toResponse);
        return PageResponse.of(responsePage);
    }

    public TaskResponse getTaskById(UUID id, User currentUser) {
        Task task = findTaskAndValidateOwnership(id, currentUser);
        return toResponse(task);
    }

    public TaskResponse updateTask(UUID id, UpdateTaskRequest request, User currentUser) {

        Task task = findTaskAndValidateOwnership(id, currentUser);
        if(request.getTitle() != null) {
            task.setTitle(request.getTitle());
        }
        if(request.getDescription() != null) {
            task.setDescription(request.getDescription());
        }
        if(request.getStatus() != null) {
            task.setStatus(request.getStatus());
        }
        if(request.getPriority() != null) {
            task.setPriority(request.getPriority());
        }
        if(request.getDueDate() != null) {
            task.setDueDate(request.getDueDate());
        }

        Task updatedTask = taskRepository.save(task);
        return toResponse(updatedTask);
    }

    public void deleteTask(UUID id, User currentUser) {
        Task task = findTaskAndValidateOwnership(id, currentUser);
        taskRepository.delete(task);
    }

    public PageResponse<TaskResponse> getAllTasks(TaskStatus status, Pageable pageable) {
        Page<Task> tasks = (status != null) ? taskRepository.findByStatus(status, pageable) : taskRepository.findAll(pageable);
        Page<TaskResponse> responsePage = tasks.map(this::toResponse);
        return PageResponse.of(responsePage);
    }

    private Task findTaskAndValidateOwnership(UUID id, User currentUser) {
        Task task = taskRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Tarefa Nao encontrada com id: " + id));

        if(!task.getUser().getId().equals(currentUser.getId())){
            throw new ForbiddenException("Voce nao tem permissao para acessar esta tarefa");
        }

        return task;
    }

    private TaskResponse toResponse(Task task) {
        return TaskResponse.builder()
                .id(task.getId())
                .title(task.getTitle())
                .description(task.getDescription())
                .status(task.getStatus())
                .priority(task.getPriority())
                .dueDate(task.getDueDate())
                .createdAt(task.getCreatedAt())
                .updatedAt(task.getUpdatedAt())
                .build();
    }
}
