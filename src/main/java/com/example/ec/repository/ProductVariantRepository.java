package com.example.ec.repository;

import com.example.ec.entity.ProductVariant;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * 商品バリアントリポジトリ
 *
 * <p>商品バリアントエンティティのデータアクセスを提供するリポジトリインターフェース。
 */
@Repository
public interface ProductVariantRepository extends JpaRepository<ProductVariant, Long> {

  /**
   * 商品IDでバリアントを取得する
   *
   * @param productId 商品ID
   * @return バリアントのリスト
   */
  List<ProductVariant> findByProductId(Long productId);

  /**
   * 商品IDリストでバリアントを取得する
   *
   * @param productIds 商品IDリスト
   * @return バリアントのリスト
   */
  @Query("SELECT pv FROM ProductVariant pv WHERE pv.product.id IN :productIds")
  List<ProductVariant> findByProductIds(@Param("productIds") List<Long> productIds);
}
