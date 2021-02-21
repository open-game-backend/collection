package de.opengamebackend.collection.model.entities;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Objects;

@Entity
@Table(name = "collection_itemtag")
public class ItemTag {
    @Id
    private String tag;

    public ItemTag() {
    }

    public ItemTag(String tag) {
        this.tag = tag;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ItemTag itemTag = (ItemTag) o;
        return tag.equals(itemTag.tag);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tag);
    }
}
