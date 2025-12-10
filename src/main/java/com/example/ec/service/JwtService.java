package com.example.ec.service;

import com.example.ec.config.JwtConfig;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.util.Date;
import javax.crypto.SecretKey;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * JWTサービス
 *
 * <p>JWTトークンの生成、検証、解析を行うサービスクラス。
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class JwtService {

  private final JwtConfig jwtConfig;

  /** rememberMe有効時のアクセストークン有効期限延長倍率（7日間 = 7倍） */
  private static final long REMEMBER_ME_MULTIPLIER = 7L;

  /**
   * アクセストークンを生成する
   *
   * @param email ユーザーのメールアドレス（サブジェクトとして使用）
   * @return 生成されたJWTトークン
   */
  public String generateAccessToken(String email) {
    return generateAccessToken(email, false);
  }

  /**
   * アクセストークンを生成する（rememberMe対応）
   *
   * <p>rememberMeがtrueの場合、トークン有効期限を7倍に延長する。 これにより、通常1時間のトークンが7日間有効になる。
   *
   * @param email ユーザーのメールアドレス（サブジェクトとして使用）
   * @param rememberMe ログイン状態を保持するフラグ
   * @return 生成されたJWTトークン
   */
  public String generateAccessToken(String email, boolean rememberMe) {
    long expiration = jwtConfig.getAccessTokenExpiration();
    if (rememberMe) {
      expiration *= REMEMBER_ME_MULTIPLIER;
    }
    return Jwts.builder()
        .subject(email)
        .issuedAt(new Date())
        .expiration(new Date(System.currentTimeMillis() + expiration))
        .signWith(getSigningKey())
        .compact();
  }

  /**
   * アクセストークンの有効期限（秒）を取得する
   *
   * @param rememberMe ログイン状態を保持するフラグ
   * @return 有効期限（秒）
   */
  public long getAccessTokenExpirationSeconds(boolean rememberMe) {
    long expiration = jwtConfig.getAccessTokenExpiration();
    if (rememberMe) {
      expiration *= REMEMBER_ME_MULTIPLIER;
    }
    return expiration / 1000;
  }

  /**
   * リフレッシュトークンを生成する
   *
   * @param email ユーザーのメールアドレス
   * @return 生成されたリフレッシュトークン
   */
  public String generateRefreshToken(String email) {
    return Jwts.builder()
        .subject(email)
        .issuedAt(new Date())
        .expiration(new Date(System.currentTimeMillis() + jwtConfig.getRefreshTokenExpiration()))
        .signWith(getSigningKey())
        .compact();
  }

  /**
   * JWTトークンからユーザー名（メールアドレス）を取得する
   *
   * @param token JWTトークン
   * @return ユーザー名（メールアドレス）
   */
  public String getUsernameFromToken(String token) {
    return Jwts.parser()
        .verifyWith(getSigningKey())
        .build()
        .parseSignedClaims(token)
        .getPayload()
        .getSubject();
  }

  /**
   * JWTトークンを検証する
   *
   * @param token 検証対象のJWTトークン
   * @return 有効な場合はtrue、無効な場合はfalse
   */
  public boolean validateToken(String token) {
    try {
      Jwts.parser().verifyWith(getSigningKey()).build().parseSignedClaims(token);
      return true;
    } catch (MalformedJwtException e) {
      log.error("不正なJWTトークン: {}", e.getMessage());
    } catch (ExpiredJwtException e) {
      log.error("JWTトークンの有効期限切れ: {}", e.getMessage());
    } catch (UnsupportedJwtException e) {
      log.error("サポートされていないJWTトークン: {}", e.getMessage());
    } catch (IllegalArgumentException e) {
      log.error("JWTクレームが空です: {}", e.getMessage());
    }
    return false;
  }

  /**
   * 署名用キーを取得する
   *
   * @return 署名用のSecretKey
   */
  private SecretKey getSigningKey() {
    byte[] keyBytes = Decoders.BASE64.decode(jwtConfig.getSecret());
    return Keys.hmacShaKeyFor(keyBytes);
  }
}
