package de.opengamebackend.collection.model.repositories;

import de.opengamebackend.collection.model.entities.Loadout;
import de.opengamebackend.collection.model.entities.LoadoutType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
public class LoadoutRepositoryTests {
    private TestEntityManager entityManager;
    private LoadoutRepository loadoutRepository;

    @Autowired
    public LoadoutRepositoryTests(TestEntityManager entityManager, LoadoutRepository loadoutRepository) {
        this.entityManager = entityManager;
        this.loadoutRepository = loadoutRepository;
    }

    @Test
    public void givenLoadouts_whenFindByPlayerId_thenReturnLoadouts() {
        // GIVEN
        String playerId = "testPlayer";

        LoadoutType loadoutType = new LoadoutType();
        loadoutType.setId("testLoadout");
        entityManager.persist(loadoutType);

        Loadout ownLoadout = new Loadout();
        ownLoadout.setPlayerId(playerId);
        ownLoadout.setType(loadoutType);
        entityManager.persist(ownLoadout);

        Loadout otherLoadout = new Loadout();
        otherLoadout.setPlayerId("otherPlayer");
        otherLoadout.setType(loadoutType);
        entityManager.persist(otherLoadout);

        entityManager.flush();

        // WHEN
        List<Loadout> items = loadoutRepository.findByPlayerId(playerId);

        // THEN
        assertThat(items).isNotNull();
        assertThat(items).containsExactly(ownLoadout);
    }
}
