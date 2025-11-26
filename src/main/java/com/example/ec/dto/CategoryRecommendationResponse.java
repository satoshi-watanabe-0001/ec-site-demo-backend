package com.example.ec.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * カテゴリおすすめ商品レスポンスDTO
 *
 * <p>おすすめ商品APIのレスポンスを表すDTO。 組織標準のAPIレスポンスフォーマットに準拠。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryRecommendationResponse {

  private Boolean success;
  private String message;
  private DataWrapper data;
  private Instant timestamp;
  private String requestId;

  /**
   * データラッパークラス
   *
   * <p>おすすめ商品リストを含む。
   */
  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class DataWrapper {
    private List<RecommendedProduct> recommendations;
  }

  /**
   * おすすめ商品DTO
   *
   * <p>おすすめ商品の情報を表すDTO。
   */
  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class RecommendedProduct {
    private Long productId;
    private String productName;
    private String description;
    private BigDecimal price;
    private String manufacturer;
    private String modelName;
    private List<String> imageUrls;
    private String recommendationReason;
  }
}
