package com.example.ec.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * カテゴリ詳細レスポンスDTO
 *
 * <p>カテゴリ詳細・フィルタリングAPIのレスポンスを表すDTO。 組織標準のAPIレスポンスフォーマットに準拠。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryDetailResponse {

  private Boolean success;
  private String message;
  private DataWrapper data;
  private Instant timestamp;
  private String requestId;

  /**
   * データラッパークラス
   *
   * <p>カテゴリ情報、商品リスト、メタ情報を含む。
   */
  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class DataWrapper {
    private CategoryInfo category;
    private List<ProductItem> products;
    private Meta meta;
  }

  /**
   * カテゴリ情報DTO
   *
   * <p>カテゴリの詳細情報を表すDTO。
   */
  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class CategoryInfo {
    private String categoryCode;
    private String displayName;
    private String heroImageUrl;
    private String leadText;
  }

  /**
   * 商品アイテムDTO
   *
   * <p>個別の商品情報を表すDTO。
   */
  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class ProductItem {
    private Long productId;
    private String productName;
    private String description;
    private BigDecimal price;
    private String manufacturer;
    private String modelName;
    private String storageCapacity;
    private String colorCode;
    private String colorName;
    private List<String> imageUrls;
    private List<CampaignBadge> campaigns;
  }

  /**
   * キャンペーンバッジDTO
   *
   * <p>商品に適用されているキャンペーンのバッジ情報を表すDTO。
   */
  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class CampaignBadge {
    private String campaignCode;
    private String badgeText;
  }

  /**
   * メタ情報クラス
   *
   * <p>ページネーション情報を含む。
   */
  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class Meta {
    private Pagination pagination;
  }

  /** ページネーション情報クラス */
  @Data
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class Pagination {
    private Integer page;
    private Integer perPage;
    private Long total;
    private Integer pages;
  }
}
