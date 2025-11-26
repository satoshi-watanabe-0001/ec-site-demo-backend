package com.example.ec;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Product Categories Common API アプリケーション
 *
 * <p>EC-270: iPhone/Android/リユース品/アクセサリカテゴリ全体で 統一された商品情報を提供するREST APIのエントリーポイント。
 */
@SpringBootApplication
public class ProductCategoryApplication {

  public static void main(String[] args) {
    SpringApplication.run(ProductCategoryApplication.class, args);
  }
}
