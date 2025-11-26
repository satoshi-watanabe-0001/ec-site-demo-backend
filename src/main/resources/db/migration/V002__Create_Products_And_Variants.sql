-- 商品テーブルの作成
-- EC-270: Product Categories Common API
CREATE TABLE products (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(200) NOT NULL,
    description TEXT,
    price DECIMAL(10, 2) NOT NULL,
    category_code VARCHAR(50) NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (category_code) REFERENCES categories(category_code)
);

-- 商品バリアントテーブルの作成
CREATE TABLE product_variants (
    id BIGSERIAL PRIMARY KEY,
    product_id BIGINT NOT NULL,
    manufacturer VARCHAR(100),
    model_name VARCHAR(200),
    storage_capacity VARCHAR(50),
    color_code VARCHAR(20),
    color_name VARCHAR(50),
    image_urls JSONB,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (product_id) REFERENCES products(id)
);

-- インデックスの作成（検索性能向上のため）
CREATE INDEX idx_products_category_code ON products(category_code);
CREATE INDEX idx_products_is_active ON products(is_active);
CREATE INDEX idx_products_name ON products(name);
CREATE INDEX idx_product_variants_product_id ON product_variants(product_id);
