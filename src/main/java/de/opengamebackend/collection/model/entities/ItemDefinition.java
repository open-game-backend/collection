package de.opengamebackend.collection.model.entities;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "collection_itemdefinition")
public class ItemDefinition {
    @Id
    private String id;

    private Integer maxCount;

    @ManyToMany
    private List<ItemTag> itemTags;

    @OneToMany(mappedBy = "owningItemDefinition", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemContainer> containers;

    public ItemDefinition() {
        itemTags = new ArrayList<>();
        containers = new ArrayList<>();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getMaxCount() {
        return maxCount;
    }

    public void setMaxCount(Integer maxCount) {
        this.maxCount = maxCount;
    }

    public List<ItemTag> getItemTags() {
        return itemTags;
    }

    public void setItemTags(List<ItemTag> itemTags) {
        this.itemTags = itemTags;
    }

    public List<ItemContainer> getContainers() {
        return containers;
    }

    public void setContainers(List<ItemContainer> containers) {
        this.containers = containers;
    }
}
