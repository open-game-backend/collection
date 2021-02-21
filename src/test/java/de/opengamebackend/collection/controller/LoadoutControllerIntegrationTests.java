package de.opengamebackend.collection.controller;

import de.opengamebackend.collection.model.entities.Loadout;
import de.opengamebackend.collection.model.entities.LoadoutType;
import de.opengamebackend.collection.model.requests.LoadoutRequest;
import de.opengamebackend.collection.model.requests.PutLoadoutTypesRequest;
import de.opengamebackend.collection.model.responses.AddLoadoutResponse;
import de.opengamebackend.collection.model.responses.GetLoadoutTypesResponse;
import de.opengamebackend.collection.model.responses.GetLoadoutsResponse;
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
public class LoadoutControllerIntegrationTests {
    private MockMvc mvc;
    private TestEntityManager entityManager;
    private HttpRequestUtils httpRequestUtils;

    private LoadoutType loadoutType;

    @Autowired
    public LoadoutControllerIntegrationTests(MockMvc mvc, TestEntityManager entityManager) {
        this.mvc = mvc;
        this.entityManager = entityManager;

        this.httpRequestUtils = new HttpRequestUtils();
    }

    @BeforeEach
    public void beforeEach() {
        this.loadoutType = new LoadoutType();
        this.loadoutType.setId("testLoadoutType");
        entityManager.persist(this.loadoutType);

        entityManager.flush();
    }

    @Test
    public void whenPostLoadout_thenOk() throws Exception {
        LoadoutRequest request = new LoadoutRequest();
        request.setType(loadoutType.getId());

        httpRequestUtils.assertPostOk(mvc, "/client/loadouts", request, AddLoadoutResponse.class,"testPlayer");
    }

    @Test
    public void whenGetLoadouts_thenOk() throws Exception {
        httpRequestUtils.assertGetOk(mvc, "/client/loadouts", GetLoadoutsResponse.class, "testPlayer");
    }

    @Test
    public void whenPutLoadout_thenOk() throws Exception {
        String playerId = "testPlayer";

        Loadout loadout = new Loadout();
        loadout.setPlayerId(playerId);
        loadout.setType(loadoutType);
        entityManager.persistAndFlush(loadout);

        LoadoutRequest request = new LoadoutRequest();
        request.setType(loadoutType.getId());

        httpRequestUtils.assertPutOk(mvc, "/client/loadouts/" + loadout.getId(), request, playerId);
    }

    @Test
    public void whenDeleteLoadout_thenOk() throws Exception {
        String playerId = "testPlayer";

        Loadout loadout = new Loadout();
        loadout.setPlayerId(playerId);
        loadout.setType(loadoutType);
        entityManager.persistAndFlush(loadout);

        httpRequestUtils.assertDeleteOk(mvc, "/client/loadouts/" + loadout.getId(), playerId);
    }

    @Test
    public void whenGetLoadoutTypes_thenOk() throws Exception {
        httpRequestUtils.assertGetOk(mvc, "/admin/loadouttypes", GetLoadoutTypesResponse.class);
    }

    @Test
    public void whenPutLoadoutTypes_thenOk() throws Exception {
        PutLoadoutTypesRequest request = new PutLoadoutTypesRequest();
        httpRequestUtils.assertPutOk(mvc, "/admin/loadouttypes", request);
    }
}
