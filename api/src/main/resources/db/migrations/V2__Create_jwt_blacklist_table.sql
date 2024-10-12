CREATE TABLE jwt_blacklist (
    id SERIAL PRIMARY KEY,
    token VARCHAR(255) NOT NULL,
    expire_date TIMESTAMP NOT NULL
);

CREATE INDEX idx_jwt_blacklist_token ON jwt_blacklist (token);
CREATE INDEX idx_jwt_blacklist_expire_date ON jwt_blacklist (expire_date);

