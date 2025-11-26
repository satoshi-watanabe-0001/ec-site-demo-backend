package com.example.ec.controller;

import com.example.ec.dto.CategoryDetailRequest;
import com.example.ec.dto.CategoryDetailResponse;
import com.example.ec.dto.CategoryListResponse;
import com.example.ec.dto.CategoryRecommendationResponse;
import com.example.ec.service.ProductCategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 商品カテゴリコントローラー
 *
 * <p>商品カテゴリに関するREST APIエンドポイントを提供するコントローラー。 薄いController設計に従い、ビジネスロジックはServiceレイヤーに委譲する。
 */
@RestController
@RequestMapping("/api/v1/products/categories")
@Validated
@RequiredArgsConstructor
@Slf4j
public class ProductCategoryController {

  private final ProductCategoryService productCategoryService;

  /**
   * カテゴリ一覧を取得する
   *
   * @return カテゴリ一覧レスポンス
   */
  @GetMapping
  public ResponseEntity<CategoryListResponse> getCategories() {
    log.info("カテゴリ一覧取得リクエストを受信しました");
    CategoryListResponse response = productCategoryService.getCategories();
    return ResponseEntity.ok(response);
  }

  /**
   * カテゴリ詳細を取得する
   *
   * @param categoryCode カテゴリコード
   * @param request リクエストパラメータ
   * @return カテゴリ詳細レスポンス
   */
  @GetMapping("/{categoryCode}")
  public ResponseEntity<CategoryDetailResponse> getCategoryDetail(
      @PathVariable String categoryCode, @ModelAttribute @Valid CategoryDetailRequest request) {
    log.info("カテゴリ詳細取得リクエストを受信しました: categoryCode={}, request={}", categoryCode, request);
    CategoryDetailResponse response =
        productCategoryService.getCategoryDetail(categoryCode, request);
    return ResponseEntity.ok(response);
  }

  /**
   * おすすめ商品を取得する
   *
   * @param categoryCode カテゴリコード
   * @return おすすめ商品レスポンス
   */
  @GetMapping("/{categoryCode}/recommendations")
  public ResponseEntity<CategoryRecommendationResponse> getRecommendations(
      @PathVariable String categoryCode) {
    log.info("おすすめ商品取得リクエストを受信しました: categoryCode={}", categoryCode);
    CategoryRecommendationResponse response =
        productCategoryService.getRecommendations(categoryCode);
    return ResponseEntity.ok(response);
  }
}
