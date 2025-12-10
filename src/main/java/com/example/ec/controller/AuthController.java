package com.example.ec.controller;

import com.example.ec.dto.AuthResponse;
import com.example.ec.dto.LoginRequest;
import com.example.ec.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 認証コントローラー
 *
 * <p>ユーザー認証に関するRESTエンドポイントを提供するコントローラー。
 */
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

  private final AuthService authService;

  /**
   * ユーザーログイン
   *
   * <p>メールアドレスとパスワードでユーザーを認証し、JWTトークンを返却する。
   *
   * @param loginRequest ログインリクエスト（メールアドレスとパスワード）
   * @return 認証レスポンス（アクセストークン、リフレッシュトークン、ユーザー情報）
   */
  @PostMapping("/login")
  public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
    log.info("ログインリクエスト受信: email={}", loginRequest.getEmail());
    AuthResponse authResponse = authService.authenticateUser(loginRequest);
    return ResponseEntity.ok(authResponse);
  }
}
