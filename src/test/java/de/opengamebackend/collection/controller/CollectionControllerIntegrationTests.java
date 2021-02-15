package de.opengamebackend.collection.controller;

import de.opengamebackend.collection.model.entities.CollectionItem;
import de.opengamebackend.collection.model.entities.ItemDefinition;
import de.opengamebackend.collection.model.requests.AddCollectionItemsRequest;
import de.opengamebackend.collection.model.requests.PutCollectionItemsRequest;
import de.opengamebackend.collection.model.requests.PutItemDefinitionsRequest;
import de.opengamebackend.collection.model.requests.PutItemSetsRequest;
import de.opengamebackend.collection.model.responses.ClaimItemSetResponse;
import de.opengamebackend.collection.model.responses.GetCollectionResponse;
import de.opengamebackend.collection.model.responses.GetItemDefinitionsResponse;
import de.opengamebackend.collection.model.responses.GetItemSetsResponse;
import de.opengamebackend.test.HttpRequestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestEntityManager;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import javax.transaction.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestEntityManager
@Transactional
public class CollectionControllerIntegrationTests {
    private MockMvc mvc;
    private TestEntityManager entityManager;
    private HttpRequestUtils httpRequestUtils;

    private ItemDefinition itemDefinition;

    @Autowired
    public CollectionControllerIntegrationTests(MockMvc mvc, TestEntityManager entityManager) {
        this.mvc = mvc;
        this.entityManager = entityManager;

        this.httpRequestUtils = new HttpRequestUtils();
    }

    @BeforeEach
    public void beforeEach() {
        this.itemDefinition = new ItemDefinition();
        this.itemDefinition.setId("testItemDefinition");
        entityManager.persist(this.itemDefinition);

        entityManager.flush();
    }

    @Test
    public void whenGetCollection_thenOk() throws Exception {
        httpRequestUtils.assertGetOk(mvc, "/client/collection", GetCollectionResponse.class, "testId");
    }

    @Test
    public void whenGetCollectionAdmin_thenOk() throws Exception {
        httpRequestUtils.assertGetOk(mvc, "/admin/collection/testPlayer", GetCollectionResponse.class);
    }

    @Test
    public void whenPostCollectionItem_thenOk() throws Exception {
        AddCollectionItemsRequest request = new AddCollectionItemsRequest();
        request.setItemDefinitionId(itemDefinition.getId());
        request.setItemCount(2);

        httpRequestUtils.assertPostOk(mvc, "/admin/collection/testPlayerId/items", request);
    }

    @Test
    public void whenPutCollectionItem_thenOk() throws Exception {
        String playerId = "testPlayerId";

        CollectionItem collectionItem = new CollectionItem();
        collectionItem.setPlayerId(playerId);
        collectionItem.setItemDefinition(itemDefinition);
        collectionItem.setCount(1);
        entityManager.persistAndFlush(collectionItem);

        PutCollectionItemsRequest request = new PutCollectionItemsRequest();
        request.setItemCount(2);

        httpRequestUtils.assertPutOk(mvc, "/admin/collection/" + playerId + "/items/" + itemDefinition.getId(), request);
    }

    @Test
    public void whenDeleteCollectionItem_thenOk() throws Exception {
        String playerId = "testPlayerId";

        CollectionItem collectionItem = new CollectionItem();
        collectionItem.setPlayerId(playerId);
        collectionItem.setItemDefinition(itemDefinition);
        collectionItem.setCount(1);
        entityManager.persistAndFlush(collectionItem);

        httpRequestUtils.assertDeleteOk(mvc, "/admin/collection/" + playerId + "/items/" + itemDefinition.getId());
    }

    @Test
    public void whenGetItemDefinitions_thenOk() throws Exception {
        httpRequestUtils.assertGetOk(mvc, "/admin/itemdefinitions", GetItemDefinitionsResponse.class);
    }

    @Test
    public void whenPutItemDefinitions_thenOk() throws Exception {
        PutItemDefinitionsRequest request = new PutItemDefinitionsRequest();
        httpRequestUtils.assertPutOk(mvc, "/admin/itemdefinitions", request);
    }

    @Test
    public void whenGetItemSets_thenOk() throws Exception {
        httpRequestUtils.assertGetOk(mvc, "/admin/itemsets", GetItemSetsResponse.class);
    }

    @Test
    public void whenPutItemSets_thenOk() throws Exception {
        PutItemSetsRequest request = new PutItemSetsRequest();
        httpRequestUtils.assertPutOk(mvc, "/admin/itemsets", request);
    }

    @Test
    public void whenClaimItemSet_thenOk() throws Exception {
        httpRequestUtils.assertPostOk(mvc, "/client/claimitemset", null, ClaimItemSetResponse.class,
                "testId");
    }
}
