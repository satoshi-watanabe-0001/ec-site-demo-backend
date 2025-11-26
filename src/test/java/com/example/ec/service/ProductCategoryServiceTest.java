package com.example.ec.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import com.example.ec.dto.CategoryDetailRequest;
import com.example.ec.dto.CategoryDetailResponse;
import com.example.ec.dto.CategoryListResponse;
import com.example.ec.dto.CategoryRecommendationResponse;
import com.example.ec.entity.Category;
import com.example.ec.entity.Product;
import com.example.ec.entity.ProductVariant;
import com.example.ec.exception.CategoryNotFoundException;
import com.example.ec.repository.CategoryRepository;
import com.example.ec.repository.ProductRepository;
import com.example.ec.repository.ProductVariantRepository;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

/**
 * ProductCategoryServiceのテストクラス
 *
 * <p>商品カテゴリサービスの単体テスト。
 */
@ExtendWith(MockitoExtension.class)
class ProductCategoryServiceTest {

  @Mock private CategoryRepository categoryRepository;

  @Mock private ProductRepository productRepository;

  @Mock private ProductVariantRepository productVariantRepository;

  @InjectMocks private ProductCategoryService productCategoryService;

  private Category testCategory;
  private Product testProduct;
  private ProductVariant testVariant;

  @BeforeEach
  void setUp() {
    testCategory =
        Category.builder()
            .categoryCode("iphone")
            .displayName("iPhone")
            .heroImageUrl("https://example.com/iphone.jpg")
            .leadText("最新のiPhoneをチェック")
            .displayOrder(1)
            .isActive(true)
            .createdAt(Instant.now())
            .updatedAt(Instant.now())
            .build();

    testProduct =
        Product.builder()
            .id(1L)
            .name("iPhone 15 Pro")
            .description("最新のiPhone")
            .price(new BigDecimal("159800"))
            .category(testCategory)
            .isActive(true)
            .productCampaigns(new ArrayList<>())
            .createdAt(Instant.now())
            .updatedAt(Instant.now())
            .build();

    testVariant =
        ProductVariant.builder()
            .id(1L)
            .product(testProduct)
            .manufacturer("Apple")
            .modelName("iPhone 15 Pro")
            .storageCapacity("256GB")
            .colorCode("#000000")
            .colorName("ブラック")
            .imageUrls(List.of("https://example.com/iphone15pro.jpg"))
            .createdAt(Instant.now())
            .updatedAt(Instant.now())
            .build();
  }

  @Test
  @DisplayName("カテゴリ一覧取得: 正常系")
  void getCategories_success() {
    when(categoryRepository.findByIsActiveTrueOrderByDisplayOrder())
        .thenReturn(List.of(testCategory));
    when(productRepository.countByCategoryCodeAndIsActiveTrue("iphone")).thenReturn(10L);

    CategoryListResponse response = productCategoryService.getCategories();

    assertThat(response.getSuccess()).isTrue();
    assertThat(response.getData()).hasSize(1);
    assertThat(response.getData().get(0).getCategoryCode()).isEqualTo("iphone");
    assertThat(response.getData().get(0).getProductCount()).isEqualTo(10L);
  }

  @Test
  @DisplayName("カテゴリ一覧取得: カテゴリが空の場合")
  void getCategories_empty() {
    when(categoryRepository.findByIsActiveTrueOrderByDisplayOrder()).thenReturn(new ArrayList<>());

    CategoryListResponse response = productCategoryService.getCategories();

    assertThat(response.getSuccess()).isTrue();
    assertThat(response.getData()).isEmpty();
  }

  @Test
  @DisplayName("カテゴリ詳細取得: 正常系")
  void getCategoryDetail_success() {
    CategoryDetailRequest request =
        CategoryDetailRequest.builder().page(0).size(20).sort("name").order("asc").build();

    Page<Product> productPage = new PageImpl<>(List.of(testProduct));

    when(categoryRepository.findActiveCategoryByCode("iphone"))
        .thenReturn(Optional.of(testCategory));
    when(productRepository.findByCategoryCodeAndIsActiveTrue(eq("iphone"), any(Pageable.class)))
        .thenReturn(productPage);
    when(productVariantRepository.findByProductIds(anyList())).thenReturn(List.of(testVariant));

    CategoryDetailResponse response = productCategoryService.getCategoryDetail("iphone", request);

    assertThat(response.getSuccess()).isTrue();
    assertThat(response.getData().getCategory().getCategoryCode()).isEqualTo("iphone");
    assertThat(response.getData().getProducts()).hasSize(1);
    assertThat(response.getData().getProducts().get(0).getProductName()).isEqualTo("iPhone 15 Pro");
  }

  @Test
  @DisplayName("カテゴリ詳細取得: キーワード検索")
  void getCategoryDetail_withKeyword() {
    CategoryDetailRequest request =
        CategoryDetailRequest.builder()
            .keyword("Pro")
            .page(0)
            .size(20)
            .sort("name")
            .order("asc")
            .build();

    Page<Product> productPage = new PageImpl<>(List.of(testProduct));

    when(categoryRepository.findActiveCategoryByCode("iphone"))
        .thenReturn(Optional.of(testCategory));
    when(productRepository.findByCategoryCodeAndKeyword(
            eq("iphone"), eq("Pro"), any(Pageable.class)))
        .thenReturn(productPage);
    when(productVariantRepository.findByProductIds(anyList())).thenReturn(List.of(testVariant));

    CategoryDetailResponse response = productCategoryService.getCategoryDetail("iphone", request);

    assertThat(response.getSuccess()).isTrue();
    assertThat(response.getData().getProducts()).hasSize(1);
  }

  @Test
  @DisplayName("カテゴリ詳細取得: カテゴリが見つからない場合")
  void getCategoryDetail_notFound() {
    CategoryDetailRequest request = CategoryDetailRequest.builder().build();

    when(categoryRepository.findActiveCategoryByCode("invalid")).thenReturn(Optional.empty());

    assertThatThrownBy(() -> productCategoryService.getCategoryDetail("invalid", request))
        .isInstanceOf(CategoryNotFoundException.class)
        .hasMessageContaining("invalid");
  }

  @Test
  @DisplayName("おすすめ商品取得: 正常系")
  void getRecommendations_success() {
    when(categoryRepository.findActiveCategoryByCode("iphone"))
        .thenReturn(Optional.of(testCategory));

    CategoryRecommendationResponse response = productCategoryService.getRecommendations("iphone");

    assertThat(response.getSuccess()).isTrue();
    assertThat(response.getData().getRecommendations()).isEmpty();
  }

  @Test
  @DisplayName("おすすめ商品取得: カテゴリが見つからない場合")
  void getRecommendations_notFound() {
    when(categoryRepository.findActiveCategoryByCode("invalid")).thenReturn(Optional.empty());

    assertThatThrownBy(() -> productCategoryService.getRecommendations("invalid"))
        .isInstanceOf(CategoryNotFoundException.class)
        .hasMessageContaining("invalid");
  }
}
