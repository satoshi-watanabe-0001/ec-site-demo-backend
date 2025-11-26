package com.example.ec.exception;

/**
 * カテゴリが見つからない例外
 *
 * <p>指定されたカテゴリコードに対応するカテゴリが存在しない場合にスローされる例外。
 */
public class CategoryNotFoundException extends RuntimeException {

  private final String categoryCode;

  /**
   * コンストラクタ
   *
   * @param categoryCode 見つからなかったカテゴリコード
   */
  public CategoryNotFoundException(String categoryCode) {
    super(String.format("カテゴリが見つかりません: %s", categoryCode));
    this.categoryCode = categoryCode;
  }

  /**
   * カテゴリコードを取得する
   *
   * @return カテゴリコード
   */
  public String getCategoryCode() {
    return categoryCode;
  }
}
