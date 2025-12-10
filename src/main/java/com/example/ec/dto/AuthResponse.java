package com.example.ec.dto;

import lombok.Builder;
import lombok.Data;

/**
 * 認証レスポンスDTO
 *
 * <p>認証成功時のレスポンスデータを保持するDTO。 アクセストークン、リフレッシュトークン、ユーザー情報を含む。
 */
@Data
@Builder
public class AuthResponse {

  /** アクセストークン（JWT） */
  private String accessToken;

  /** リフレッシュトークン */
  private String refreshToken;

  /** トークンタイプ（Bearer） */
  private String tokenType;

  /** トークン有効期限（秒） */
  private Long expiresIn;

  /** ユーザー情報 */
  private UserResponse user;
}
