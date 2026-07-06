package com.cherohn.taskManager.service;

import com.cherohn.taskManager.dto.request.LoginRequest;
import com.cherohn.taskManager.dto.request.RegisterRequest;
import com.cherohn.taskManager.dto.response.AuthResponse;
import com.cherohn.taskManager.exception.ConflictException;
import com.cherohn.taskManager.model.User;
import com.cherohn.taskManager.model.Role;
import com.cherohn.taskManager.repositoy.UserRepository;
import com.cherohn.taskManager.security.JwtService;
import com.cherohn.taskManager.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthService authService;

    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;
    private User user;

    @BeforeEach
    void setUp() {
        registerRequest = RegisterRequest.builder()
                .name("Matheus")
                .email("matheus.garcez09@gmail.com")
                .password("senhaforte123")
                .build();

        loginRequest = LoginRequest.builder()
                .email("matheus.garcez09@gmail.com")
                .password("senhaforte123")
                .build();

        user = User.builder()
                .id(UUID.randomUUID())
                .name("Matheus")
                .email("matheus.garcez09@gmail.com")
                .password("senhaforte123")
                .role(Role.USER)
                .build();
    }

    @Nested
    @DisplayName("Testes do metodo register")
    class RegisterTest {

        @Test
        @DisplayName("Deve retorntar AuthResposnde com token quando email nao existe")
        void deveRetornarAuthResposndeComTokenQuandoEmailExiste() {
            // Arrange
            when(userRepository.existsByEmail(registerRequest.getEmail())).thenReturn(false);
            when(passwordEncoder.encode(registerRequest.getPassword())).thenReturn("hash_da_senha");
            when(userRepository.save(any(User.class))).thenReturn(user);
            when(jwtService.generateToken(any(User.class))).thenReturn("token_jwt_gerado");

            // Act
            AuthResponse response = authService.register(registerRequest);

            // Assert
            assertNotNull(response);
            assertNotNull(response.getToken());
            assertEquals("matheus.garcez09@gmail.com", response.getEmail());
            verify(userRepository, times(1)).save(any(User.class));
        }

        @Test
        @DisplayName("Deve lancar ConflictException quando email ja esta cadastrado")
        void deveLancarConflictExceptionQuandoEmailJaCadastrado() {
            // Arrange
            when(userRepository.existsByEmail(registerRequest.getEmail())).thenReturn(true);

            // Act + Assert
            assertThrows(ConflictException.class, () -> authService.register(registerRequest));
            verify(userRepository, never()). save(any(User.class));
        }
    }

    @Nested
    @DisplayName("Testes do metodo login")
    class LoginTest {

        @Test
        @DisplayName("Deve retornar AuthResponse com token quando credenciais sao validas")
        void deveRetornarAuthResponseComTokenQuandoCredenciaisSaoValidas() {

            // Arrange
            Authentication authenticationFalso = mock(Authentication.class);
            when(authenticationFalso.getPrincipal()).thenReturn(user);

            when(authenticationManager.authenticate(any(Authentication.class))).thenReturn(authenticationFalso);
            when(jwtService.generateToken(user)).thenReturn("token_jwt_gerado");

            // Act
            AuthResponse response = authService.login(loginRequest);

            // Assert
            assertNotNull(response);
            assertNotNull(response.getToken());
            assertEquals("matheus.garcez09@gmail.com", response.getEmail());
            verify(userRepository, never()).findByEmail(anyString());
        }

        @Test
        @DisplayName("Deve lancar excecao quando credenciais sao invalidas")
        void deveLancarExcecaoQuandoCredenciaisSaoInvalidas() {

            // Arrange
            when(authenticationManager.authenticate(any())).thenThrow(new BadCredentialsException("Credenciais invalidas"));

            // Act + Assert
            assertThrows(BadCredentialsException.class, () -> authService.login(loginRequest));
            verify(jwtService, never()).generateToken(any());
        }
    }
}
