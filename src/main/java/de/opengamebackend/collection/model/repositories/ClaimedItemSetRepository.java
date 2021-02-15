package de.opengamebackend.collection.model.repositories;

import de.opengamebackend.collection.model.entities.ClaimedItemSet;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClaimedItemSetRepository extends CrudRepository<ClaimedItemSet, Long> {
    List<ClaimedItemSet> findByPlayerId(String playerId);
}
