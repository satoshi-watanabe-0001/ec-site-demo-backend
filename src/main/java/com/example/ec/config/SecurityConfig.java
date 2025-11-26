package com.example.ec.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

/**
 * セキュリティ設定クラス
 *
 * <p>アプリケーションのセキュリティ設定を定義するクラス。
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

  /**
   * セキュリティフィルターチェーンを設定する
   *
   * @param http HttpSecurity
   * @return SecurityFilterChain
   * @throws Exception 設定エラー
   */
  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http.csrf(csrf -> csrf.disable())
        .authorizeHttpRequests(
            auth ->
                auth.requestMatchers("/api/v1/products/categories/**")
                    .permitAll()
                    .requestMatchers("/api/v1/health/**")
                    .permitAll()
                    .requestMatchers("/actuator/**")
                    .permitAll()
                    .anyRequest()
                    .authenticated());

    return http.build();
  }
}
