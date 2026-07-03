package com.cherohn.taskManager.dto;

import com.cherohn.taskManager.model.TaskPriority;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;

@Getter @Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateTaskRequest {

    @NotBlank(message = "O Titulo nao pode estar em branco")
    private String title;

    private String description;

    @NotNull(message = "A prioridade e obrigatoria")
    private TaskPriority priority;

    private LocalDate dueDate;
}
