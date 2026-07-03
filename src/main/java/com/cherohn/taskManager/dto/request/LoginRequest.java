package com.cherohn.taskManager.dto;

import lombok.*;
import jakarta.validation.constraints.NotBlank;

@Getter @Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {

    @NotBlank(message = "O email nao pode estar em branco")
    private String email;

    @NotBlank(message = "A senha nao pode estar em branco")
    private String password;
}
