package com.example.ec;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * ProductCategoryApplicationのテストクラス
 *
 * <p>アプリケーションコンテキストの読み込みテスト。
 */
@SpringBootTest
@ActiveProfiles("test")
class ProductCategoryApplicationTest {

  @Test
  @DisplayName("アプリケーションコンテキストが正常に読み込まれる")
  void contextLoads() {
    // コンテキストが正常に読み込まれることを確認
  }
}
