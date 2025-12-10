package com.example.ec.service;

import com.example.ec.dto.AuthResponse;
import com.example.ec.dto.LoginRequest;
import com.example.ec.dto.UserResponse;
import com.example.ec.entity.User;
import com.example.ec.exception.AuthenticationException;
import com.example.ec.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 認証サービス
 *
 * <p>ユーザー認証のビジネスロジックを提供するサービスクラス。 メールアドレスとパスワードによる認証をサポートする。
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

  private final UserRepository userRepository;
  private final JwtService jwtService;
  private final PasswordEncoder passwordEncoder;

  /**
   * ユーザー認証を行う
   *
   * <p>メールアドレスとパスワードでユーザーを認証し、JWTトークンを発行する。
   *
   * @param loginRequest ログインリクエスト（メールアドレスとパスワード）
   * @return 認証レスポンス（アクセストークン、リフレッシュトークン、ユーザー情報）
   * @throws AuthenticationException 認証失敗時
   */
  @Transactional(readOnly = true)
  public AuthResponse authenticateUser(LoginRequest loginRequest) {
    String email = loginRequest.getEmail();
    log.info("ログイン試行: email={}", email);

    User user =
        userRepository
            .findByEmail(email)
            .orElseThrow(() -> new AuthenticationException("認証情報が無効です"));

    if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPasswordHash())) {
      log.warn("パスワード不一致: email={}", email);
      throw new AuthenticationException("認証情報が無効です");
    }

    boolean rememberMe = loginRequest.isRememberMe();
    String accessToken = jwtService.generateAccessToken(email, rememberMe);
    String refreshToken = jwtService.generateRefreshToken(email);
    long expiresIn = jwtService.getAccessTokenExpirationSeconds(rememberMe);

    log.info("ログイン成功: email={}, rememberMe={}", email, rememberMe);

    UserResponse userResponse =
        UserResponse.builder()
            .id(String.valueOf(user.getId()))
            .name(user.getName())
            .email(user.getEmail())
            .build();

    return AuthResponse.builder()
        .accessToken(accessToken)
        .refreshToken(refreshToken)
        .tokenType("Bearer")
        .expiresIn(expiresIn)
        .user(userResponse)
        .build();
  }
}
