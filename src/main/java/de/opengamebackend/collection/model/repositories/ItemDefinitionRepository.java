package de.opengamebackend.collection.model.repositories;

import de.opengamebackend.collection.model.entities.ItemDefinition;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemDefinitionRepository extends CrudRepository<ItemDefinition, String> {
}
