package com.example.ec.service;

import com.example.ec.dto.CategoryDetailRequest;
import com.example.ec.dto.CategoryDetailResponse;
import com.example.ec.dto.CategoryListResponse;
import com.example.ec.dto.CategoryRecommendationResponse;
import com.example.ec.entity.Campaign;
import com.example.ec.entity.Category;
import com.example.ec.entity.Product;
import com.example.ec.entity.ProductCampaign;
import com.example.ec.entity.ProductVariant;
import com.example.ec.exception.CategoryNotFoundException;
import com.example.ec.repository.CategoryRepository;
import com.example.ec.repository.ProductRepository;
import com.example.ec.repository.ProductVariantRepository;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 商品カテゴリサービス
 *
 * <p>商品カテゴリに関するビジネスロジックを提供するサービスクラス。
 */
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class ProductCategoryService {

  private final CategoryRepository categoryRepository;
  private final ProductRepository productRepository;
  private final ProductVariantRepository productVariantRepository;

  /**
   * カテゴリ一覧を取得する
   *
   * @return カテゴリ一覧レスポンス
   */
  public CategoryListResponse getCategories() {
    log.info("カテゴリ一覧を取得します");

    List<Category> categories = categoryRepository.findByIsActiveTrueOrderByDisplayOrder();

    List<CategoryListResponse.CategorySummary> summaries =
        categories.stream()
            .map(
                category -> {
                  Long productCount =
                      productRepository.countByCategoryCodeAndIsActiveTrue(
                          category.getCategoryCode());
                  return CategoryListResponse.CategorySummary.builder()
                      .categoryCode(category.getCategoryCode())
                      .displayName(category.getDisplayName())
                      .heroImageUrl(category.getHeroImageUrl())
                      .leadText(category.getLeadText())
                      .productCount(productCount)
                      .build();
                })
            .collect(Collectors.toList());

    return CategoryListResponse.builder()
        .success(true)
        .message("カテゴリ一覧を取得しました")
        .data(summaries)
        .timestamp(Instant.now())
        .requestId(UUID.randomUUID().toString())
        .build();
  }

  /**
   * カテゴリ詳細を取得する
   *
   * @param categoryCode カテゴリコード
   * @param request リクエストパラメータ
   * @return カテゴリ詳細レスポンス
   * @throws CategoryNotFoundException カテゴリが見つからない場合
   */
  public CategoryDetailResponse getCategoryDetail(
      String categoryCode, CategoryDetailRequest request) {
    log.info("カテゴリ詳細を取得します: categoryCode={}", categoryCode);

    Category category =
        categoryRepository
            .findActiveCategoryByCode(categoryCode)
            .orElseThrow(() -> new CategoryNotFoundException(categoryCode));

    Pageable pageable = createPageable(request);

    Page<Product> productPage;
    if (request.getKeyword() != null && !request.getKeyword().isEmpty()) {
      productPage =
          productRepository.findByCategoryCodeAndKeyword(
              categoryCode, request.getKeyword(), pageable);
    } else {
      productPage = productRepository.findByCategoryCodeAndIsActiveTrue(categoryCode, pageable);
    }

    List<Long> productIds =
        productPage.getContent().stream().map(Product::getId).collect(Collectors.toList());

    List<ProductVariant> variants = productVariantRepository.findByProductIds(productIds);

    List<CategoryDetailResponse.ProductItem> productItems =
        productPage.getContent().stream()
            .map(product -> convertToProductItem(product, variants))
            .collect(Collectors.toList());

    CategoryDetailResponse.CategoryInfo categoryInfo =
        CategoryDetailResponse.CategoryInfo.builder()
            .categoryCode(category.getCategoryCode())
            .displayName(category.getDisplayName())
            .heroImageUrl(category.getHeroImageUrl())
            .leadText(category.getLeadText())
            .build();

    CategoryDetailResponse.Pagination pagination =
        CategoryDetailResponse.Pagination.builder()
            .page(request.getPage())
            .perPage(request.getSize())
            .total(productPage.getTotalElements())
            .pages(productPage.getTotalPages())
            .build();

    CategoryDetailResponse.Meta meta =
        CategoryDetailResponse.Meta.builder().pagination(pagination).build();

    CategoryDetailResponse.DataWrapper data =
        CategoryDetailResponse.DataWrapper.builder()
            .category(categoryInfo)
            .products(productItems)
            .meta(meta)
            .build();

    return CategoryDetailResponse.builder()
        .success(true)
        .message("カテゴリ詳細を取得しました")
        .data(data)
        .timestamp(Instant.now())
        .requestId(UUID.randomUUID().toString())
        .build();
  }

  /**
   * おすすめ商品を取得する
   *
   * <p>TODO: ec-site-recommendation-serviceが実装されたら、そちらと連携する
   *
   * @param categoryCode カテゴリコード
   * @return おすすめ商品レスポンス
   * @throws CategoryNotFoundException カテゴリが見つからない場合
   */
  public CategoryRecommendationResponse getRecommendations(String categoryCode) {
    log.info("おすすめ商品を取得します: categoryCode={}", categoryCode);

    categoryRepository
        .findActiveCategoryByCode(categoryCode)
        .orElseThrow(() -> new CategoryNotFoundException(categoryCode));

    // TODO: ec-site-recommendation-serviceが実装されたら、そちらから取得する
    // 現時点では空のリストを返す
    CategoryRecommendationResponse.DataWrapper data =
        CategoryRecommendationResponse.DataWrapper.builder()
            .recommendations(new ArrayList<>())
            .build();

    return CategoryRecommendationResponse.builder()
        .success(true)
        .message("おすすめ商品を取得しました（現在おすすめ商品はありません）")
        .data(data)
        .timestamp(Instant.now())
        .requestId(UUID.randomUUID().toString())
        .build();
  }

  /**
   * ページング情報を作成する
   *
   * @param request リクエストパラメータ
   * @return ページング情報
   */
  private Pageable createPageable(CategoryDetailRequest request) {
    Sort.Direction direction =
        "desc".equalsIgnoreCase(request.getOrder()) ? Sort.Direction.DESC : Sort.Direction.ASC;
    String sortField = mapSortField(request.getSort());
    return PageRequest.of(request.getPage(), request.getSize(), Sort.by(direction, sortField));
  }

  /**
   * ソートフィールドをマッピングする
   *
   * @param sort クライアントから指定されたソートフィールド
   * @return エンティティのプロパティパス
   */
  private String mapSortField(String sort) {
    return switch (sort) {
      case "price" -> "price";
      case "createdAt" -> "createdAt";
      default -> "name";
    };
  }

  /**
   * 商品エンティティをDTOに変換する
   *
   * @param product 商品エンティティ
   * @param allVariants 全バリアントリスト
   * @return 商品アイテムDTO
   */
  private CategoryDetailResponse.ProductItem convertToProductItem(
      Product product, List<ProductVariant> allVariants) {
    List<ProductVariant> productVariants =
        allVariants.stream()
            .filter(v -> v.getProduct().getId().equals(product.getId()))
            .collect(Collectors.toList());

    ProductVariant firstVariant = productVariants.isEmpty() ? null : productVariants.get(0);

    List<CategoryDetailResponse.CampaignBadge> campaignBadges =
        product.getProductCampaigns().stream()
            .map(ProductCampaign::getCampaign)
            .filter(Campaign::isCurrentlyValid)
            .map(
                campaign ->
                    CategoryDetailResponse.CampaignBadge.builder()
                        .campaignCode(campaign.getCampaignCode())
                        .badgeText(campaign.getBadgeText())
                        .build())
            .collect(Collectors.toList());

    return CategoryDetailResponse.ProductItem.builder()
        .productId(product.getId())
        .productName(product.getName())
        .description(product.getDescription())
        .price(product.getPrice())
        .manufacturer(firstVariant != null ? firstVariant.getManufacturer() : null)
        .modelName(firstVariant != null ? firstVariant.getModelName() : null)
        .storageCapacity(firstVariant != null ? firstVariant.getStorageCapacity() : null)
        .colorCode(firstVariant != null ? firstVariant.getColorCode() : null)
        .colorName(firstVariant != null ? firstVariant.getColorName() : null)
        .imageUrls(firstVariant != null ? firstVariant.getImageUrls() : new ArrayList<>())
        .campaigns(campaignBadges)
        .build();
  }
}
