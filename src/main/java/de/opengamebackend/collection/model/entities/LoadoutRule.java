package de.opengamebackend.collection.model.entities;

import javax.persistence.*;

@Entity
@Table(name = "collection_loadoutrule")
public class LoadoutRule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    private LoadoutType type;

    @ManyToOne
    private ItemTag itemTag;

    private Integer minTotal;
    private Integer maxTotal;
    private Integer maxCopies;

    public long getId() {
        return id;
    }

    public LoadoutType getType() {
        return type;
    }

    public void setType(LoadoutType type) {
        this.type = type;
    }

    public ItemTag getItemTag() {
        return itemTag;
    }

    public void setItemTag(ItemTag itemTag) {
        this.itemTag = itemTag;
    }

    public Integer getMinTotal() {
        return minTotal;
    }

    public void setMinTotal(Integer minTotal) {
        this.minTotal = minTotal;
    }

    public Integer getMaxTotal() {
        return maxTotal;
    }

    public void setMaxTotal(Integer maxTotal) {
        this.maxTotal = maxTotal;
    }

    public Integer getMaxCopies() {
        return maxCopies;
    }

    public void setMaxCopies(Integer maxCopies) {
        this.maxCopies = maxCopies;
    }
}
