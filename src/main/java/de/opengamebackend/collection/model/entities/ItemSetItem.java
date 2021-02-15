package de.opengamebackend.collection.model.entities;

import javax.persistence.*;

@Entity
@Table(name = "collection_itemsetitem")
public class ItemSetItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    private ItemSet itemSet;

    @ManyToOne(optional = false)
    private ItemDefinition itemDefinition;

    private int count;

    public ItemSetItem() {
    }

    public ItemSetItem(ItemSet itemSet, ItemDefinition itemDefinition, int count) {
        this.itemSet = itemSet;
        this.itemDefinition = itemDefinition;
        this.count = count;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public ItemSet getItemSet() {
        return itemSet;
    }

    public void setItemSet(ItemSet itemSet) {
        this.itemSet = itemSet;
    }

    public ItemDefinition getItemDefinition() {
        return itemDefinition;
    }

    public void setItemDefinition(ItemDefinition itemDefinition) {
        this.itemDefinition = itemDefinition;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
