CREATE TABLE collection_itemcontainer (
    id INT NOT NULL AUTO_INCREMENT,
    owning_item_definition_id VARCHAR(100) NOT NULL,
    item_count INT(10) UNSIGNED NOT NULL,

    PRIMARY KEY (id),
    FOREIGN KEY (owning_item_definition_id) REFERENCES collection_itemdefinition(id)
);
