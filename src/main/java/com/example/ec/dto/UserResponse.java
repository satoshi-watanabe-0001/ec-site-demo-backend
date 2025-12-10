package com.example.ec.dto;

import lombok.Builder;
import lombok.Data;

/**
 * ユーザーレスポンスDTO
 *
 * <p>フロントエンドのUser型と互換性を持つレスポンスDTO。 認証成功時にユーザー情報を返却するために使用する。
 */
@Data
@Builder
public class UserResponse {

  /** ユーザーID */
  private String id;

  /** ユーザー名 */
  private String name;

  /** メールアドレス */
  private String email;
}
