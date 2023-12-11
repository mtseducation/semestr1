CREATE TABLE comment (
    id BIGSERIAL primary key,
    article_id BIGINT REFERENCES article(id),
    text TEXT
);