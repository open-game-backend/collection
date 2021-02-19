CREATE TABLE collection_loadoutrule (
    id INT NOT NULL AUTO_INCREMENT,
    type VARCHAR(100) NOT NULL,
    item_tag VARCHAR(100) NOT NULL,
    min_total INT(32) UNSIGNED NULL,
    max_total INT(32) UNSIGNED NULL,
    max_copies INT(32) UNSIGNED NULL,

    PRIMARY KEY (id),
    FOREIGN KEY (type) REFERENCES collection_loadouttype(id),
    FOREIGN KEY (item_tag) REFERENCES collection_itemtag(tag)
);
