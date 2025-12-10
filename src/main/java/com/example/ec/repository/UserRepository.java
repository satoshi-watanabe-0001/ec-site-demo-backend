package com.example.ec.repository;

import com.example.ec.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * ユーザーリポジトリ
 *
 * <p>ユーザーエンティティのデータアクセスを提供するリポジトリインターフェース。
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

  /**
   * メールアドレスでユーザーを検索する
   *
   * @param email メールアドレス
   * @return ユーザー（存在しない場合はOptional.empty()）
   */
  Optional<User> findByEmail(String email);

  /**
   * メールアドレスの存在確認
   *
   * @param email メールアドレス
   * @return 存在する場合はtrue
   */
  boolean existsByEmail(String email);
}
