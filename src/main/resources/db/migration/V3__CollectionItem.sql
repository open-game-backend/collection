CREATE TABLE collection_item (
    id INT NOT NULL AUTO_INCREMENT,
    player_id VARCHAR(100) NOT NULL,
    item_definition VARCHAR(100) NOT NULL,
    count INT(10) UNSIGNED NOT NULL,

    PRIMARY KEY (id),
    FOREIGN KEY (item_definition) REFERENCES collection_itemdefinition(id)
);

CREATE INDEX ix_collection_item_player_id ON collection_item (player_id);
