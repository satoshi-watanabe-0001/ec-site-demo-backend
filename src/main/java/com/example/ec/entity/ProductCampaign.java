package com.example.ec.entity;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 商品キャンペーン関連エンティティ
 *
 * <p>商品とキャンペーンの多対多関連を管理する中間テーブルエンティティ。
 */
@Entity
@Table(name = "product_campaigns")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductCampaign {

  @EmbeddedId private ProductCampaignId id;

  @ManyToOne(fetch = FetchType.LAZY)
  @MapsId("productId")
  @JoinColumn(name = "product_id")
  private Product product;

  @ManyToOne(fetch = FetchType.LAZY)
  @MapsId("campaignId")
  @JoinColumn(name = "campaign_id")
  private Campaign campaign;
}
