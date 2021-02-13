package de.opengamebackend.collection.model.repositories;

import de.opengamebackend.collection.model.entities.ClaimedItemSet;
import de.opengamebackend.collection.model.entities.ItemSet;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
public class ItemSetRepositoryTests {
    private TestEntityManager entityManager;
    private ItemSetRepository itemSetRepository;

    @Autowired
    public ItemSetRepositoryTests(TestEntityManager entityManager, ItemSetRepository itemSetRepository) {
        this.entityManager = entityManager;
        this.itemSetRepository = itemSetRepository;
    }

    @Test
    public void givenUnclaimedItemSet_whenFindUnclaimedItemSetsByPlayerId_thenReturnItemSet() {
        // GIVEN
        ItemSet itemSet = new ItemSet();
        itemSet.setId("testItemSet");
        entityManager.persist(itemSet);

        entityManager.flush();

        // WHEN
        List<ItemSet> itemSets = itemSetRepository.findUnclaimedItemSetsByPlayerId("testPlayer");

        // THEN
        assertThat(itemSets).isNotNull();
        assertThat(itemSets).hasSize(1);
        assertThat(itemSets.get(0)).isEqualTo(itemSet);
    }

    @Test
    public void givenClaimedItemSet_whenFindUnclaimedItemSetsByPlayerId_thenDontReturnItemSet() {
        // GIVEN
        ItemSet itemSet = new ItemSet();
        itemSet.setId("testItemSet");
        entityManager.persist(itemSet);

        ClaimedItemSet claimedItemSet = new ClaimedItemSet();
        claimedItemSet.setItemSet(itemSet);
        claimedItemSet.setPlayerId("testPlayer");
        entityManager.persist(claimedItemSet);

        entityManager.flush();

        // WHEN
        List<ItemSet> itemSets = itemSetRepository.findUnclaimedItemSetsByPlayerId(claimedItemSet.getPlayerId());

        // THEN
        assertThat(itemSets).isNotNull();
        assertThat(itemSets).isEmpty();
    }
}
