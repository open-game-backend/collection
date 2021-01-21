package de.opengamebackend.collection.controller;

import de.opengamebackend.collection.model.responses.GetCollectionResponse;
import de.opengamebackend.test.HttpRequestUtils;
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
        httpRequestUtils.assertGetOk(mvc, "/collection", GetCollectionResponse.class, "testId");
    }
}
