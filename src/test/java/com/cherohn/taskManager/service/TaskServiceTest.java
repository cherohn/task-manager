package com.cherohn.taskManager.service;

import com.cherohn.taskManager.dto.request.CreateTaskRequest;
import com.cherohn.taskManager.dto.request.UpdateTaskRequest;
import com.cherohn.taskManager.dto.response.TaskResponse;
import com.cherohn.taskManager.exception.ForbiddenException;
import com.cherohn.taskManager.exception.ResourceNotFoundException;
import com.cherohn.taskManager.model.Task;
import com.cherohn.taskManager.model.TaskPriority;
import com.cherohn.taskManager.model.TaskStatus;
import com.cherohn.taskManager.model.User;
import com.cherohn.taskManager.repositoy.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Optional;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private TaskService taskService;

    private User usuarioDono;
    private User outroUsuario;
    private Task tarefa;
    private UUID tarefaID;

    @BeforeEach
    void setUp() {
        usuarioDono = User.builder()
                .id(UUID.randomUUID())
                .email("dono@email.com")
                .build();

        outroUsuario = User.builder()
                .id(UUID.randomUUID())
                .email("outro@email.com")
                .build();

        tarefaID = UUID.randomUUID();

        tarefa = Task.builder()
                .id(tarefaID)
                .title("Estudar Spring Security")
                .status(TaskStatus.TODO)
                .priority(TaskPriority.HIGH)
                .user(usuarioDono)
                .build();
    }

    @Nested
    @DisplayName("Testes de criacao de tarefas")
    class CreateTask {

        @Test
        @DisplayName("Deve criar tarefa com status TODO independente do que o cliente enviou")
        void deveCriarTarefaComStatusTODO() {

            // Arrange
            CreateTaskRequest request = CreateTaskRequest.builder()
                    .title("Nova Tarefa")
                    .priority(TaskPriority.HIGH)
                    .build();

            when(taskRepository.save(any(Task.class))).thenAnswer(invocation -> invocation.getArgument(0));

            // Act
            TaskResponse response = taskService.createTask(request, usuarioDono);

            // Assert
            assertNotNull(response);
            assertEquals(TaskStatus.TODO, response.getStatus());
            verify(taskRepository, times(1)).save(any(Task.class));
        }
    }

    @Nested
    @DisplayName("Teste de busca de tarefa")
    class BuscaTests{

        @Test
        @DisplayName("Deve retornar tarefa quando usuario e o dono")
        void deveRetornarTarefaQuandoUsuarioEhDono() {

            // Arrange
            when(taskRepository.findById(tarefaID)).thenReturn(Optional.of(tarefa));

            // Act
            TaskResponse response = taskService.getTaskById(tarefaID, usuarioDono);

            // Assert
            assertNotNull(response);
            assertEquals(tarefaID, response.getId());
        }

        @Test
        @DisplayName("Deve lancar ForbiddenException quando usuario nao e o dono")
        void deveLancarForbiddenException() {

            // Arrange
            when(taskRepository.findById(tarefaID)).thenReturn(Optional.of(tarefa));

            // Act + Assert
            assertThrows(ForbiddenException.class, () -> { taskService.getTaskById(tarefaID, outroUsuario); });
        }

        @Test
        @DisplayName("Deve lancar ResourceNotFoundException quando tarefa nao existe")
        void deveLancarResourceNotFoundException() {

            // Arrange
            when(taskRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

            // Act + Assert
            assertThrows(ResourceNotFoundException.class, () -> { taskService.getTaskById(tarefaID, usuarioDono); });
        }
    }

    @Nested
    @DisplayName("Testes de atualizacao de tarefa")
    class UpdateTask {

        @Test
        @DisplayName("Deve atualizar apenas campos nao nulos")
        void deveAtualizarApenasCamposNulos() {

            // Arrange
            UpdateTaskRequest request = UpdateTaskRequest.builder()
                    .status(TaskStatus.IN_PROGRESS)
                    .build();

            when(taskRepository.findById(tarefaID)).thenReturn(Optional.of(tarefa));
            when(taskRepository.save(any(Task.class))).thenAnswer(invocation -> invocation.getArgument(0));

            // Act
            TaskResponse response = taskService.updateTask(tarefaID, request, usuarioDono);

            // Assert
            assertEquals(TaskStatus.IN_PROGRESS, response.getStatus());
            assertEquals("Estudar Spring Security", response.getTitle());
        }
    }

    @Nested
    @DisplayName("Testes de deletar tarefa")
    class DeleteTask {

        @Test
        @DisplayName("Deve deletar tarefa quando usuario e o dono")
        void deveDeletarTarefaQuandoUsuarioEoDono() {

            // Arrange
            when(taskRepository.findById(tarefaID)).thenReturn(Optional.of(tarefa));

            // Act
            taskService.deleteTask(tarefaID, usuarioDono);

            // Assert
            verify(taskRepository, times(1)).delete(tarefa);
        }

        @Test
        @DisplayName("Deve lancar ResourceNotFoundException ao deletar tarefa inexistente")
        void deveLancarResourceNotFoundException() {

            // Arrange
            when(taskRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

            // Act + Assert
            assertThrows(ResourceNotFoundException.class, () -> { taskService.deleteTask(UUID.randomUUID(), usuarioDono); });

            verify(taskRepository, never()).delete(any(Task.class));
        }
    }
}
