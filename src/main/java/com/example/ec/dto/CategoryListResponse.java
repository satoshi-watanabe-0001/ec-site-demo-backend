package com.example.ec.dto;

import java.time.Instant;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * カテゴリ一覧レスポンスDTO
 *
 * <p>カテゴリ一覧APIのレスポンスを表すDTO。 組織標準のAPIレスポンスフォーマットに準拠。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryListResponse {

  private Boolean success;
  private String message;
  private List<CategorySummary> data;
  private Instant timestamp;
  private String requestId;

  /**
   * カテゴリサマリーDTO
   *
   * <p>カテゴリの概要情報を表すDTO。
   */
  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class CategorySummary {
    private String categoryCode;
    private String displayName;
    private String heroImageUrl;
    private String leadText;
    private Long productCount;
  }
}
