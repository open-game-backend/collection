package de.opengamebackend.collection.model.entities;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "collection_itemdefinition")
public class ItemDefinition {
    @Id
    private String id;

    @ManyToMany
    private List<ItemTag> itemTags;

    public ItemDefinition() {
        itemTags = new ArrayList<>();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<ItemTag> getItemTags() {
        return itemTags;
    }

    public void setItemTags(List<ItemTag> itemTags) {
        this.itemTags = itemTags;
    }
}
