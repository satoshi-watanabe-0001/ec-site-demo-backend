package com.example.ec.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.ec.dto.AuthResponse;
import com.example.ec.dto.LoginRequest;
import com.example.ec.dto.UserResponse;
import com.example.ec.exception.AuthenticationException;
import com.example.ec.exception.GlobalExceptionHandler;
import com.example.ec.service.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

/**
 * AuthControllerのテストクラス
 *
 * <p>認証コントローラーの単体テスト。
 */
@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

  private MockMvc mockMvc;

  private ObjectMapper objectMapper;

  @Mock private AuthService authService;

  @InjectMocks private AuthController authController;

  @BeforeEach
  void setUp() {
    mockMvc =
        MockMvcBuilders.standaloneSetup(authController)
            .setControllerAdvice(new GlobalExceptionHandler())
            .build();
    objectMapper = new ObjectMapper();
  }

  @Test
  @DisplayName("ログイン: 正常系")
  void login_success() throws Exception {
    LoginRequest loginRequest = new LoginRequest();
    loginRequest.setEmail("test@example.com");
    loginRequest.setPassword("password123");

    UserResponse userResponse =
        UserResponse.builder().id("1").name("テストユーザー").email("test@example.com").build();

    AuthResponse authResponse =
        AuthResponse.builder()
            .accessToken("access-token-xxx")
            .refreshToken("refresh-token-xxx")
            .tokenType("Bearer")
            .expiresIn(3600L)
            .user(userResponse)
            .build();

    when(authService.authenticateUser(any(LoginRequest.class))).thenReturn(authResponse);

    mockMvc
        .perform(
            post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.accessToken").value("access-token-xxx"))
        .andExpect(jsonPath("$.refreshToken").value("refresh-token-xxx"))
        .andExpect(jsonPath("$.tokenType").value("Bearer"))
        .andExpect(jsonPath("$.expiresIn").value(3600))
        .andExpect(jsonPath("$.user.id").value("1"))
        .andExpect(jsonPath("$.user.name").value("テストユーザー"))
        .andExpect(jsonPath("$.user.email").value("test@example.com"));
  }

  @Test
  @DisplayName("ログイン: 認証失敗")
  void login_authenticationFailed() throws Exception {
    LoginRequest loginRequest = new LoginRequest();
    loginRequest.setEmail("test@example.com");
    loginRequest.setPassword("wrongpassword");

    when(authService.authenticateUser(any(LoginRequest.class)))
        .thenThrow(new AuthenticationException("認証情報が無効です"));

    mockMvc
        .perform(
            post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.success").value(false))
        .andExpect(jsonPath("$.errorCode").value("AUTHENTICATION_FAILED"));
  }

  @Test
  @DisplayName("ログイン: メールアドレス未入力")
  void login_emailBlank() throws Exception {
    LoginRequest loginRequest = new LoginRequest();
    loginRequest.setEmail("");
    loginRequest.setPassword("password123");

    mockMvc
        .perform(
            post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.success").value(false))
        .andExpect(jsonPath("$.errorCode").value("VALIDATION_ERROR"));
  }

  @Test
  @DisplayName("ログイン: パスワード未入力")
  void login_passwordBlank() throws Exception {
    LoginRequest loginRequest = new LoginRequest();
    loginRequest.setEmail("test@example.com");
    loginRequest.setPassword("");

    mockMvc
        .perform(
            post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.success").value(false))
        .andExpect(jsonPath("$.errorCode").value("VALIDATION_ERROR"));
  }

  @Test
  @DisplayName("ログイン: 無効なメールアドレス形式")
  void login_invalidEmailFormat() throws Exception {
    LoginRequest loginRequest = new LoginRequest();
    loginRequest.setEmail("invalid-email");
    loginRequest.setPassword("password123");

    mockMvc
        .perform(
            post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.success").value(false))
        .andExpect(jsonPath("$.errorCode").value("VALIDATION_ERROR"));
  }
}
