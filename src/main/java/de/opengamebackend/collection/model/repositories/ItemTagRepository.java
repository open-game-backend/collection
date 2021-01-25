package de.opengamebackend.collection.model.repositories;

import de.opengamebackend.collection.model.entities.ItemTag;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemTagRepository extends CrudRepository<ItemTag, String> {
}
