package com.example.ec.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.ec.config.JwtConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * JwtServiceのテストクラス
 *
 * <p>JWTサービスの単体テスト。
 */
class JwtServiceTest {

  private JwtService jwtService;

  private JwtConfig jwtConfig;

  @BeforeEach
  void setUp() {
    jwtConfig = new JwtConfig();
    jwtConfig.setSecret("dGVzdC1zZWNyZXQta2V5LWZvci1qd3QtdGVzdGluZy0xMjM0NTY3ODkw");
    jwtConfig.setAccessTokenExpiration(3600000L);
    jwtConfig.setRefreshTokenExpiration(86400000L);
    jwtService = new JwtService(jwtConfig);
  }

  @Test
  @DisplayName("アクセストークン生成: 正常系")
  void generateAccessToken_success() {
    String email = "test@example.com";

    String token = jwtService.generateAccessToken(email);

    assertThat(token).isNotNull();
    assertThat(token).isNotEmpty();
    assertThat(token.split("\\.")).hasSize(3);
  }

  @Test
  @DisplayName("リフレッシュトークン生成: 正常系")
  void generateRefreshToken_success() {
    String email = "test@example.com";

    String token = jwtService.generateRefreshToken(email);

    assertThat(token).isNotNull();
    assertThat(token).isNotEmpty();
    assertThat(token.split("\\.")).hasSize(3);
  }

  @Test
  @DisplayName("トークンからユーザー名取得: 正常系")
  void getUsernameFromToken_success() {
    String email = "test@example.com";
    String token = jwtService.generateAccessToken(email);

    String extractedEmail = jwtService.getUsernameFromToken(token);

    assertThat(extractedEmail).isEqualTo(email);
  }

  @Test
  @DisplayName("トークン検証: 有効なトークン")
  void validateToken_validToken() {
    String email = "test@example.com";
    String token = jwtService.generateAccessToken(email);

    boolean isValid = jwtService.validateToken(token);

    assertThat(isValid).isTrue();
  }

  @Test
  @DisplayName("トークン検証: 不正なトークン")
  void validateToken_invalidToken() {
    String invalidToken = "invalid.token.here";

    boolean isValid = jwtService.validateToken(invalidToken);

    assertThat(isValid).isFalse();
  }

  @Test
  @DisplayName("トークン検証: 空のトークン")
  void validateToken_emptyToken() {
    String emptyToken = "";

    boolean isValid = jwtService.validateToken(emptyToken);

    assertThat(isValid).isFalse();
  }

  @Test
  @DisplayName("アクセストークン生成: rememberMe=true")
  void generateAccessToken_withRememberMe() {
    String email = "test@example.com";

    String token = jwtService.generateAccessToken(email, true);

    assertThat(token).isNotNull();
    assertThat(token).isNotEmpty();
    assertThat(token.split("\\.")).hasSize(3);
  }

  @Test
  @DisplayName("アクセストークン生成: rememberMe=false")
  void generateAccessToken_withoutRememberMe() {
    String email = "test@example.com";

    String token = jwtService.generateAccessToken(email, false);

    assertThat(token).isNotNull();
    assertThat(token).isNotEmpty();
    assertThat(token.split("\\.")).hasSize(3);
  }

  @Test
  @DisplayName("アクセストークン有効期限取得: rememberMe=false")
  void getAccessTokenExpirationSeconds_withoutRememberMe() {
    long expirationSeconds = jwtService.getAccessTokenExpirationSeconds(false);

    assertThat(expirationSeconds).isEqualTo(3600L);
  }

  @Test
  @DisplayName("アクセストークン有効期限取得: rememberMe=true（7倍延長）")
  void getAccessTokenExpirationSeconds_withRememberMe() {
    long expirationSeconds = jwtService.getAccessTokenExpirationSeconds(true);

    assertThat(expirationSeconds).isEqualTo(25200L);
  }
}
