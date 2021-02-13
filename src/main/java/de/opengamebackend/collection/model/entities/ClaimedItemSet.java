package de.opengamebackend.collection.model.entities;

import javax.persistence.*;

@Entity
@Table(name = "collection_claimeditemset")
public class ClaimedItemSet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    private String playerId;

    @ManyToOne(optional = false)
    private ItemSet itemSet;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getPlayerId() {
        return playerId;
    }

    public void setPlayerId(String playerId) {
        this.playerId = playerId;
    }

    public ItemSet getItemSet() {
        return itemSet;
    }

    public void setItemSet(ItemSet itemSet) {
        this.itemSet = itemSet;
    }
}
