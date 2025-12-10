package com.example.ec.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.ec.dto.LoginRequest;
import com.example.ec.entity.User;
import com.example.ec.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * 認証API統合テストクラス
 *
 * <p>ログインAPIの疎通テストを自動実行するための統合テスト。 JWT_SECRET環境変数はGradleタスクで設定されます。
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("integrationtest")
@Transactional
class AuthApiIntegrationTest {

  @Autowired private MockMvc mockMvc;

  @Autowired private ObjectMapper objectMapper;

  @Autowired private UserRepository userRepository;

  @Autowired private PasswordEncoder passwordEncoder;

  private static final String LOGIN_ENDPOINT = "/api/v1/auth/login";
  private static final String TEST_EMAIL = "test@example.com";
  private static final String TEST_PASSWORD = "password123";
  private static final String TEST_USER_NAME = "テストユーザー";

  @BeforeEach
  void setUp() {
    userRepository.deleteAll();
    User testUser =
        User.builder()
            .email(TEST_EMAIL)
            .name(TEST_USER_NAME)
            .passwordHash(passwordEncoder.encode(TEST_PASSWORD))
            .build();
    userRepository.save(testUser);
  }

  @Test
  @DisplayName("疎通テスト1: 正常系（有効な認証情報）")
  void login_withValidCredentials_returnsTokenAndUserInfo() throws Exception {
    LoginRequest loginRequest = new LoginRequest();
    loginRequest.setEmail(TEST_EMAIL);
    loginRequest.setPassword(TEST_PASSWORD);

    mockMvc
        .perform(
            post(LOGIN_ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.access_token").exists())
        .andExpect(jsonPath("$.refresh_token").exists())
        .andExpect(jsonPath("$.token_type").value("Bearer"))
        .andExpect(jsonPath("$.expires_in").value(3600))
        .andExpect(jsonPath("$.user.name").value(TEST_USER_NAME))
        .andExpect(jsonPath("$.user.email").value(TEST_EMAIL));
  }

  @Test
  @DisplayName("疎通テスト2: 認証失敗（パスワード不一致）")
  void login_withWrongPassword_returnsAuthenticationError() throws Exception {
    LoginRequest loginRequest = new LoginRequest();
    loginRequest.setEmail(TEST_EMAIL);
    loginRequest.setPassword("wrongpassword");

    mockMvc
        .perform(
            post(LOGIN_ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.success").value(false))
        .andExpect(jsonPath("$.error_code").value("AUTHENTICATION_FAILED"))
        .andExpect(jsonPath("$.message").value("認証情報が無効です"));
  }

  @Test
  @DisplayName("疎通テスト3: 認証失敗（ユーザー未発見）")
  void login_withNonExistentUser_returnsAuthenticationError() throws Exception {
    LoginRequest loginRequest = new LoginRequest();
    loginRequest.setEmail("notfound@example.com");
    loginRequest.setPassword(TEST_PASSWORD);

    mockMvc
        .perform(
            post(LOGIN_ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.success").value(false))
        .andExpect(jsonPath("$.error_code").value("AUTHENTICATION_FAILED"))
        .andExpect(jsonPath("$.message").value("認証情報が無効です"));
  }

  @Test
  @DisplayName("疎通テスト4: バリデーションエラー（メールアドレス未入力）")
  void login_withEmptyEmail_returnsValidationError() throws Exception {
    LoginRequest loginRequest = new LoginRequest();
    loginRequest.setEmail("");
    loginRequest.setPassword(TEST_PASSWORD);

    mockMvc
        .perform(
            post(LOGIN_ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.success").value(false))
        .andExpect(jsonPath("$.error_code").value("VALIDATION_ERROR"))
        .andExpect(jsonPath("$.field_errors.email").exists());
  }

  @Test
  @DisplayName("疎通テスト5: バリデーションエラー（パスワード未入力）")
  void login_withEmptyPassword_returnsValidationError() throws Exception {
    LoginRequest loginRequest = new LoginRequest();
    loginRequest.setEmail(TEST_EMAIL);
    loginRequest.setPassword("");

    mockMvc
        .perform(
            post(LOGIN_ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.success").value(false))
        .andExpect(jsonPath("$.error_code").value("VALIDATION_ERROR"))
        .andExpect(jsonPath("$.field_errors.password").exists());
  }

  @Test
  @DisplayName("疎通テスト6: バリデーションエラー（無効なメールアドレス形式）")
  void login_withInvalidEmailFormat_returnsValidationError() throws Exception {
    LoginRequest loginRequest = new LoginRequest();
    loginRequest.setEmail("invalid-email");
    loginRequest.setPassword(TEST_PASSWORD);

    mockMvc
        .perform(
            post(LOGIN_ENDPOINT)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.success").value(false))
        .andExpect(jsonPath("$.error_code").value("VALIDATION_ERROR"))
        .andExpect(jsonPath("$.field_errors.email").exists());
  }
}
