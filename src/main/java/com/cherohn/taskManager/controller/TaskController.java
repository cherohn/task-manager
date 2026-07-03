package com.cherohn.taskManager.controller;

import com.cherohn.taskManager.dto.request.CreateTaskRequest;
import com.cherohn.taskManager.dto.request.UpdateTaskRequest;
import com.cherohn.taskManager.dto.response.TaskResponse;
import com.cherohn.taskManager.dto.response.PageResponse;
import com.cherohn.taskManager.model.TaskStatus;
import com.cherohn.taskManager.model.User;
import com.cherohn.taskManager.service.TaskService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/tasks")
@RequiredArgsConstructor
@Tag(name = "Tarefas", description = "Endpoints protegidos de gerenciamento de tarefas")
@SecurityRequirement(name = "bearerAuth")
public class TaskController {

    private final TaskService taskService;

    @PostMapping
    public ResponseEntity<TaskResponse> createTask(@Valid @RequestBody CreateTaskRequest request, @AuthenticationPrincipal User currentUser) {
        TaskResponse response = taskService.createTask(request, currentUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<PageResponse<TaskResponse>> getUserTasks(@AuthenticationPrincipal User currentUser,
                                                                   @RequestParam(required = false) TaskStatus status,
                                                                   @RequestParam(defaultValue = "0") int page,
                                                                   @RequestParam(defaultValue = "10") int size){
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        PageResponse<TaskResponse> response = taskService.getUserTasks(currentUser, status, pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TaskResponse> getTaskById(@PathVariable UUID id, @AuthenticationPrincipal User currentUser) {
        TaskResponse response = taskService.getTaskById(id, currentUser);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TaskResponse> updateTask(@PathVariable UUID id, @Valid @RequestBody UpdateTaskRequest request, @AuthenticationPrincipal User currentUser) {
        TaskResponse response = taskService.updateTask(id, request, currentUser);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable UUID id, @AuthenticationPrincipal User currentUser) {
        taskService.deleteTask(id, currentUser);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PageResponse<TaskResponse>> getAllTasks(@RequestParam(required = false) TaskStatus status,
                                                                  @RequestParam(defaultValue = "0") int page,
                                                                  @RequestParam(defaultValue = "10") int size){
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        PageResponse<TaskResponse> response = taskService.getAllTasks(status, pageable);
        return ResponseEntity.ok(response);
    }
}
