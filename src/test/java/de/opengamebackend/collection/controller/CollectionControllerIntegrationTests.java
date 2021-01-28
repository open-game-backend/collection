package de.opengamebackend.collection.controller;

import de.opengamebackend.collection.model.requests.PutItemTagsRequest;
import de.opengamebackend.collection.model.responses.GetCollectionResponse;
import de.opengamebackend.collection.model.responses.GetItemDefinitionsResponse;
import de.opengamebackend.test.HttpRequestUtils;
import org.assertj.core.util.Lists;
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

    @Autowired
    public CollectionControllerIntegrationTests(MockMvc mvc, TestEntityManager entityManager) {
        this.mvc = mvc;
        this.entityManager = entityManager;

        this.httpRequestUtils = new HttpRequestUtils();
    }

    @Test
    public void whenGetCollection_thenOk() throws Exception {
        httpRequestUtils.assertGetOk(mvc, "/client/collection", GetCollectionResponse.class, "testId");
    }

    @Test
    public void whenGetItemDefinitions_thenOk() throws Exception {
        httpRequestUtils.assertGetOk(mvc, "/admin/itemdefinitions", GetItemDefinitionsResponse.class);
    }

    @Test
    public void whenPutItemTags_thenOk() throws Exception {
        PutItemTagsRequest request =
                new PutItemTagsRequest(Lists.list("Head", "Chest", "Feet", "Main Hand", "Epic", "A", "B"));
        httpRequestUtils.assertPutOk(mvc, "/admin/itemtags", request);
    }
}
