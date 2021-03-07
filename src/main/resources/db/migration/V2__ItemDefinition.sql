CREATE TABLE collection_itemdefinition (
    id VARCHAR(100) NOT NULL,
    max_count INT(10) UNSIGNED NULL,
    PRIMARY KEY (id)
);

CREATE TABLE collection_itemdefinition_item_tags (
    item_definition_id VARCHAR(100) NOT NULL,
    item_tags_tag VARCHAR(100) NOT NULL,

    PRIMARY KEY (item_definition_id,item_tags_tag),

    FOREIGN KEY (item_definition_id) REFERENCES collection_itemdefinition(id),
    FOREIGN KEY (item_tags_tag) REFERENCES collection_itemtag(tag)
);
