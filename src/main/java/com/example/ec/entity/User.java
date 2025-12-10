package com.example.ec.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

/**
 * ユーザーエンティティ
 *
 * <p>認証用のユーザー情報を管理するエンティティクラス。 メールアドレスとパスワードによる認証をサポートする。
 */
@Entity
@Table(name = "users")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class User {

  /** ユーザーID（主キー） */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  /** メールアドレス（一意制約、認証に使用） */
  @Column(unique = true, nullable = false)
  private String email;

  /** ユーザー名 */
  @Column(nullable = false)
  private String name;

  /** パスワードハッシュ（BCrypt） */
  @Column(nullable = false)
  private String passwordHash;

  /** 作成日時 */
  @CreationTimestamp private LocalDateTime createdAt;

  /** 更新日時 */
  @UpdateTimestamp private LocalDateTime updatedAt;
}
