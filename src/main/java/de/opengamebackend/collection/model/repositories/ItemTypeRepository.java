package de.opengamebackend.collection.model.repositories;

import de.opengamebackend.collection.model.entities.ItemType;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemTypeRepository extends CrudRepository<ItemType, String> {
}
