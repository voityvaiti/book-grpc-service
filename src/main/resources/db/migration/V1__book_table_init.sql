CREATE TABLE book
(
    id       uuid DEFAULT gen_random_uuid(),
    title VARCHAR(256) NOT NULL,
    author VARCHAR(256) NOT NULL,
    isbn CHAR(13) NOT NULL,
    quantity INT NOT NULL,
    PRIMARY KEY (id)
);