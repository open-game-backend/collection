package de.opengamebackend.collection.model.entities;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "collection_containeditem")
public class ContainedItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    private ItemContainer itemContainer;

    @ManyToMany
    private List<ItemTag> requiredTags;

    private int relativeProbability;

    public long getId() {
        return id;
    }

    public ItemContainer getItemContainer() {
        return itemContainer;
    }

    public void setItemContainer(ItemContainer itemContainer) {
        this.itemContainer = itemContainer;
    }

    public List<ItemTag> getRequiredTags() {
        return requiredTags;
    }

    public void setRequiredTags(List<ItemTag> requiredTags) {
        this.requiredTags = requiredTags;
    }

    public int getRelativeProbability() {
        return relativeProbability;
    }

    public void setRelativeProbability(int relativeProbability) {
        this.relativeProbability = relativeProbability;
    }
}
