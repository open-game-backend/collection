package de.opengamebackend.collection.model.entities;

import javax.persistence.*;

@Entity
@Table(name = "collection_loadoutitem")
public class LoadoutItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    private Loadout loadout;

    @ManyToOne(optional = false)
    private ItemDefinition itemDefinition;

    private int count;

    public long getId() {
        return id;
    }

    public Loadout getLoadout() {
        return loadout;
    }

    public void setLoadout(Loadout loadout) {
        this.loadout = loadout;
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
