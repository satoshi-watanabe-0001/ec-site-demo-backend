-- ユーザーテーブル作成
-- 認証用のユーザー情報を管理するテーブル

CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(255) NOT NULL UNIQUE,
    name VARCHAR(255) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- メールアドレスの検索用インデックス
CREATE INDEX idx_users_email ON users(email);

-- コメント
COMMENT ON TABLE users IS 'ユーザー情報テーブル';
COMMENT ON COLUMN users.id IS 'ユーザーID（主キー）';
COMMENT ON COLUMN users.email IS 'メールアドレス（一意制約、認証に使用）';
COMMENT ON COLUMN users.name IS 'ユーザー名';
COMMENT ON COLUMN users.password_hash IS 'パスワードハッシュ（BCrypt）';
COMMENT ON COLUMN users.created_at IS '作成日時';
COMMENT ON COLUMN users.updated_at IS '更新日時';
