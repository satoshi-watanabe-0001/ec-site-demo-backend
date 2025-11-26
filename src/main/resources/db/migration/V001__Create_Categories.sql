-- カテゴリテーブルの作成
-- EC-270: Product Categories Common API
CREATE TABLE categories (
    category_code VARCHAR(50) PRIMARY KEY,
    display_name VARCHAR(100) NOT NULL,
    hero_image_url TEXT,
    lead_text TEXT,
    display_order INTEGER NOT NULL DEFAULT 0,
    is_active BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 初期カテゴリデータの投入
INSERT INTO categories (category_code, display_name, display_order) VALUES 
('iphone', 'iPhone', 1),
('android', 'Android', 2),
('refurbished', 'ドコモ認定リユース品', 3),
('accessories', 'アクセサリ', 4);

-- カテゴリコードにインデックスを作成（検索性能向上のため）
CREATE INDEX idx_categories_display_order ON categories(display_order);
CREATE INDEX idx_categories_is_active ON categories(is_active);
