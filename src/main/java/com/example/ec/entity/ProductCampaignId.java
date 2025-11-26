package com.example.ec.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 商品キャンペーン複合主キー
 *
 * <p>ProductCampaignエンティティの複合主キーを表すクラス。
 */
@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductCampaignId implements Serializable {

  private static final long serialVersionUID = 1L;

  @Column(name = "product_id")
  private Long productId;

  @Column(name = "campaign_id")
  private Long campaignId;
}
