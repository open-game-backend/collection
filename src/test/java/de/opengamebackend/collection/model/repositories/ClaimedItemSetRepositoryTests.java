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
public class ClaimedItemSetRepositoryTests {
    private TestEntityManager entityManager;
    private ClaimedItemSetRepository claimedItemSetRepository;

    @Autowired
    public ClaimedItemSetRepositoryTests(TestEntityManager entityManager, ClaimedItemSetRepository claimedItemSetRepository) {
        this.entityManager = entityManager;
        this.claimedItemSetRepository = claimedItemSetRepository;
    }

    @Test
    public void givenClaimedItemSet_whenFindByPlayerId_thenReturnItemSet() {
        // GIVEN
        ItemSet itemSet = new ItemSet();
        itemSet.setId("testItemSet");
        entityManager.persist(itemSet);

        ClaimedItemSet claimedItemSet = new ClaimedItemSet();
        claimedItemSet.setPlayerId("testPlayerId");
        claimedItemSet.setItemSet(itemSet);
        entityManager.persist(claimedItemSet);

        ClaimedItemSet claimedItemSetOtherPlayer = new ClaimedItemSet();
        claimedItemSetOtherPlayer.setPlayerId("otherTestPlayerId");
        claimedItemSetOtherPlayer.setItemSet(itemSet);
        entityManager.persist(claimedItemSetOtherPlayer);

        entityManager.flush();

        // WHEN
        List<ClaimedItemSet> claimedItemSets = claimedItemSetRepository.findByPlayerId(claimedItemSet.getPlayerId());

        // THEN
        assertThat(claimedItemSets).isNotNull();
        assertThat(claimedItemSets).hasSize(1);
        assertThat(claimedItemSets.get(0)).isEqualTo(claimedItemSet);
    }
}
