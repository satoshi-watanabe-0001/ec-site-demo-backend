package com.example.ec.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * カテゴリ詳細リクエストDTO
 *
 * <p>カテゴリ詳細・フィルタリングAPIのリクエストパラメータを表すDTO。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryDetailRequest {

  @Size(max = 100, message = "キーワードは100文字以内で指定してください")
  private String keyword;

  @Min(value = 0, message = "ページ番号は0以上である必要があります")
  @Builder.Default
  private Integer page = 0;

  @Min(value = 1, message = "ページサイズは1以上である必要があります")
  @Max(value = 100, message = "ページサイズは100以下である必要があります")
  @Builder.Default
  private Integer size = 20;

  @Builder.Default private String sort = "name";

  @Builder.Default private String order = "asc";
}
