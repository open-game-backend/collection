package de.opengamebackend.collection.controller;

import de.opengamebackend.collection.model.entities.ItemDefinition;
import de.opengamebackend.collection.model.entities.Loadout;
import de.opengamebackend.collection.model.entities.LoadoutItem;
import de.opengamebackend.collection.model.entities.LoadoutType;
import de.opengamebackend.collection.model.repositories.*;
import de.opengamebackend.collection.model.requests.LoadoutRequest;
import de.opengamebackend.collection.model.requests.LoadoutRequestItem;
import de.opengamebackend.collection.model.responses.AddLoadoutResponse;
import de.opengamebackend.collection.model.responses.GetLoadoutsResponse;
import de.opengamebackend.net.ApiErrors;
import de.opengamebackend.net.ApiException;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.*;

public class LoadoutServiceTests {
    private LoadoutRepository loadoutRepository;
    private LoadoutTypeRepository loadoutTypeRepository;
    private ItemDefinitionRepository itemDefinitionRepository;

    private LoadoutService loadoutService;

    @BeforeEach
    public void beforeEach() {
        loadoutRepository = mock(LoadoutRepository.class);
        loadoutTypeRepository = mock(LoadoutTypeRepository.class);
        itemDefinitionRepository = mock(ItemDefinitionRepository.class);

        loadoutService = new LoadoutService(loadoutRepository, loadoutTypeRepository, itemDefinitionRepository);
    }

    @Test
    public void givenMissingPlayerId_whenAddLoadout_thenThrowException() {
        // WHEN & THEN
        assertThatExceptionOfType(ApiException.class)
                .isThrownBy(() -> loadoutService.addLoadout("", null))
                .withMessage(ApiErrors.MISSING_PLAYER_ID_MESSAGE);
    }

    @Test
    public void givenUnknownLoadoutType_whenAddLoadout_thenThrowException() {
        // GIVEN
        LoadoutRequest request = mock(LoadoutRequest.class);

        // WHEN & THEN
        assertThatExceptionOfType(ApiException.class)
                .isThrownBy(() -> loadoutService.addLoadout("testPlayer", request))
                .withMessage(ApiErrors.UNKNOWN_LOADOUT_TYPE_MESSAGE);
    }

    @Test
    public void givenUnknownItemDefinition_whenAddLoadout_thenThrowException() {
        // GIVEN
        String loadoutTypeId = "testLoadoutType";

        LoadoutType loadoutType = mock(LoadoutType.class);
        when(loadoutTypeRepository.findById(loadoutTypeId)).thenReturn(Optional.of(loadoutType));

        LoadoutRequestItem item = mock(LoadoutRequestItem.class);

        LoadoutRequest request = mock(LoadoutRequest.class);
        when(request.getType()).thenReturn(loadoutTypeId);
        when(request.getItems()).thenReturn(Lists.list(item));

        // WHEN & THEN
        assertThatExceptionOfType(ApiException.class)
                .isThrownBy(() -> loadoutService.addLoadout("testPlayer", request))
                .withMessage(ApiErrors.UNKNOWN_ITEM_DEFINITION_MESSAGE);
    }

    @Test
    public void givenValidLoadout_whenAddLoadout_thenSaveLoadout() throws ApiException {
        // GIVEN
        String playerId = "testPlayer";
        String loadoutTypeId = "testLoadoutType";
        String itemDefinitionId = "testItem";

        LoadoutType loadoutType = mock(LoadoutType.class);
        when(loadoutTypeRepository.findById(loadoutTypeId)).thenReturn(Optional.of(loadoutType));

        ItemDefinition itemDefinition = mock(ItemDefinition.class);
        when(itemDefinitionRepository.findById(itemDefinitionId)).thenReturn(Optional.of(itemDefinition));

        LoadoutRequestItem item = mock(LoadoutRequestItem.class);
        when(item.getId()).thenReturn(itemDefinitionId);
        when(item.getCount()).thenReturn(2);

        LoadoutRequest request = mock(LoadoutRequest.class);
        when(request.getType()).thenReturn(loadoutTypeId);
        when(request.getItems()).thenReturn(Lists.list(item));

        // WHEN
        AddLoadoutResponse response = loadoutService.addLoadout(playerId, request);

        // THEN
        ArgumentCaptor<Loadout> argumentCaptor = ArgumentCaptor.forClass(Loadout.class);
        verify(loadoutRepository).save(argumentCaptor.capture());

        Loadout loadout = argumentCaptor.getValue();

        assertThat(loadout).isNotNull();
        assertThat(loadout.getPlayerId()).isEqualTo(playerId);
        assertThat(loadout.getType()).isEqualTo(loadoutType);
        assertThat(loadout.getItems()).isNotNull();
        assertThat(loadout.getItems()).hasSize(1);
        assertThat(loadout.getItems().get(0).getLoadout()).isEqualTo(loadout);
        assertThat(loadout.getItems().get(0).getItemDefinition()).isEqualTo(itemDefinition);
        assertThat(loadout.getItems().get(0).getCount()).isEqualTo(item.getCount());

        assertThat(response.getLoadoutId()).isEqualTo(loadout.getId());
    }

    @Test
    public void givenMissingPlayerId_whenGetLoadouts_thenThrowException() {
        // WHEN & THEN
        assertThatExceptionOfType(ApiException.class)
                .isThrownBy(() -> loadoutService.getLoadouts(""))
                .withMessage(ApiErrors.MISSING_PLAYER_ID_MESSAGE);
    }

    @Test
    public void givenValidPlayerId_whenGetLoadouts_thenReturnLoadouts() throws ApiException {
        // GIVEN
        String playerId = "testPlayer";

        ItemDefinition itemDefinition = mock(ItemDefinition.class);
        when(itemDefinition.getId()).thenReturn("testItem");

        LoadoutItem loadoutItem = mock(LoadoutItem.class);
        when(loadoutItem.getItemDefinition()).thenReturn(itemDefinition);
        when(loadoutItem.getCount()).thenReturn(2);

        LoadoutType loadoutType = mock(LoadoutType.class);
        when(loadoutType.getId()).thenReturn("testLoadoutType");

        Loadout loadout = mock(Loadout.class);
        when(loadout.getId()).thenReturn(1L);
        when(loadout.getType()).thenReturn(loadoutType);
        when(loadout.getItems()).thenReturn(Lists.list(loadoutItem));

        when(loadoutRepository.findByPlayerId(playerId)).thenReturn(Lists.list(loadout));

        // WHEN
        GetLoadoutsResponse response = loadoutService.getLoadouts(playerId);

        // THEN
        assertThat(response).isNotNull();
        assertThat(response.getLoadouts()).isNotNull();
        assertThat(response.getLoadouts()).hasSize(1);
        assertThat(response.getLoadouts().get(0).getId()).isEqualTo(loadout.getId());
        assertThat(response.getLoadouts().get(0).getType()).isEqualTo(loadoutType.getId());
        assertThat(response.getLoadouts().get(0).getItems()).isNotNull();
        assertThat(response.getLoadouts().get(0).getItems()).hasSize(1);
        assertThat(response.getLoadouts().get(0).getItems().get(0).getItemDefinitionId()).isEqualTo(loadoutItem.getItemDefinition().getId());
        assertThat(response.getLoadouts().get(0).getItems().get(0).getCount()).isEqualTo(loadoutItem.getCount());
    }

    @Test
    public void givenMissingPlayerId_whenPutLoadout_thenThrowException() {
        // WHEN & THEN
        assertThatExceptionOfType(ApiException.class)
                .isThrownBy(() -> loadoutService.putLoadout("", 0L,null))
                .withMessage(ApiErrors.MISSING_PLAYER_ID_MESSAGE);
    }

    @Test
    public void givenUnknownLoadout_whenPutLoadout_thenThrowException() {
        // WHEN & THEN
        assertThatExceptionOfType(ApiException.class)
                .isThrownBy(() -> loadoutService.putLoadout("testPlayer", 0L, null))
                .withMessage(ApiErrors.UNKNOWN_LOADOUT_MESSAGE);
    }

    @Test
    public void givenUnknownLoadoutType_whenPutLoadout_thenThrowException() {
        // GIVEN
        long loadoutId = 1L;

        Loadout loadout = mock(Loadout.class);
        when(loadoutRepository.findById(loadoutId)).thenReturn(Optional.of(loadout));

        LoadoutRequest request = mock(LoadoutRequest.class);

        // WHEN & THEN
        assertThatExceptionOfType(ApiException.class)
                .isThrownBy(() -> loadoutService.putLoadout("testPlayer", loadoutId, request))
                .withMessage(ApiErrors.UNKNOWN_LOADOUT_TYPE_MESSAGE);
    }

    @Test
    public void givenUnknownItemDefinition_whenPutLoadout_thenThrowException() {
        // GIVEN
        long loadoutId = 1L;
        String loadoutTypeId = "testLoadoutType";

        Loadout loadout = mock(Loadout.class);
        when(loadoutRepository.findById(loadoutId)).thenReturn(Optional.of(loadout));

        LoadoutType loadoutType = mock(LoadoutType.class);
        when(loadoutTypeRepository.findById(loadoutTypeId)).thenReturn(Optional.of(loadoutType));

        LoadoutRequestItem item = mock(LoadoutRequestItem.class);

        LoadoutRequest request = mock(LoadoutRequest.class);
        when(request.getType()).thenReturn(loadoutTypeId);
        when(request.getItems()).thenReturn(Lists.list(item));

        // WHEN & THEN
        assertThatExceptionOfType(ApiException.class)
                .isThrownBy(() -> loadoutService.putLoadout("testPlayer", loadoutId, request))
                .withMessage(ApiErrors.UNKNOWN_ITEM_DEFINITION_MESSAGE);
    }

    @Test
    public void givenValidLoadout_whenPutLoadout_thenSaveLoadout() throws ApiException {
        // GIVEN
        String playerId = "testPlayer";
        long loadoutId = 1L;
        String loadoutTypeId = "testLoadoutType";
        String itemDefinitionId = "testItem";

        Loadout loadout = mock(Loadout.class);
        when(loadoutRepository.findById(loadoutId)).thenReturn(Optional.of(loadout));

        LoadoutType loadoutType = mock(LoadoutType.class);
        when(loadoutTypeRepository.findById(loadoutTypeId)).thenReturn(Optional.of(loadoutType));

        ItemDefinition itemDefinition = mock(ItemDefinition.class);
        when(itemDefinitionRepository.findById(itemDefinitionId)).thenReturn(Optional.of(itemDefinition));

        LoadoutRequestItem item = mock(LoadoutRequestItem.class);
        when(item.getId()).thenReturn(itemDefinitionId);
        when(item.getCount()).thenReturn(2);

        LoadoutRequest request = mock(LoadoutRequest.class);
        when(request.getType()).thenReturn(loadoutTypeId);
        when(request.getItems()).thenReturn(Lists.list(item));

        // WHEN
        loadoutService.putLoadout(playerId, loadoutId, request);

        // THEN
        verify(loadout).setPlayerId(playerId);
        verify(loadout).setType(loadoutType);

        verify(loadoutRepository).save(loadout);
    }

    @Test
    public void givenMissingPlayerId_whenDeleteLoadout_thenThrowException() {
        // WHEN & THEN
        assertThatExceptionOfType(ApiException.class)
                .isThrownBy(() -> loadoutService.deleteLoadout("", 0L))
                .withMessage(ApiErrors.MISSING_PLAYER_ID_MESSAGE);
    }

    @Test
    public void givenUnknownLoadout_whenDeleteLoadout_thenThrowException() {
        // WHEN & THEN
        assertThatExceptionOfType(ApiException.class)
                .isThrownBy(() -> loadoutService.deleteLoadout("testPlayer", 0L))
                .withMessage(ApiErrors.UNKNOWN_LOADOUT_MESSAGE);
    }

    @Test
    public void givenValidLoadout_whenDeleteLoadout_thenDeletesLoadout() throws ApiException {
        // GIVEN
        String playerId = "testPlayer";
        long loadoutId = 1L;

        Loadout loadout = mock(Loadout.class);
        when(loadoutRepository.findById(loadoutId)).thenReturn(Optional.of(loadout));

        // WHEN
        loadoutService.deleteLoadout(playerId, loadoutId);

        // THEN
        verify(loadoutRepository).delete(loadout);
    }
}
