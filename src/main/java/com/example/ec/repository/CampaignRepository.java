package com.example.ec.repository;

import com.example.ec.entity.Campaign;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * キャンペーンリポジトリ
 *
 * <p>キャンペーンエンティティのデータアクセスを提供するリポジトリインターフェース。
 */
@Repository
public interface CampaignRepository extends JpaRepository<Campaign, Long> {

  /**
   * 現在有効なキャンペーンを取得する
   *
   * @return 有効なキャンペーンのリスト
   */
  @Query(
      "SELECT c FROM Campaign c WHERE c.isActive = true "
          + "AND (c.validFrom IS NULL OR c.validFrom <= CURRENT_TIMESTAMP) "
          + "AND (c.validTo IS NULL OR c.validTo >= CURRENT_TIMESTAMP)")
  List<Campaign> findCurrentlyValidCampaigns();
}
