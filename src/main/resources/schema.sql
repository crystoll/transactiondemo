CREATE TABLE order_line (
    id   INTEGER      NOT NULL AUTO_INCREMENT,
    product_id INTEGER NOT NULL,
    quantity INTEGER NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE product (
    id   INTEGER      NOT NULL AUTO_INCREMENT,
    product_id INTEGER NOT NULL,
    quantity INTEGER NOT NULL,
    PRIMARY KEY (id)
);