package de.opengamebackend.collection.controller;

import de.opengamebackend.collection.model.entities.*;
import de.opengamebackend.collection.model.repositories.ItemDefinitionRepository;
import de.opengamebackend.collection.model.requests.LoadoutRequest;
import de.opengamebackend.collection.model.requests.LoadoutRequestItem;
import de.opengamebackend.net.ApiErrors;
import de.opengamebackend.net.ApiException;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.*;

public class LoadoutMapperTests {
    private ItemDefinitionRepository itemDefinitionRepository;

    private LoadoutMapper loadoutMapper;

    @BeforeEach
    public void beforeEach() {
        itemDefinitionRepository = mock(ItemDefinitionRepository.class);

        loadoutMapper = new LoadoutMapper(itemDefinitionRepository);
    }

    @Test
    public void givenUnknownItemDefinition_whenPutLoadout_thenThrowException() {
        // GIVEN
        LoadoutType loadoutType = mock(LoadoutType.class);
        LoadoutRequestItem item = mock(LoadoutRequestItem.class);

        LoadoutRequest request = mock(LoadoutRequest.class);
        when(request.getType()).thenReturn("testLoadoutType");
        when(request.getItems()).thenReturn(Lists.list(item));

        Loadout loadout = mock(Loadout.class);

        // WHEN & THEN
        assertThatExceptionOfType(ApiException.class)
                .isThrownBy(() -> loadoutMapper.mapLoadout("testPlayer", loadoutType, request, loadout))
                .withMessage(ApiErrors.UNKNOWN_ITEM_DEFINITION_MESSAGE);
    }

    @Test
    public void givenLoadoutRequest_whenMapLoadout_thenMapsLoadout() throws ApiException {
        // GIVEN
        String playerId = "testPlayer";
        String itemId = "testItem";

        LoadoutType loadoutType = mock(LoadoutType.class);

        LoadoutRequestItem item = mock(LoadoutRequestItem.class);
        when(item.getId()).thenReturn(itemId);

        ItemDefinition itemDefinition = mock(ItemDefinition.class);
        when(itemDefinitionRepository.findById(itemId)).thenReturn(Optional.of(itemDefinition));

        LoadoutRequest request = mock(LoadoutRequest.class);
        when(request.getType()).thenReturn("testLoadoutType");
        when(request.getItems()).thenReturn(Lists.list(item));

        Loadout loadout = mock(Loadout.class);

        // WHEN
        loadoutMapper.mapLoadout(playerId, loadoutType, request, loadout);

        // THEN
        verify(loadout).setPlayerId(playerId);
        verify(loadout).setType(loadoutType);
    }

    @Test
    public void givenTooManyItemCopies_whenVerifyLoadout_thenThrowException() {
        // GIVEN
        int maxCopies = 2;

        ItemTag itemTag = mock(ItemTag.class);

        ItemDefinition itemDefinition = mock(ItemDefinition.class);
        when(itemDefinition.getItemTags()).thenReturn(Lists.list(itemTag));

        LoadoutItem loadoutItem = mock(LoadoutItem.class);
        when(loadoutItem.getItemDefinition()).thenReturn(itemDefinition);
        when(loadoutItem.getCount()).thenReturn(maxCopies + 1);

        LoadoutRule loadoutRule = mock(LoadoutRule.class);
        when(loadoutRule.getItemTag()).thenReturn(itemTag);
        when(loadoutRule.getMaxCopies()).thenReturn(maxCopies);

        LoadoutType loadoutType = mock(LoadoutType.class);
        when(loadoutType.getRules()).thenReturn(Lists.list(loadoutRule));

        Loadout loadout = mock(Loadout.class);
        when(loadout.getItems()).thenReturn(Lists.list(loadoutItem));
        when(loadout.getType()).thenReturn(loadoutType);

        // WHEN & THEN
        assertThatExceptionOfType(ApiException.class).isThrownBy(() -> loadoutMapper.verifyLoadout(loadout));
    }

    @Test
    public void givenTooFewTotalItems_whenVerifyLoadout_thenThrowException() {
        // GIVEN
        int minTotal = 2;

        ItemTag itemTag = mock(ItemTag.class);

        ItemDefinition itemDefinition = mock(ItemDefinition.class);
        when(itemDefinition.getItemTags()).thenReturn(Lists.list(itemTag));

        LoadoutItem loadoutItem = mock(LoadoutItem.class);
        when(loadoutItem.getItemDefinition()).thenReturn(itemDefinition);
        when(loadoutItem.getCount()).thenReturn(minTotal - 1);

        LoadoutRule loadoutRule = mock(LoadoutRule.class);
        when(loadoutRule.getItemTag()).thenReturn(itemTag);
        when(loadoutRule.getMaxCopies()).thenReturn(minTotal);
        when(loadoutRule.getMinTotal()).thenReturn(minTotal);

        LoadoutType loadoutType = mock(LoadoutType.class);
        when(loadoutType.getRules()).thenReturn(Lists.list(loadoutRule));

        Loadout loadout = mock(Loadout.class);
        when(loadout.getItems()).thenReturn(Lists.list(loadoutItem));
        when(loadout.getType()).thenReturn(loadoutType);

        // WHEN & THEN
        assertThatExceptionOfType(ApiException.class).isThrownBy(() -> loadoutMapper.verifyLoadout(loadout));
    }

    @Test
    public void givenTooManyTotalItems_whenVerifyLoadout_thenThrowException() {
        // GIVEN
        int maxTotal = 2;

        ItemTag itemTag = mock(ItemTag.class);

        ItemDefinition itemDefinition = mock(ItemDefinition.class);
        when(itemDefinition.getItemTags()).thenReturn(Lists.list(itemTag));

        LoadoutItem loadoutItem = mock(LoadoutItem.class);
        when(loadoutItem.getItemDefinition()).thenReturn(itemDefinition);
        when(loadoutItem.getCount()).thenReturn(maxTotal + 1);

        LoadoutRule loadoutRule = mock(LoadoutRule.class);
        when(loadoutRule.getItemTag()).thenReturn(itemTag);
        when(loadoutRule.getMaxCopies()).thenReturn(maxTotal + 1);
        when(loadoutRule.getMaxTotal()).thenReturn(maxTotal);

        LoadoutType loadoutType = mock(LoadoutType.class);
        when(loadoutType.getRules()).thenReturn(Lists.list(loadoutRule));

        Loadout loadout = mock(Loadout.class);
        when(loadout.getItems()).thenReturn(Lists.list(loadoutItem));
        when(loadout.getType()).thenReturn(loadoutType);

        // WHEN & THEN
        assertThatExceptionOfType(ApiException.class).isThrownBy(() -> loadoutMapper.verifyLoadout(loadout));
    }

    @Test
    public void givenValidLoadout_whenVerifyLoadout_thenDontThrowException() throws ApiException {
        // GIVEN
        int itemCount = 2;

        ItemTag itemTag = mock(ItemTag.class);

        ItemDefinition itemDefinition = mock(ItemDefinition.class);
        when(itemDefinition.getItemTags()).thenReturn(Lists.list(itemTag));

        ItemDefinition otherItemDefinition = mock(ItemDefinition.class);
        when(otherItemDefinition.getItemTags()).thenReturn(Lists.emptyList());

        LoadoutItem loadoutItemMatchingRule = mock(LoadoutItem.class);
        when(loadoutItemMatchingRule.getItemDefinition()).thenReturn(itemDefinition);
        when(loadoutItemMatchingRule.getCount()).thenReturn(itemCount);

        LoadoutItem loadoutItemMatchingNoRule = mock(LoadoutItem.class);
        when(loadoutItemMatchingNoRule.getItemDefinition()).thenReturn(otherItemDefinition);

        LoadoutRule loadoutRule = mock(LoadoutRule.class);
        when(loadoutRule.getItemTag()).thenReturn(itemTag);
        when(loadoutRule.getMaxCopies()).thenReturn(itemCount + 1);
        when(loadoutRule.getMaxTotal()).thenReturn(itemCount + 1);

        LoadoutType loadoutType = mock(LoadoutType.class);
        when(loadoutType.getRules()).thenReturn(Lists.list(loadoutRule));

        Loadout loadout = mock(Loadout.class);
        when(loadout.getItems()).thenReturn(Lists.list(loadoutItemMatchingRule, loadoutItemMatchingNoRule));
        when(loadout.getType()).thenReturn(loadoutType);

        // WHEN & THEN
        loadoutMapper.verifyLoadout(loadout);
    }
}
