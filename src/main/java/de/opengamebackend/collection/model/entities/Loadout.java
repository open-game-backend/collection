package de.opengamebackend.collection.model.entities;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "collection_loadout")
public class Loadout {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    private String playerId;

    @OneToMany(mappedBy = "loadout", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LoadoutItem> items;

    @ManyToOne(optional = false)
    private LoadoutType type;

    public Loadout() {
        items = new ArrayList<>();
    }

    public long getId() {
        return id;
    }

    public String getPlayerId() {
        return playerId;
    }

    public void setPlayerId(String playerId) {
        this.playerId = playerId;
    }

    public List<LoadoutItem> getItems() {
        return items;
    }

    public void setItems(List<LoadoutItem> items) {
        this.items = items;
    }

    public LoadoutType getType() {
        return type;
    }

    public void setType(LoadoutType type) {
        this.type = type;
    }
}
