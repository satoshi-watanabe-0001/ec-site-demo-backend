package com.example.ec.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.ec.dto.CategoryDetailRequest;
import com.example.ec.dto.CategoryDetailResponse;
import com.example.ec.dto.CategoryListResponse;
import com.example.ec.dto.CategoryRecommendationResponse;
import com.example.ec.exception.CategoryNotFoundException;
import com.example.ec.exception.GlobalExceptionHandler;
import com.example.ec.service.ProductCategoryService;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

/**
 * ProductCategoryControllerのテストクラス
 *
 * <p>商品カテゴリコントローラーの単体テスト。
 */
@ExtendWith(MockitoExtension.class)
class ProductCategoryControllerTest {

  private MockMvc mockMvc;

  @Mock private ProductCategoryService productCategoryService;

  @InjectMocks private ProductCategoryController productCategoryController;

  @BeforeEach
  void setUp() {
    mockMvc =
        MockMvcBuilders.standaloneSetup(productCategoryController)
            .setControllerAdvice(new GlobalExceptionHandler())
            .build();
  }

  @Test
  @DisplayName("カテゴリ一覧取得: 正常系")
  void getCategories_success() throws Exception {
    List<CategoryListResponse.CategorySummary> summaries = new ArrayList<>();
    summaries.add(
        CategoryListResponse.CategorySummary.builder()
            .categoryCode("iphone")
            .displayName("iPhone")
            .heroImageUrl("https://example.com/iphone.jpg")
            .leadText("最新のiPhoneをチェック")
            .productCount(10L)
            .build());

    CategoryListResponse response =
        CategoryListResponse.builder()
            .success(true)
            .message("カテゴリ一覧を取得しました")
            .data(summaries)
            .timestamp(Instant.now())
            .requestId(UUID.randomUUID().toString())
            .build();

    when(productCategoryService.getCategories()).thenReturn(response);

    mockMvc
        .perform(get("/api/v1/products/categories"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.data[0].categoryCode").value("iphone"))
        .andExpect(jsonPath("$.data[0].displayName").value("iPhone"));
  }

  @Test
  @DisplayName("カテゴリ詳細取得: 正常系")
  void getCategoryDetail_success() throws Exception {
    CategoryDetailResponse.CategoryInfo categoryInfo =
        CategoryDetailResponse.CategoryInfo.builder()
            .categoryCode("iphone")
            .displayName("iPhone")
            .heroImageUrl("https://example.com/iphone.jpg")
            .leadText("最新のiPhoneをチェック")
            .build();

    List<CategoryDetailResponse.ProductItem> products = new ArrayList<>();
    products.add(
        CategoryDetailResponse.ProductItem.builder()
            .productId(1L)
            .productName("iPhone 15 Pro")
            .description("最新のiPhone")
            .price(new BigDecimal("159800"))
            .manufacturer("Apple")
            .modelName("iPhone 15 Pro")
            .storageCapacity("256GB")
            .colorCode("#000000")
            .colorName("ブラック")
            .imageUrls(List.of("https://example.com/iphone15pro.jpg"))
            .campaigns(new ArrayList<>())
            .build());

    CategoryDetailResponse.Pagination pagination =
        CategoryDetailResponse.Pagination.builder().page(0).perPage(20).total(1L).pages(1).build();

    CategoryDetailResponse.Meta meta =
        CategoryDetailResponse.Meta.builder().pagination(pagination).build();

    CategoryDetailResponse.DataWrapper data =
        CategoryDetailResponse.DataWrapper.builder()
            .category(categoryInfo)
            .products(products)
            .meta(meta)
            .build();

    CategoryDetailResponse response =
        CategoryDetailResponse.builder()
            .success(true)
            .message("カテゴリ詳細を取得しました")
            .data(data)
            .timestamp(Instant.now())
            .requestId(UUID.randomUUID().toString())
            .build();

    when(productCategoryService.getCategoryDetail(eq("iphone"), any(CategoryDetailRequest.class)))
        .thenReturn(response);

    mockMvc
        .perform(get("/api/v1/products/categories/iphone"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.data.category.categoryCode").value("iphone"))
        .andExpect(jsonPath("$.data.products[0].productName").value("iPhone 15 Pro"));
  }

  @Test
  @DisplayName("カテゴリ詳細取得: カテゴリが見つからない場合")
  void getCategoryDetail_notFound() throws Exception {
    when(productCategoryService.getCategoryDetail(eq("invalid"), any(CategoryDetailRequest.class)))
        .thenThrow(new CategoryNotFoundException("invalid"));

    mockMvc
        .perform(get("/api/v1/products/categories/invalid"))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.success").value(false))
        .andExpect(jsonPath("$.errorCode").value("CATEGORY_NOT_FOUND"));
  }

  @Test
  @DisplayName("おすすめ商品取得: 正常系")
  void getRecommendations_success() throws Exception {
    CategoryRecommendationResponse.DataWrapper data =
        CategoryRecommendationResponse.DataWrapper.builder()
            .recommendations(new ArrayList<>())
            .build();

    CategoryRecommendationResponse response =
        CategoryRecommendationResponse.builder()
            .success(true)
            .message("おすすめ商品を取得しました")
            .data(data)
            .timestamp(Instant.now())
            .requestId(UUID.randomUUID().toString())
            .build();

    when(productCategoryService.getRecommendations("iphone")).thenReturn(response);

    mockMvc
        .perform(get("/api/v1/products/categories/iphone/recommendations"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.data.recommendations").isArray());
  }

  @Test
  @DisplayName("おすすめ商品取得: カテゴリが見つからない場合")
  void getRecommendations_notFound() throws Exception {
    when(productCategoryService.getRecommendations("invalid"))
        .thenThrow(new CategoryNotFoundException("invalid"));

    mockMvc
        .perform(get("/api/v1/products/categories/invalid/recommendations"))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.success").value(false))
        .andExpect(jsonPath("$.errorCode").value("CATEGORY_NOT_FOUND"));
  }
}
