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
            .heroImageUrl("https://example.com/iphone.jpg")
            .leadText("最新のiPhoneシリーズ")
            .displayOrder(1)
            .isActive(true)
            .build();

    Category androidCategory =
        Category.builder()
            .categoryCode("android")
            .displayName("Android")
            .heroImageUrl("https://example.com/android.jpg")
            .leadText("Androidスマートフォン")
            .displayOrder(2)
            .isActive(true)
            .build();

    Category inactiveCategory =
        Category.builder()
            .categoryCode("inactive")
            .displayName("非アクティブ")
            .heroImageUrl("https://example.com/inactive.jpg")
            .leadText("非アクティブカテゴリ")
            .displayOrder(3)
            .isActive(false)
            .build();

    categoryRepository.saveAll(List.of(iphoneCategory, androidCategory, inactiveCategory));

    Product product1 =
        Product.builder()
            .name("iPhone 15 Pro")
            .description("最新のiPhone")
            .price(new BigDecimal("159800"))
            .category(iphoneCategory)
            .isActive(true)
            .build();

    Product product2 =
        Product.builder()
            .name("iPhone 15")
            .description("スタンダードモデル")
            .price(new BigDecimal("124800"))
            .category(iphoneCategory)
            .isActive(true)
            .build();

    productRepository.saveAll(List.of(product1, product2));

    ProductVariant variant1 =
        ProductVariant.builder()
            .product(product1)
            .manufacturer("Apple")
            .modelName("iPhone 15 Pro 256GB")
            .storageCapacity("256GB")
            .colorCode("#000000")
            .colorName("ブラック")
            .imageUrls(new ArrayList<>(List.of("https://example.com/iphone15pro-black.jpg")))
            .build();

    ProductVariant variant2 =
        ProductVariant.builder()
            .product(product1)
            .manufacturer("Apple")
            .modelName("iPhone 15 Pro 512GB")
            .storageCapacity("512GB")
            .colorCode("#FFFFFF")
            .colorName("ホワイト")
            .imageUrls(new ArrayList<>(List.of("https://example.com/iphone15pro-white.jpg")))
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
    }

    @Test
    @DisplayName("正常系: カテゴリ詳細のレスポンス構造が仕様通りである")
    void shouldReturnCorrectDetailResponseStructure() {
      ResponseEntity<CategoryDetailResponse> response =
          restTemplate.getForEntity(baseUrl + "/iphone", CategoryDetailResponse.class);

      assertThat(response.getBody()).isNotNull();
      CategoryDetailResponse.DataWrapper data = response.getBody().getData();

      assertThat(data.getCategory()).isNotNull();
      assertThat(data.getCategory().getCategoryCode()).isEqualTo("iphone");
      assertThat(data.getCategory().getDisplayName()).isEqualTo("iPhone");
      assertThat(data.getProducts()).isNotNull();
      assertThat(data.getMeta()).isNotNull();
    }

    @Test
    @DisplayName("正常系: 商品リストが含まれる")
    void shouldIncludeProductsList() {
      ResponseEntity<CategoryDetailResponse> response =
          restTemplate.getForEntity(baseUrl + "/iphone", CategoryDetailResponse.class);

      assertThat(response.getBody()).isNotNull();
      List<CategoryDetailResponse.ProductItem> products =
          response.getBody().getData().getProducts();

      assertThat(products).hasSize(2);
      assertThat(products.get(0).getProductName()).isNotNull();
      assertThat(products.get(0).getPrice()).isNotNull();
    }

    @Test
    @DisplayName("正常系: ページネーション情報が含まれる")
    void shouldIncludePaginationInfo() {
      ResponseEntity<CategoryDetailResponse> response =
          restTemplate.getForEntity(baseUrl + "/iphone", CategoryDetailResponse.class);

      assertThat(response.getBody()).isNotNull();
      CategoryDetailResponse.Pagination pagination =
          response.getBody().getData().getMeta().getPagination();

      assertThat(pagination.getPage()).isEqualTo(0);
      assertThat(pagination.getTotal()).isEqualTo(2L);
      assertThat(pagination.getPages()).isGreaterThanOrEqualTo(1);
    }

    @Test
    @DisplayName("正常系: ページネーションパラメータが機能する")
    void shouldSupportPaginationParameters() {
      ResponseEntity<CategoryDetailResponse> response =
          restTemplate.getForEntity(baseUrl + "/iphone?page=0&size=1", CategoryDetailResponse.class);

      assertThat(response.getBody()).isNotNull();
      assertThat(response.getBody().getData().getProducts()).hasSize(1);
      assertThat(response.getBody().getData().getMeta().getPagination().getPerPage()).isEqualTo(1);
    }

    @Test
    @DisplayName("正常系: キーワード検索が機能する")
    void shouldSupportKeywordSearch() {
      ResponseEntity<CategoryDetailResponse> response =
          restTemplate.getForEntity(baseUrl + "/iphone?keyword=Pro", CategoryDetailResponse.class);

      assertThat(response.getBody()).isNotNull();
      List<CategoryDetailResponse.ProductItem> products =
          response.getBody().getData().getProducts();

      assertThat(products).hasSize(1);
      assertThat(products.get(0).getProductName()).contains("Pro");
    }

    @Test
    @DisplayName("異常系: 存在しないカテゴリコードで404を返す")
    void shouldReturn404ForNonExistentCategory() {
      ResponseEntity<ErrorResponse> response =
          restTemplate.getForEntity(baseUrl + "/nonexistent", ErrorResponse.class);

      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
      assertThat(response.getBody()).isNotNull();
      assertThat(response.getBody().getErrorCode()).isEqualTo("CATEGORY_NOT_FOUND");
    }

    @Test
    @DisplayName("異常系: 非アクティブなカテゴリコードで404を返す")
    void shouldReturn404ForInactiveCategory() {
      ResponseEntity<ErrorResponse> response =
          restTemplate.getForEntity(baseUrl + "/inactive", ErrorResponse.class);

      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("正常系: timestamp と requestId が含まれる")
    void shouldIncludeTimestampAndRequestId() {
      ResponseEntity<CategoryDetailResponse> response =
          restTemplate.getForEntity(baseUrl + "/iphone", CategoryDetailResponse.class);

      assertThat(response.getBody()).isNotNull();
      assertThat(response.getBody().getTimestamp()).isNotNull();
      assertThat(response.getBody().getTimestamp()).isBefore(Instant.now().plusSeconds(1));
      assertThat(response.getBody().getRequestId()).isNotNull();
      assertThat(response.getBody().getRequestId()).isNotEmpty();
    }
  }

  @Nested
  @DisplayName("GET /api/v1/products/categories/{categoryCode}/recommendations - おすすめ商品API")
  class GetRecommendationsTest {

    @Test
    @DisplayName("正常系: おすすめ商品を取得できる（現在は空リスト）")
    void shouldReturnRecommendations() {
      ResponseEntity<CategoryRecommendationResponse> response =
          restTemplate.getForEntity(
              baseUrl + "/iphone/recommendations", CategoryRecommendationResponse.class);

      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
      assertThat(response.getBody()).isNotNull();
      assertThat(response.getBody().getSuccess()).isTrue();
      assertThat(response.getBody().getData()).isNotNull();
    }

    @Test
    @DisplayName("正常系: おすすめ商品のレスポンス構造が仕様通りである")
    void shouldReturnCorrectRecommendationResponseStructure() {
      ResponseEntity<CategoryRecommendationResponse> response =
          restTemplate.getForEntity(
              baseUrl + "/iphone/recommendations", CategoryRecommendationResponse.class);

      assertThat(response.getBody()).isNotNull();
      assertThat(response.getBody().getTimestamp()).isNotNull();
      assertThat(response.getBody().getRequestId()).isNotNull();
    }

    @Test
    @DisplayName("異常系: 存在しないカテゴリコードで404を返す")
    void shouldReturn404ForNonExistentCategoryRecommendations() {
      ResponseEntity<ErrorResponse> response =
          restTemplate.getForEntity(baseUrl + "/nonexistent/recommendations", ErrorResponse.class);

      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
      assertThat(response.getBody()).isNotNull();
      assertThat(response.getBody().getErrorCode()).isEqualTo("CATEGORY_NOT_FOUND");
    }
  }

  @Nested
  @DisplayName("共通仕様テスト")
  class CommonSpecificationTest {

    @Test
    @DisplayName("正常系: 全エンドポイントでtimestampフィールドが返される")
    void shouldReturnTimestampInAllEndpoints() {
      ResponseEntity<CategoryListResponse> listResponse =
          restTemplate.getForEntity(baseUrl, CategoryListResponse.class);
      assertThat(listResponse.getBody().getTimestamp()).isNotNull();

      ResponseEntity<CategoryDetailResponse> detailResponse =
          restTemplate.getForEntity(baseUrl + "/iphone", CategoryDetailResponse.class);
      assertThat(detailResponse.getBody().getTimestamp()).isNotNull();

      ResponseEntity<CategoryRecommendationResponse> recommendationResponse =
          restTemplate.getForEntity(
              baseUrl + "/iphone/recommendations", CategoryRecommendationResponse.class);
      assertThat(recommendationResponse.getBody().getTimestamp()).isNotNull();
    }

    @Test
    @DisplayName("正常系: 全エンドポイントでrequestIdフィールドが返される")
    void shouldReturnRequestIdInAllEndpoints() {
      ResponseEntity<CategoryListResponse> listResponse =
          restTemplate.getForEntity(baseUrl, CategoryListResponse.class);
      assertThat(listResponse.getBody().getRequestId()).isNotNull();

      ResponseEntity<CategoryDetailResponse> detailResponse =
          restTemplate.getForEntity(baseUrl + "/iphone", CategoryDetailResponse.class);
      assertThat(detailResponse.getBody().getRequestId()).isNotNull();

      ResponseEntity<CategoryRecommendationResponse> recommendationResponse =
          restTemplate.getForEntity(
              baseUrl + "/iphone/recommendations", CategoryRecommendationResponse.class);
      assertThat(recommendationResponse.getBody().getRequestId()).isNotNull();
    }

    @Test
    @DisplayName("異常系: エラーレスポンスの構造が仕様通りである")
    void shouldReturnCorrectErrorResponseStructure() {
      ResponseEntity<ErrorResponse> response =
          restTemplate.getForEntity(baseUrl + "/nonexistent", ErrorResponse.class);

      assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
      assertThat(response.getBody()).isNotNull();
      assertThat(response.getBody().getErrorCode()).isNotNull();
      assertThat(response.getBody().getMessage()).isNotNull();
      assertThat(response.getBody().getTimestamp()).isNotNull();
    }
  }
}
