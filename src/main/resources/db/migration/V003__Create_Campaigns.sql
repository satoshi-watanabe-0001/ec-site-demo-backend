-- キャンペーンテーブルの作成
-- EC-270: Product Categories Common API
CREATE TABLE campaigns (
    id BIGSERIAL PRIMARY KEY,
    campaign_code VARCHAR(100) UNIQUE NOT NULL,
    campaign_name VARCHAR(200) NOT NULL,
    badge_text VARCHAR(100),
    valid_from TIMESTAMP,
    valid_to TIMESTAMP,
    is_active BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 商品キャンペーン関連テーブルの作成
CREATE TABLE product_campaigns (
    product_id BIGINT NOT NULL,
    campaign_id BIGINT NOT NULL,
    PRIMARY KEY (product_id, campaign_id),
    FOREIGN KEY (product_id) REFERENCES products(id),
    FOREIGN KEY (campaign_id) REFERENCES campaigns(id)
);

-- インデックスの作成（検索性能向上のため）
CREATE INDEX idx_campaigns_campaign_code ON campaigns(campaign_code);
CREATE INDEX idx_campaigns_is_active ON campaigns(is_active);
CREATE INDEX idx_campaigns_valid_period ON campaigns(valid_from, valid_to);
CREATE INDEX idx_product_campaigns_campaign_id ON product_campaigns(campaign_id);
