package com.example.ec.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * ログインリクエストDTO
 *
 * <p>ユーザー認証のためのリクエストデータを保持するDTO。 メールアドレスとパスワードのバリデーションを含む。
 */
@Data
public class LoginRequest {

  /** メールアドレス（必須、有効なメール形式） */
  @NotBlank(message = "メールアドレスは必須です")
  @Email(message = "有効なメールアドレス形式で入力してください")
  private String email;

  /** パスワード（必須、6〜100文字） */
  @NotBlank(message = "パスワードは必須です")
  @Size(min = 6, max = 100, message = "パスワードは6文字以上100文字以下で入力してください")
  private String password;

  /** ログイン状態を保持するフラグ（trueの場合、トークン有効期限を延長） */
  private boolean rememberMe;
}
