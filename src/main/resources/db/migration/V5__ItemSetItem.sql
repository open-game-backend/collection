CREATE TABLE collection_itemsetitem (
    id INT NOT NULL AUTO_INCREMENT,
    item_set_id VARCHAR(100) NOT NULL,
    item_definition_id VARCHAR(100) NOT NULL,
    count INT(10) UNSIGNED NOT NULL,

    PRIMARY KEY (id),
    FOREIGN KEY (item_set_id) REFERENCES collection_itemset(id),
    FOREIGN KEY (item_definition_id) REFERENCES collection_itemdefinition(id)
);
