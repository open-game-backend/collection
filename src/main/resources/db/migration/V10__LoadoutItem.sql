CREATE TABLE collection_loadoutitem (
    id INT NOT NULL AUTO_INCREMENT,
    loadout_id INT NOT NULL,
    item_definition_id VARCHAR(100) NOT NULL,
    count INT(10) UNSIGNED NOT NULL,

    PRIMARY KEY (id),
    FOREIGN KEY (loadout_id) REFERENCES collection_loadout(id),
    FOREIGN KEY (item_definition_id) REFERENCES collection_itemdefinition(id)
);
