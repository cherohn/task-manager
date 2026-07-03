package com.cherohn.taskManager.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;


@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {

    @NotBlank(message = "O Nome nao pode estar em branco")
    private String name;

    @NotBlank(message = "O email nao pode estar em branco")
    @Email(message = "Email Invalido")
    private String email;

    @NotBlank(message = "A senha nao pode estar em branco")
    @Size(min = 12, message = "A senha deve ter no minimo 12 caracteres")
    private String password;
}
