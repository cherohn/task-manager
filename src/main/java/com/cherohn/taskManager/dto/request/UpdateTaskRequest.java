package com.cherohn.taskManager.dto.request;

import com.cherohn.taskManager.model.TaskPriority;
import com.cherohn.taskManager.model.TaskStatus;
import lombok.*;

import java.time.LocalDate;

@Getter @Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateTaskRequest {

    private String title;
    private String description;
    private TaskStatus status;
    private TaskPriority priority;
    private LocalDate dueDate;
}
