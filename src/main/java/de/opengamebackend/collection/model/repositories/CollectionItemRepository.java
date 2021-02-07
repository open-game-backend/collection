package de.opengamebackend.collection.model.repositories;

import de.opengamebackend.collection.model.entities.CollectionItem;
import de.opengamebackend.collection.model.entities.ItemDefinition;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CollectionItemRepository extends CrudRepository<CollectionItem, Long> {
    List<CollectionItem> findByPlayerId(String playerId);
    Optional<CollectionItem> findByPlayerIdAndItemDefinition(String playerId, ItemDefinition itemDefinition);
    void deleteByPlayerIdAndItemDefinition(String playerId, ItemDefinition itemDefinition);
}
