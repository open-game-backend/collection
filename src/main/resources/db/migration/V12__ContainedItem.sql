CREATE TABLE collection_containeditem (
    id INT NOT NULL AUTO_INCREMENT,
    item_container_id INT NOT NULL,
    relative_probability INT(10) UNSIGNED NOT NULL,

    PRIMARY KEY (id),
    FOREIGN KEY (item_container_id) REFERENCES collection_itemcontainer(id)
);

CREATE TABLE collection_containeditem_required_tags (
    contained_item_id INT NOT NULL,
    required_tags_tag VARCHAR(100) NOT NULL,

    PRIMARY KEY (contained_item_id,required_tags_tag),

    FOREIGN KEY (contained_item_id) REFERENCES collection_containeditem(id),
    FOREIGN KEY (required_tags_tag) REFERENCES collection_itemtag(tag)
);
