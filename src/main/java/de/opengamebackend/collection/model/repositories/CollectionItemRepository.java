package de.opengamebackend.collection.model.repositories;

import de.opengamebackend.collection.model.entities.CollectionItem;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CollectionItemRepository extends CrudRepository<CollectionItem, Long> {
    List<CollectionItem> findByPlayerId(String playerId);
}
