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
}
