package com.example.ec.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * カテゴリエンティティ
 *
 * <p>商品カテゴリの基本情報を管理するエンティティクラス。 iPhone、Android、リユース品、アクセサリなどのカテゴリを表す。
 */
@Entity
@Table(name = "categories")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Category {

  @Id
  @Column(name = "category_code", length = 50)
  private String categoryCode;

  @Column(name = "display_name", nullable = false, length = 100)
  private String displayName;

  @Column(name = "hero_image_url", columnDefinition = "TEXT")
  private String heroImageUrl;

  @Column(name = "lead_text", columnDefinition = "TEXT")
  private String leadText;

  @Column(name = "display_order", nullable = false)
  private Integer displayOrder;

  @Column(name = "is_active", nullable = false)
  private Boolean isActive;

  @Column(name = "created_at", nullable = false, updatable = false)
  private Instant createdAt;

  @Column(name = "updated_at", nullable = false)
  private Instant updatedAt;

  /** エンティティ作成時のコールバック（タイムスタンプとデフォルト値の設定） */
  @PrePersist
  protected void onCreate() {
    Instant now = Instant.now();
    createdAt = now;
    updatedAt = now;
    if (isActive == null) {
      isActive = true;
    }
    if (displayOrder == null) {
      displayOrder = 0;
    }
  }

  /** エンティティ更新時のコールバック（更新タイムスタンプの設定） */
  @PreUpdate
  protected void onUpdate() {
    updatedAt = Instant.now();
  }
}
