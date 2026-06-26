CREATE TABLE users (
                       id BIGSERIAL PRIMARY KEY,
                       email VARCHAR(255) NOT NULL UNIQUE,
                       password VARCHAR(255) NOT NULL,
                       role VARCHAR(50) NOT NULL,
                       created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE links (
                       id BIGSERIAL PRIMARY KEY,
                       original_url TEXT NOT NULL,
                       short_code VARCHAR(20) NOT NULL UNIQUE,
                       click_count BIGINT NOT NULL DEFAULT 0,
                       created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                       expires_at TIMESTAMP,
                       user_id BIGINT NOT NULL,

                       CONSTRAINT fk_links_users
                           FOREIGN KEY (user_id)
                               REFERENCES users(id)
                               ON DELETE CASCADE
);