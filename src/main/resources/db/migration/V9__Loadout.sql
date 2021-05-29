CREATE TABLE collection_loadout (
    id INT NOT NULL AUTO_INCREMENT,
    player_id VARCHAR(100) NOT NULL,
    type_id VARCHAR(100) NOT NULL,

    PRIMARY KEY (id),
    FOREIGN KEY (type_id) REFERENCES collection_loadouttype(id)
);

CREATE INDEX ix_collection_loadout_player_id ON collection_loadout (player_id);
