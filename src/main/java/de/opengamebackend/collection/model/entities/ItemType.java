package de.opengamebackend.collection.model.entities;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "collection_itemtype")
public class ItemType {
    @Id
    private String type;

    public ItemType() {
    }

    public ItemType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
