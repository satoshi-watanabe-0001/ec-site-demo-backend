package com.example.ec.exception;

import jakarta.validation.ConstraintViolationException;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

/**
 * グローバル例外ハンドラー
 *
 * <p>アプリケーション全体の例外を一元的に処理するクラス。
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

  /**
   * 認証例外を処理する
   *
   * @param ex 例外
   * @return エラーレスポンス
   */
  @ExceptionHandler(AuthenticationException.class)
  public ResponseEntity<ErrorResponse> handleAuthenticationException(AuthenticationException ex) {
    log.warn("認証エラー: {}", ex.getMessage());

    ErrorResponse response =
        ErrorResponse.builder()
            .success(false)
            .errorCode("AUTHENTICATION_FAILED")
            .message(ex.getMessage())
            .timestamp(Instant.now())
            .requestId(UUID.randomUUID().toString())
            .build();

    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
  }

  /**
   * カテゴリが見つからない例外を処理する
   *
   * @param ex 例外
   * @return エラーレスポンス
   */
  @ExceptionHandler(CategoryNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleCategoryNotFoundException(
      CategoryNotFoundException ex) {
    log.warn("カテゴリが見つかりません: {}", ex.getCategoryCode());

    ErrorResponse response =
        ErrorResponse.builder()
            .success(false)
            .errorCode("CATEGORY_NOT_FOUND")
            .message(ex.getMessage())
            .timestamp(Instant.now())
            .requestId(UUID.randomUUID().toString())
            .build();

    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
  }

  /**
   * バリデーション例外を処理する
   *
   * @param ex 例外
   * @return エラーレスポンス
   */
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponse> handleValidationException(
      MethodArgumentNotValidException ex) {
    log.warn("バリデーションエラー: {}", ex.getMessage());

    Map<String, String> fieldErrors = new HashMap<>();
    ex.getBindingResult()
        .getFieldErrors()
        .forEach(error -> fieldErrors.put(error.getField(), error.getDefaultMessage()));

    ErrorResponse response =
        ErrorResponse.builder()
            .success(false)
            .errorCode("VALIDATION_ERROR")
            .message("入力値が不正です")
            .fieldErrors(fieldErrors)
            .timestamp(Instant.now())
            .requestId(UUID.randomUUID().toString())
            .build();

    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
  }

  /**
   * バインド例外を処理する
   *
   * @param ex 例外
   * @return エラーレスポンス
   */
  @ExceptionHandler(BindException.class)
  public ResponseEntity<ErrorResponse> handleBindException(BindException ex) {
    log.warn("バインドエラー: {}", ex.getMessage());

    Map<String, String> fieldErrors = new HashMap<>();
    ex.getBindingResult()
        .getFieldErrors()
        .forEach(error -> fieldErrors.put(error.getField(), error.getDefaultMessage()));

    ErrorResponse response =
        ErrorResponse.builder()
            .success(false)
            .errorCode("VALIDATION_ERROR")
            .message("入力値が不正です")
            .fieldErrors(fieldErrors)
            .timestamp(Instant.now())
            .requestId(UUID.randomUUID().toString())
            .build();

    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
  }

  /**
   * 制約違反例外を処理する
   *
   * @param ex 例外
   * @return エラーレスポンス
   */
  @ExceptionHandler(ConstraintViolationException.class)
  public ResponseEntity<ErrorResponse> handleConstraintViolationException(
      ConstraintViolationException ex) {
    log.warn("制約違反エラー: {}", ex.getMessage());

    Map<String, String> fieldErrors = new HashMap<>();
    ex.getConstraintViolations()
        .forEach(
            violation -> {
              String field = violation.getPropertyPath().toString();
              fieldErrors.put(field, violation.getMessage());
            });

    ErrorResponse response =
        ErrorResponse.builder()
            .success(false)
            .errorCode("VALIDATION_ERROR")
            .message("入力値が不正です")
            .fieldErrors(fieldErrors)
            .timestamp(Instant.now())
            .requestId(UUID.randomUUID().toString())
            .build();

    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
  }

  /**
   * 型不一致例外を処理する
   *
   * @param ex 例外
   * @return エラーレスポンス
   */
  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  public ResponseEntity<ErrorResponse> handleTypeMismatchException(
      MethodArgumentTypeMismatchException ex) {
    log.warn("型不一致エラー: {}", ex.getMessage());

    String message = String.format("\"%s\" の値 \"%s\" は不正です", ex.getName(), ex.getValue());

    ErrorResponse response =
        ErrorResponse.builder()
            .success(false)
            .errorCode("TYPE_MISMATCH")
            .message(message)
            .timestamp(Instant.now())
            .requestId(UUID.randomUUID().toString())
            .build();

    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
  }

  /**
   * その他の例外を処理する
   *
   * @param ex 例外
   * @return エラーレスポンス
   */
  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
    log.error("予期しないエラーが発生しました", ex);

    ErrorResponse response =
        ErrorResponse.builder()
            .success(false)
            .errorCode("INTERNAL_SERVER_ERROR")
            .message("サーバー内部エラーが発生しました")
            .timestamp(Instant.now())
            .requestId(UUID.randomUUID().toString())
            .build();

    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
  }
}
