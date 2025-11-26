package com.example.ec.exception;

import java.time.Instant;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * エラーレスポンスDTO
 *
 * <p>APIエラー時の標準レスポンスフォーマット。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {

  private Boolean success;
  private String errorCode;
  private String message;
  private Map<String, String> fieldErrors;
  private Instant timestamp;
  private String requestId;
}
