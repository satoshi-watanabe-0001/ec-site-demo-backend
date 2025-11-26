package com.example.ec.exception;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

/**
 * GlobalExceptionHandlerのテストクラス
 *
 * <p>グローバル例外ハンドラーの単体テスト。
 */
class GlobalExceptionHandlerTest {

  private GlobalExceptionHandler handler;

  @BeforeEach
  void setUp() {
    handler = new GlobalExceptionHandler();
  }

  @Test
  @DisplayName("CategoryNotFoundException: 404レスポンスを返す")
  void handleCategoryNotFoundException() {
    CategoryNotFoundException ex = new CategoryNotFoundException("invalid");

    ResponseEntity<ErrorResponse> response = handler.handleCategoryNotFoundException(ex);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getSuccess()).isFalse();
    assertThat(response.getBody().getErrorCode()).isEqualTo("CATEGORY_NOT_FOUND");
    assertThat(response.getBody().getMessage()).contains("invalid");
  }

  @Test
  @DisplayName("BindException: 400レスポンスを返す")
  void handleBindException() {
    BindingResult bindingResult = Mockito.mock(BindingResult.class);
    FieldError fieldError = new FieldError("request", "page", "ページ番号は0以上である必要があります");
    Mockito.when(bindingResult.getFieldErrors()).thenReturn(java.util.List.of(fieldError));

    BindException ex = new BindException(bindingResult);

    ResponseEntity<ErrorResponse> response = handler.handleBindException(ex);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getSuccess()).isFalse();
    assertThat(response.getBody().getErrorCode()).isEqualTo("VALIDATION_ERROR");
    assertThat(response.getBody().getFieldErrors()).containsKey("page");
  }

  @Test
  @DisplayName("ConstraintViolationException: 400レスポンスを返す")
  void handleConstraintViolationException() {
    Set<ConstraintViolation<?>> violations = new HashSet<>();
    ConstraintViolation<?> violation = Mockito.mock(ConstraintViolation.class);
    Path path = Mockito.mock(Path.class);
    Mockito.when(path.toString()).thenReturn("size");
    Mockito.when(violation.getPropertyPath()).thenReturn(path);
    Mockito.when(violation.getMessage()).thenReturn("サイズは1以上である必要があります");
    violations.add(violation);

    ConstraintViolationException ex = new ConstraintViolationException(violations);

    ResponseEntity<ErrorResponse> response = handler.handleConstraintViolationException(ex);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getSuccess()).isFalse();
    assertThat(response.getBody().getErrorCode()).isEqualTo("VALIDATION_ERROR");
  }

  @Test
  @DisplayName("MethodArgumentTypeMismatchException: 400レスポンスを返す")
  void handleTypeMismatchException() {
    MethodArgumentTypeMismatchException ex =
        new MethodArgumentTypeMismatchException(
            "invalid", Integer.class, "page", null, new NumberFormatException());

    ResponseEntity<ErrorResponse> response = handler.handleTypeMismatchException(ex);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getSuccess()).isFalse();
    assertThat(response.getBody().getErrorCode()).isEqualTo("TYPE_MISMATCH");
    assertThat(response.getBody().getMessage()).contains("page");
  }

  @Test
  @DisplayName("Exception: 500レスポンスを返す")
  void handleGenericException() {
    Exception ex = new RuntimeException("予期しないエラー");

    ResponseEntity<ErrorResponse> response = handler.handleGenericException(ex);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getSuccess()).isFalse();
    assertThat(response.getBody().getErrorCode()).isEqualTo("INTERNAL_SERVER_ERROR");
  }
}
