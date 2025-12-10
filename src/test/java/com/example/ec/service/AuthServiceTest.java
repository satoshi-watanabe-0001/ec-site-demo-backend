package com.example.ec.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import com.example.ec.dto.AuthResponse;
import com.example.ec.dto.LoginRequest;
import com.example.ec.entity.User;
import com.example.ec.exception.AuthenticationException;
import com.example.ec.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * AuthServiceのテストクラス
 *
 * <p>認証サービスの単体テスト。
 */
@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

  @Mock private UserRepository userRepository;

  @Mock private JwtService jwtService;

  @Mock private PasswordEncoder passwordEncoder;

  @InjectMocks private AuthService authService;

  private User testUser;

  @BeforeEach
  void setUp() {
    testUser =
        User.builder()
            .id(1L)
            .email("test@example.com")
            .name("テストユーザー")
            .passwordHash("$2a$10$hashedpassword")
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();
  }

  @Test
  @DisplayName("ユーザー認証: 正常系")
  void authenticateUser_success() {
    LoginRequest loginRequest = new LoginRequest();
    loginRequest.setEmail("test@example.com");
    loginRequest.setPassword("password123");

    when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
    when(passwordEncoder.matches("password123", "$2a$10$hashedpassword")).thenReturn(true);
    when(jwtService.generateAccessToken("test@example.com")).thenReturn("access-token-xxx");
    when(jwtService.generateRefreshToken("test@example.com")).thenReturn("refresh-token-xxx");

    AuthResponse response = authService.authenticateUser(loginRequest);

    assertThat(response.getAccessToken()).isEqualTo("access-token-xxx");
    assertThat(response.getRefreshToken()).isEqualTo("refresh-token-xxx");
    assertThat(response.getTokenType()).isEqualTo("Bearer");
    assertThat(response.getExpiresIn()).isEqualTo(3600L);
    assertThat(response.getUser().getId()).isEqualTo("1");
    assertThat(response.getUser().getName()).isEqualTo("テストユーザー");
    assertThat(response.getUser().getEmail()).isEqualTo("test@example.com");
  }

  @Test
  @DisplayName("ユーザー認証: ユーザーが見つからない場合")
  void authenticateUser_userNotFound() {
    LoginRequest loginRequest = new LoginRequest();
    loginRequest.setEmail("notfound@example.com");
    loginRequest.setPassword("password123");

    when(userRepository.findByEmail("notfound@example.com")).thenReturn(Optional.empty());

    assertThatThrownBy(() -> authService.authenticateUser(loginRequest))
        .isInstanceOf(AuthenticationException.class)
        .hasMessageContaining("認証情報が無効です");
  }

  @Test
  @DisplayName("ユーザー認証: パスワード不一致")
  void authenticateUser_wrongPassword() {
    LoginRequest loginRequest = new LoginRequest();
    loginRequest.setEmail("test@example.com");
    loginRequest.setPassword("wrongpassword");

    when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(testUser));
    when(passwordEncoder.matches("wrongpassword", "$2a$10$hashedpassword")).thenReturn(false);

    assertThatThrownBy(() -> authService.authenticateUser(loginRequest))
        .isInstanceOf(AuthenticationException.class)
        .hasMessageContaining("認証情報が無効です");
  }
}
