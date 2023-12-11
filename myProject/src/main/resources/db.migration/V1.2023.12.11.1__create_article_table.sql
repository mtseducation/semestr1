CREATE TABLE article (
    id BIGSERIAL primary key,
    title VARCHAR(255),
    tags TEXT[]
);