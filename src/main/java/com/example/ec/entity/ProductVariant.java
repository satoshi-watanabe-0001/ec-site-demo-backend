package com.example.ec.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

/**
 * 商品バリアントエンティティ
 *
 * <p>商品のバリエーション情報を管理するエンティティクラス。 ストレージ容量、カラー、メーカー、モデル名などを保持する。
 */
@Entity
@Table(name = "product_variants")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductVariant {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "product_id", nullable = false)
  private Product product;

  @Column(name = "manufacturer", length = 100)
  private String manufacturer;

  @Column(name = "model_name", length = 200)
  private String modelName;

  @Column(name = "storage_capacity", length = 50)
  private String storageCapacity;

  @Column(name = "color_code", length = 20)
  private String colorCode;

  @Column(name = "color_name", length = 50)
  private String colorName;

  @JdbcTypeCode(SqlTypes.JSON)
  @Column(name = "image_urls", columnDefinition = "jsonb")
  private List<String> imageUrls;

  @Column(name = "created_at", nullable = false, updatable = false)
  private Instant createdAt;

  @Column(name = "updated_at", nullable = false)
  private Instant updatedAt;

  /** エンティティ作成時のコールバック（タイムスタンプの設定） */
  @PrePersist
  protected void onCreate() {
    Instant now = Instant.now();
    createdAt = now;
    updatedAt = now;
  }

  /** エンティティ更新時のコールバック（更新タイムスタンプの設定） */
  @PreUpdate
  protected void onUpdate() {
    updatedAt = Instant.now();
  }
}
