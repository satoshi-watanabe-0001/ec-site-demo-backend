package com.example.ec.exception;

/**
 * 認証例外
 *
 * <p>ユーザー認証に失敗した場合にスローされる例外クラス。
 */
public class AuthenticationException extends RuntimeException {

  /**
   * メッセージを指定して例外を生成する
   *
   * @param message エラーメッセージ
   */
  public AuthenticationException(String message) {
    super(message);
  }

  /**
   * メッセージと原因を指定して例外を生成する
   *
   * @param message エラーメッセージ
   * @param cause 原因となった例外
   */
  public AuthenticationException(String message, Throwable cause) {
    super(message, cause);
  }
}
