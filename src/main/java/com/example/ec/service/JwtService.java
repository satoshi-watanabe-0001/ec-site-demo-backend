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

  /**
   * アクセストークンを生成する
   *
   * @param email ユーザーのメールアドレス（サブジェクトとして使用）
   * @return 生成されたJWTトークン
   */
  public String generateAccessToken(String email) {
    return Jwts.builder()
        .subject(email)
        .issuedAt(new Date())
        .expiration(new Date(System.currentTimeMillis() + jwtConfig.getAccessTokenExpiration()))
        .signWith(getSigningKey())
        .compact();
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
