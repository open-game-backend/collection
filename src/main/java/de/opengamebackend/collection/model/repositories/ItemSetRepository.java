package de.opengamebackend.collection.model.repositories;

import de.opengamebackend.collection.model.entities.ItemSet;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemSetRepository extends CrudRepository<ItemSet, String> {
    @Query("SELECT s FROM ItemSet s WHERE NOT EXISTS " +
            "(SELECT c FROM ClaimedItemSet c WHERE c.itemSet=s AND c.playerId=:playerId)")
    List<ItemSet> findUnclaimedItemSetsByPlayerId(String playerId);
}
