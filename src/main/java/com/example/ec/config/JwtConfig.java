package com.example.ec.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * JWT設定クラス
 *
 * <p>application.ymlからJWT関連の設定値を読み込むための設定クラス。
 */
@Data
@Component
@ConfigurationProperties(prefix = "jwt")
public class JwtConfig {

  /** JWT署名用シークレットキー（Base64エンコード） */
  private String secret;

  /** アクセストークンの有効期限（ミリ秒） */
  private long accessTokenExpiration;

  /** リフレッシュトークンの有効期限（ミリ秒） */
  private long refreshTokenExpiration;
}
