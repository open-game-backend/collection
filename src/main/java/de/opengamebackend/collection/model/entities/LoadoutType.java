package de.opengamebackend.collection.model.entities;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "collection_loadouttype")
public class LoadoutType {
    @Id
    private String id;

    @OneToMany(mappedBy = "type", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LoadoutRule> rules;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<LoadoutRule> getRules() {
        return rules;
    }
}
