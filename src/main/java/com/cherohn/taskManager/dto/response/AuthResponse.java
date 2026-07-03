package com.cherohn.taskManager.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;


@Getter
@Builder@AllArgsConstructor
public class AuthResponse {
    private String token;

    @Builder.Default
    private String type = "Bearer";

    private String email;
    private String name;
    private String role;
}
