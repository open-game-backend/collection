package de.opengamebackend.collection.model.repositories;

import de.opengamebackend.collection.model.entities.CollectionItem;
import de.opengamebackend.collection.model.entities.ItemDefinition;
import de.opengamebackend.collection.model.entities.ItemTag;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
public class CollectionItemRepositoryTests {
    private TestEntityManager entityManager;
    private CollectionItemRepository collectionItemRepository;

    @Autowired
    public CollectionItemRepositoryTests(TestEntityManager entityManager, CollectionItemRepository collectionItemRepository) {
        this.entityManager = entityManager;
        this.collectionItemRepository = collectionItemRepository;
    }

    @Test
    public void givenItems_whenFindByPlayerId_thenReturnItems() {
        // GIVEN
        ItemTag itemTag = new ItemTag("testType");
        entityManager.persist(itemTag);

        ItemDefinition itemDefinition1 = new ItemDefinition();
        itemDefinition1.setId("testDefinition1");
        itemDefinition1.setItemTags(Lists.list(itemTag));
        entityManager.persist(itemDefinition1);

        ItemDefinition itemDefinition2 = new ItemDefinition();
        itemDefinition2.setId("testDefinition2");
        itemDefinition2.setItemTags(Lists.list(itemTag));
        entityManager.persist(itemDefinition2);

        String playerId = "testPlayerId";

        CollectionItem item1 = new CollectionItem();
        item1.setPlayerId(playerId);
        item1.setItemDefinition(itemDefinition1);
        item1.setCount(2);
        entityManager.persist(item1);

        CollectionItem item2 = new CollectionItem();
        item2.setPlayerId(playerId);
        item2.setItemDefinition(itemDefinition2);
        item2.setCount(3);
        entityManager.persist(item2);

        entityManager.flush();

        // WHEN
        List<CollectionItem> items = collectionItemRepository.findByPlayerId(playerId);

        // THEN
        assertThat(items).isNotNull();
        assertThat(items).hasSize(2);
        assertThat(items.get(0)).isEqualTo(item1);
        assertThat(items.get(1)).isEqualTo(item2);
    }
}
