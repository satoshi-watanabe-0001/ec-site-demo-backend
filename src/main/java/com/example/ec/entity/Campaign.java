package com.example.ec.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
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
 * キャンペーンエンティティ
 *
 * <p>商品に適用されるキャンペーン情報を管理するエンティティクラス。 バッジテキストや有効期間などを保持する。
 */
@Entity
@Table(name = "campaigns")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Campaign {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "campaign_code", nullable = false, unique = true, length = 100)
  private String campaignCode;

  @Column(name = "campaign_name", nullable = false, length = 200)
  private String campaignName;

  @Column(name = "badge_text", length = 100)
  private String badgeText;

  @Column(name = "valid_from")
  private Instant validFrom;

  @Column(name = "valid_to")
  private Instant validTo;

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
  }

  /** エンティティ更新時のコールバック（更新タイムスタンプの設定） */
  @PreUpdate
  protected void onUpdate() {
    updatedAt = Instant.now();
  }

  /**
   * キャンペーンが現在有効かどうかを判定する
   *
   * @return 有効期間内かつアクティブな場合true
   */
  public boolean isCurrentlyValid() {
    if (!Boolean.TRUE.equals(isActive)) {
      return false;
    }
    Instant now = Instant.now();
    boolean afterStart = validFrom == null || !now.isBefore(validFrom);
    boolean beforeEnd = validTo == null || !now.isAfter(validTo);
    return afterStart && beforeEnd;
  }
}
