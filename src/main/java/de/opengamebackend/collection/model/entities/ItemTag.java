package de.opengamebackend.collection.model.entities;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "collection_itemtag")
public class ItemTag {
    @Id
    private String tag;

    public ItemTag() {
    }

    public ItemTag(String tag) {
        this.tag = tag;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }
}
