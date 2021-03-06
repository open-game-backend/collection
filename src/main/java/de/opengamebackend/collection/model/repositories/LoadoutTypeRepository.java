package de.opengamebackend.collection.model.repositories;

import de.opengamebackend.collection.model.entities.LoadoutType;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LoadoutTypeRepository extends CrudRepository<LoadoutType, String> {
}
