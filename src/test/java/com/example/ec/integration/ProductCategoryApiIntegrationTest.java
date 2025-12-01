package com.example.ec.integration;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.ec.dto.CategoryDetailResponse;
import com.example.ec.dto.CategoryListResponse;
import com.example.ec.dto.CategoryRecommendationResponse;
import com.example.ec.entity.Category;
import com.example.ec.entity.Product;
import com.example.ec.entity.ProductVariant;
import com.example.ec.exception.ErrorResponse;
import com.example.ec.repository.CategoryRepository;
import com.example.ec.repository.ProductRepository;
import com.example.ec.repository.ProductVariantRepository;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

/**
 * Product Categories APIの結合テスト
 *
 * <p>API仕様書（EC-270）に基づいたHTTPレベルでの確認テスト。 実際のHTTPリクエストを送信し、レスポンスの構造・ステータスコード・フィールドを検証する。
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class ProductCategoryApiIntegrationTest {

  @LocalServerPort private int port;

  @Autowired private TestRestTemplate restTemplate;

  @Autowired private CategoryRepository categoryRepository;

  @Autowired private ProductRepository productRepository;

  @Autowired private ProductVariantRepository productVariantRepository;

  private String baseUrl;

  @BeforeEach
  void setUp() {
    baseUrl = "http://localhost:" + port + "/api/v1/products/categories";

    productVariantRepository.deleteAll();
    productRepository.deleteAll();
    categoryRepository.deleteAll();

    Category iphoneCategory =
        Category.builder()
            .categoryCode("iphone")
            .displayName("iPhone")
            .heroImageUrl("https://example.com/iphone-hero.jpg")
            .leadText("最新のiPhoneをチェック")
            .displayOrder(1)
            .isActive(true)
            .createdAt(Instant.now())
            .updatedAt(Instant.now())
            .build();

    Category androidCategory =
        Category.builder()
            .categoryCode("android")
            .displayName("Android")
            .heroImageUrl("https://example.com/android-hero.jpg")
            .leadText("人気のAndroidスマートフォン")
            .displayOrder(2)
            .isActive(true)
            .createdAt(Instant.now())
            .updatedAt(Instant.now())
            .build();

    Category inactiveCategory =
        Category.builder()
            .categoryCode("inactive")
            .displayName("非アクティブカテゴリ")
            .displayOrder(99)
            .isActive(false)
            .createdAt(Instant.now())
            .updatedAt(Instant.now())
            .build();

    categoryRepository.saveAll(List.of(iphoneCategory, androidCategory, inactiveCategory));

    Product iphone15Pro =
        Product.builder()
            .name("iPhone 15 Pro")
            .description("最新のiPhone 15 Proモデル")
            .price(new BigDecimal("159800"))
            .category(iphoneCategory)
            .isActive(true)
            .productCampaigns(new ArrayList<>())
            .createdAt(Instant.now())
            .updatedAt(Instant.now())
            .build();

    Product iphone15 =
        Product.builder()
            .name("iPhone 15")
            .description("iPhone 15スタンダードモデル")
            .price(new BigDecimal("124800"))
            .category(iphoneCategory)
            .isActive(true)
            .productCampaigns(new ArrayList<>())
            .createdAt(Instant.now())
            .updatedAt(Instant.now())
            .build();

    Product galaxyS24 =
        Product.builder()
            .name("Galaxy S24")
            .description("Samsung Galaxy S24")
            .price(new BigDecimal("139800"))
            .category(androidCategory)
            .isActive(true)
            .productCampaigns(new ArrayList<>())
            .createdAt(Instant.now())
            .updatedAt(Instant.now())
            .build();

    productRepository.saveAll(List.of(iphone15Pro, iphone15, galaxyS24));

    ProductVariant variant1 =
        ProductVariant.builder()
            .product(iphone15Pro)
            .manufacturer("Apple")
            .modelName("iPhone 15 Pro")
            .storageCapacity("256GB")
            .colorCode("#000000")
            .colorName("ブラックチタニウム")
            .imageUrls(List.of("https://example.com/iphone15pro-black.jpg"))
            .createdAt(Instant.now())
            .updatedAt(Instant.now())
            .build();

    ProductVariant variant2 =
        ProductVariant.builder()
            .product(iphone15Pro)
            .manufacturer("Apple")
            .modelName("iPhone 15 Pro")
            .storageCapacity("512GB")
            .colorCode("#FFFFFF")
            .colorName("ホワイトチタニウム")
            .imageUrls(List.of("https://example.com/iphone15pro-white.jpg"))
            .createdAt(Instant.now())
            .updatedAt(Instant.now())
            .build();

    productVariantRepository.saveAll(List.of(variant1, variant2));
  }

  @Nested
  @DisplayName("GET /api/v1/products/categories - カテゴリ一覧API")
  class GetCategoriesTest {

    @Test
    @DisplayName("正常系: アクティブなカテゴリ一覧を取得できる")
    void shouldReturnActiveCategoriesList() {
      ResponseEntity<CategoryListResponse> response =
          restTemplate.getForEntity(baseUrl, CategoryListResponse.class);

      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
      assertThat(response.getBody()).isNotNull();
      assertThat(response.getBody().getSuccess()).isTrue();
      assertThat(response.getBody().getData()).hasSize(2);
      assertThat(response.getBody().getTimestamp()).isNotNull();
      assertThat(response.getBody().getRequestId()).isNotNull();
    }

    @Test
    @DisplayName("正常系: カテゴリ一覧のレスポンス構造が仕様通りである")
    void shouldReturnCorrectResponseStructure() {
      ResponseEntity<CategoryListResponse> response =
          restTemplate.getForEntity(baseUrl, CategoryListResponse.class);

      assertThat(response.getBody()).isNotNull();
      CategoryListResponse.CategorySummary firstCategory = response.getBody().getData().get(0);

      assertThat(firstCategory.getCategoryCode()).isEqualTo("iphone");
      assertThat(firstCategory.getDisplayName()).isEqualTo("iPhone");
      assertThat(firstCategory.getHeroImageUrl()).isNotNull();
      assertThat(firstCategory.getLeadText()).isNotNull();
      assertThat(firstCategory.getProductCount()).isEqualTo(2L);
    }

    @Test
    @DisplayName("正常系: カテゴリはdisplayOrder順にソートされている")
    void shouldReturnCategoriesSortedByDisplayOrder() {
      ResponseEntity<CategoryListResponse> response =
          restTemplate.getForEntity(baseUrl, CategoryListResponse.class);

      assertThat(response.getBody()).isNotNull();
      List<CategoryListResponse.CategorySummary> categories = response.getBody().getData();

      assertThat(categories.get(0).getCategoryCode()).isEqualTo("iphone");
      assertThat(categories.get(1).getCategoryCode()).isEqualTo("android");
    }

    @Test
    @DisplayName("正常系: 非アクティブなカテゴリは含まれない")
    void shouldNotIncludeInactiveCategories() {
      ResponseEntity<CategoryListResponse> response =
          restTemplate.getForEntity(baseUrl, CategoryListResponse.class);

      assertThat(response.getBody()).isNotNull();
      List<String> categoryCodes =
          response.getBody().getData().stream()
              .map(CategoryListResponse.CategorySummary::getCategoryCode)
              .toList();

      assertThat(categoryCodes).doesNotContain("inactive");
    }
  }

  @Nested
  @DisplayName("GET /api/v1/products/categories/{categoryCode} - カテゴリ詳細API")
  class GetCategoryDetailTest {

    @Test
    @DisplayName("正常系: カテゴリ詳細を取得できる")
    void shouldReturnCategoryDetail() {
      ResponseEntity<CategoryDetailResponse> response =
          restTemplate.getForEntity(baseUrl + "/iphone", CategoryDetailResponse.class);

      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
      assertThat(response.getBody()).isNotNull();
      assertThat(response.getBody().getSuccess()).isTrue();
      assertThat(response.getBody().getData()).isNotNull();
      assertThat(response.getBody().getData().getCategory()).isNotNull();
      assertThat(response.getBody().getData().getProducts()).isNotNull();
    }

    @Test
    @DisplayName("正常系: カテゴリ情報が仕様通りの構造である")
    void shouldReturnCorrectCategoryInfo() {
      ResponseEntity<CategoryDetailResponse> response =
          restTemplate.getForEntity(baseUrl + "/iphone", CategoryDetailResponse.class);

      assertThat(response.getBody()).isNotNull();
      CategoryDetailResponse.CategoryInfo category = response.getBody().getData().getCategory();

      assertThat(category.getCategoryCode()).isEqualTo("iphone");
      assertThat(category.getDisplayName()).isEqualTo("iPhone");
      assertThat(category.getHeroImageUrl()).isNotNull();
      assertThat(category.getLeadText()).isNotNull();
    }

    @Test
    @DisplayName("正常系: 商品リストが仕様通りの構造である")
    void shouldReturnCorrectProductStructure() {
      ResponseEntity<CategoryDetailResponse> response =
          restTemplate.getForEntity(baseUrl + "/iphone", CategoryDetailResponse.class);

      assertThat(response.getBody()).isNotNull();
      List<CategoryDetailResponse.ProductItem> products =
          response.getBody().getData().getProducts();

      assertThat(products).isNotEmpty();
      CategoryDetailResponse.ProductItem product = products.get(0);

      assertThat(product.getProductId()).isNotNull();
      assertThat(product.getProductName()).isNotNull();
      assertThat(product.getDescription()).isNotNull();
      assertThat(product.getPrice()).isNotNull();
    }

    @Test
    @DisplayName("正常系: ページネーション情報が含まれる")
    void shouldIncludePaginationInfo() {
      ResponseEntity<CategoryDetailResponse> response =
          restTemplate.getForEntity(baseUrl + "/iphone", CategoryDetailResponse.class);

      assertThat(response.getBody()).isNotNull();
      assertThat(response.getBody().getData().getMeta()).isNotNull();
      assertThat(response.getBody().getData().getMeta().getPagination()).isNotNull();

      CategoryDetailResponse.Pagination pagination =
          response.getBody().getData().getMeta().getPagination();
      assertThat(pagination.getPage()).isNotNull();
      assertThat(pagination.getPerPage()).isNotNull();
      assertThat(pagination.getTotal()).isNotNull();
      assertThat(pagination.getPages()).isNotNull();
    }

    @Test
    @DisplayName("正常系: ページネーションパラメータが機能する")
    void shouldSupportPaginationParameters() {
      ResponseEntity<CategoryDetailResponse> response =
          restTemplate.getForEntity(
              baseUrl + "/iphone?page=0&size=1", CategoryDetailResponse.class);

      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
      assertThat(response.getBody()).isNotNull();
      assertThat(response.getBody().getData().getProducts()).hasSize(1);
      assertThat(response.getBody().getData().getMeta().getPagination().getPerPage()).isEqualTo(1);
    }

    @Test
    @DisplayName("正常系: キーワード検索が機能する")
    void shouldSupportKeywordSearch() {
      ResponseEntity<CategoryDetailResponse> response =
          restTemplate.getForEntity(baseUrl + "/iphone?keyword=Pro", CategoryDetailResponse.class);

      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
      assertThat(response.getBody()).isNotNull();
      assertThat(response.getBody().getData().getProducts()).hasSize(1);
      assertThat(response.getBody().getData().getProducts().get(0).getProductName())
          .contains("Pro");
    }

    @Test
    @DisplayName("異常系: 存在しないカテゴリコードで404を返す")
    void shouldReturn404ForNonExistentCategory() {
      ResponseEntity<ErrorResponse> response =
          restTemplate.getForEntity(baseUrl + "/nonexistent", ErrorResponse.class);

      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
      assertThat(response.getBody()).isNotNull();
      assertThat(response.getBody().getSuccess()).isFalse();
      assertThat(response.getBody().getErrorCode()).isEqualTo("CATEGORY_NOT_FOUND");
    }

    @Test
    @DisplayName("異常系: 非アクティブなカテゴリコードで404を返す")
    void shouldReturn404ForInactiveCategory() {
      ResponseEntity<ErrorResponse> response =
          restTemplate.getForEntity(baseUrl + "/inactive", ErrorResponse.class);

      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
      assertThat(response.getBody()).isNotNull();
      assertThat(response.getBody().getErrorCode()).isEqualTo("CATEGORY_NOT_FOUND");
    }
  }

  @Nested
  @DisplayName("GET /api/v1/products/categories/{categoryCode}/recommendations - おすすめ商品API")
  class GetRecommendationsTest {

    @Test
    @DisplayName("正常系: おすすめ商品を取得できる")
    void shouldReturnRecommendations() {
      ResponseEntity<CategoryRecommendationResponse> response =
          restTemplate.getForEntity(
              baseUrl + "/iphone/recommendations", CategoryRecommendationResponse.class);

      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
      assertThat(response.getBody()).isNotNull();
      assertThat(response.getBody().getSuccess()).isTrue();
      assertThat(response.getBody().getData()).isNotNull();
      assertThat(response.getBody().getData().getRecommendations()).isNotNull();
    }

    @Test
    @DisplayName("正常系: レスポンス構造が仕様通りである")
    void shouldReturnCorrectResponseStructure() {
      ResponseEntity<CategoryRecommendationResponse> response =
          restTemplate.getForEntity(
              baseUrl + "/iphone/recommendations", CategoryRecommendationResponse.class);

      assertThat(response.getBody()).isNotNull();
      assertThat(response.getBody().getTimestamp()).isNotNull();
      assertThat(response.getBody().getRequestId()).isNotNull();
    }

    @Test
    @DisplayName("異常系: 存在しないカテゴリコードで404を返す")
    void shouldReturn404ForNonExistentCategory() {
      ResponseEntity<ErrorResponse> response =
          restTemplate.getForEntity(baseUrl + "/nonexistent/recommendations", ErrorResponse.class);

      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
      assertThat(response.getBody()).isNotNull();
      assertThat(response.getBody().getSuccess()).isFalse();
      assertThat(response.getBody().getErrorCode()).isEqualTo("CATEGORY_NOT_FOUND");
    }
  }

  @Nested
  @DisplayName("共通仕様の確認")
  class CommonSpecificationTest {

    @Test
    @DisplayName("正常系: 全てのレスポンスにtimestampが含まれる")
    void shouldIncludeTimestampInAllResponses() {
      ResponseEntity<CategoryListResponse> listResponse =
          restTemplate.getForEntity(baseUrl, CategoryListResponse.class);
      assertThat(listResponse.getBody().getTimestamp()).isNotNull();

      ResponseEntity<CategoryDetailResponse> detailResponse =
          restTemplate.getForEntity(baseUrl + "/iphone", CategoryDetailResponse.class);
      assertThat(detailResponse.getBody().getTimestamp()).isNotNull();

      ResponseEntity<CategoryRecommendationResponse> recResponse =
          restTemplate.getForEntity(
              baseUrl + "/iphone/recommendations", CategoryRecommendationResponse.class);
      assertThat(recResponse.getBody().getTimestamp()).isNotNull();
    }

    @Test
    @DisplayName("正常系: 全てのレスポンスにrequestIdが含まれる")
    void shouldIncludeRequestIdInAllResponses() {
      ResponseEntity<CategoryListResponse> listResponse =
          restTemplate.getForEntity(baseUrl, CategoryListResponse.class);
      assertThat(listResponse.getBody().getRequestId()).isNotNull();

      ResponseEntity<CategoryDetailResponse> detailResponse =
          restTemplate.getForEntity(baseUrl + "/iphone", CategoryDetailResponse.class);
      assertThat(detailResponse.getBody().getRequestId()).isNotNull();

      ResponseEntity<CategoryRecommendationResponse> recResponse =
          restTemplate.getForEntity(
              baseUrl + "/iphone/recommendations", CategoryRecommendationResponse.class);
      assertThat(recResponse.getBody().getRequestId()).isNotNull();
    }

    @Test
    @DisplayName("異常系: エラーレスポンスの構造が仕様通りである")
    void shouldReturnCorrectErrorResponseStructure() {
      ResponseEntity<ErrorResponse> response =
          restTemplate.getForEntity(baseUrl + "/nonexistent", ErrorResponse.class);

      assertThat(response.getBody()).isNotNull();
      assertThat(response.getBody().getSuccess()).isFalse();
      assertThat(response.getBody().getErrorCode()).isNotNull();
      assertThat(response.getBody().getMessage()).isNotNull();
      assertThat(response.getBody().getTimestamp()).isNotNull();
    }
  }
}
