package de.opengamebackend.collection.model.repositories;

import de.opengamebackend.collection.model.entities.Loadout;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LoadoutRepository extends CrudRepository<Loadout, Long> {
    List<Loadout> findByPlayerId(String playerId);
}
