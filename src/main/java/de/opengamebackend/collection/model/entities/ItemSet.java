package de.opengamebackend.collection.model.entities;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "collection_itemset")
public class ItemSet {
    @Id
    private String id;

    @OneToMany(mappedBy = "itemSet", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemSetItem> items;

    public ItemSet() {
        items = new ArrayList<>();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<ItemSetItem> getItems() {
        return items;
    }

    public void setItems(List<ItemSetItem> items) {
        this.items = items;
    }
}
