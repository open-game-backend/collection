CREATE TABLE collection_itemdefinition (
    id VARCHAR(100) NOT NULL,
    item_type_type VARCHAR(100) NULL,

    PRIMARY KEY (id),
    FOREIGN KEY (item_type_type) REFERENCES collection_itemtype(type)
);
