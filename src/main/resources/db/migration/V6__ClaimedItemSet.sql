CREATE TABLE collection_claimeditemset (
    id INT NOT NULL AUTO_INCREMENT,
    player_id VARCHAR(100) NOT NULL,
    item_set_id VARCHAR(100) NOT NULL,

    PRIMARY KEY (id),
    FOREIGN KEY (item_set_id) REFERENCES collection_itemset(id)
);
