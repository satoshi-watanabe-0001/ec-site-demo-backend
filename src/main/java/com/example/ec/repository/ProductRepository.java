package com.example.ec.repository;

import com.example.ec.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * 商品リポジトリ
 *
 * <p>商品エンティティのデータアクセスを提供するリポジトリインターフェース。
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

  /**
   * カテゴリコードでアクティブな商品を取得する
   *
   * @param categoryCode カテゴリコード
   * @param pageable ページング情報
   * @return 商品のページ
   */
  @Query(
      "SELECT p FROM Product p WHERE p.category.categoryCode = :categoryCode AND p.isActive = true")
  Page<Product> findByCategoryCodeAndIsActiveTrue(
      @Param("categoryCode") String categoryCode, Pageable pageable);

  /**
   * カテゴリコードとキーワードでアクティブな商品を検索する
   *
   * @param categoryCode カテゴリコード
   * @param keyword 検索キーワード
   * @param pageable ページング情報
   * @return 商品のページ
   */
  @Query(
      "SELECT p FROM Product p WHERE p.category.categoryCode = :categoryCode "
          + "AND p.isActive = true "
          + "AND (LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) "
          + "OR LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%')))")
  Page<Product> findByCategoryCodeAndKeyword(
      @Param("categoryCode") String categoryCode,
      @Param("keyword") String keyword,
      Pageable pageable);

  /**
   * カテゴリコードでアクティブな商品数を取得する
   *
   * @param categoryCode カテゴリコード
   * @return 商品数
   */
  @Query(
      "SELECT COUNT(p) FROM Product p WHERE p.category.categoryCode = :categoryCode "
          + "AND p.isActive = true")
  Long countByCategoryCodeAndIsActiveTrue(@Param("categoryCode") String categoryCode);
}
