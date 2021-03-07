package de.opengamebackend.collection.model.entities;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "collection_itemcontainer")
public class ItemContainer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    private ItemDefinition owningItemDefinition;

    private int itemCount;

    @OneToMany(mappedBy = "itemContainer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ContainedItem> containedItems;

    public long getId() {
        return id;
    }

    public ItemDefinition getOwningItemDefinition() {
        return owningItemDefinition;
    }

    public void setOwningItemDefinition(ItemDefinition owningItemDefinition) {
        this.owningItemDefinition = owningItemDefinition;
    }

    public int getItemCount() {
        return itemCount;
    }

    public void setItemCount(int itemCount) {
        this.itemCount = itemCount;
    }

    public List<ContainedItem> getContainedItems() {
        return containedItems;
    }

    public void setContainedItems(List<ContainedItem> containedItems) {
        this.containedItems = containedItems;
    }
}
