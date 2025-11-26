package com.example.ec.repository;

import com.example.ec.entity.Category;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * カテゴリリポジトリ
 *
 * <p>カテゴリエンティティのデータアクセスを提供するリポジトリインターフェース。
 */
@Repository
public interface CategoryRepository extends JpaRepository<Category, String> {

  /**
   * アクティブなカテゴリを表示順で取得する
   *
   * @return アクティブなカテゴリのリスト
   */
  List<Category> findByIsActiveTrueOrderByDisplayOrder();

  /**
   * カテゴリコードでアクティブなカテゴリを取得する
   *
   * @param categoryCode カテゴリコード
   * @return カテゴリ（存在しない場合はOptional.empty()）
   */
  @Query("SELECT c FROM Category c WHERE c.categoryCode = :code AND c.isActive = true")
  Optional<Category> findActiveCategoryByCode(@Param("code") String categoryCode);
}
