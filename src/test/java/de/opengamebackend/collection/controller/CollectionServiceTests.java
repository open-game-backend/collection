package de.opengamebackend.collection.controller;

import de.opengamebackend.collection.model.entities.*;
import de.opengamebackend.collection.model.repositories.*;
import de.opengamebackend.collection.model.requests.AddCollectionItemsRequest;
import de.opengamebackend.collection.model.requests.PutCollectionItemsRequest;
import de.opengamebackend.collection.model.requests.PutItemDefinitionsRequest;
import de.opengamebackend.collection.model.requests.PutItemDefinitionsRequestItem;
import de.opengamebackend.collection.model.responses.ClaimItemSetResponse;
import de.opengamebackend.collection.model.responses.GetCollectionResponse;
import de.opengamebackend.collection.model.responses.GetItemDefinitionsResponse;
import de.opengamebackend.net.ApiErrors;
import de.opengamebackend.net.ApiException;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.*;

public class CollectionServiceTests {
    private CollectionItemRepository collectionItemRepository;
    private ItemDefinitionRepository itemDefinitionRepository;
    private ItemTagRepository itemTagRepository;
    private ItemSetRepository itemSetRepository;
    private ClaimedItemSetRepository claimedItemSetRepository;

    private CollectionService collectionService;

    @BeforeEach
    public void beforeEach() {
        collectionItemRepository = mock(CollectionItemRepository.class);
        itemDefinitionRepository = mock(ItemDefinitionRepository.class);
        itemTagRepository = mock(ItemTagRepository.class);
        itemSetRepository = mock(ItemSetRepository.class);
        claimedItemSetRepository = mock(ClaimedItemSetRepository.class);

        collectionService = new CollectionService(collectionItemRepository, itemDefinitionRepository, itemTagRepository,
                itemSetRepository, claimedItemSetRepository);
    }

    @Test
    public void givenMissingPlayerId_whenGetCollection_thenThrowException() {
        // WHEN & THEN
        assertThatExceptionOfType(ApiException.class)
                .isThrownBy(() -> collectionService.getCollection(""))
                .withMessage(ApiErrors.MISSING_PLAYER_ID_MESSAGE);
    }

    @Test
    public void givenItems_whenGetCollection_thenReturnItems() throws ApiException {
        // GIVEN
        String playerId = "testPlayerId";

        ItemTag itemTag = mock(ItemTag.class);
        when(itemTag.getTag()).thenReturn("testItemTag");

        ItemDefinition itemDefinition1 = mock(ItemDefinition.class);
        when(itemDefinition1.getId()).thenReturn("testItemDefinition1");
        when(itemDefinition1.getItemTags()).thenReturn(Lists.list(itemTag));

        ItemDefinition itemDefinition2 = mock(ItemDefinition.class);
        when(itemDefinition2.getId()).thenReturn("testItemDefinition2");
        when(itemDefinition2.getItemTags()).thenReturn(Lists.list(itemTag));

        CollectionItem item1 = mock(CollectionItem.class);
        when(item1.getItemDefinition()).thenReturn(itemDefinition1);
        when(item1.getCount()).thenReturn(2);

        CollectionItem item2 = mock(CollectionItem.class);
        when(item2.getItemDefinition()).thenReturn(itemDefinition2);
        when(item2.getCount()).thenReturn(3);

        when(collectionItemRepository.findByPlayerId(playerId)).thenReturn(Lists.list(item1, item2));

        // WHEN
        GetCollectionResponse response = collectionService.getCollection(playerId);

        // THEN
        assertThat(response).isNotNull();
        assertThat(response.getCollection()).isNotNull();
        assertThat(response.getCollection()).hasSize(2);
        assertThat(response.getCollection().get(0).getId()).isEqualTo(itemDefinition1.getId());
        assertThat(response.getCollection().get(0).getTags()).isNotNull();
        assertThat(response.getCollection().get(0).getTags()).hasSize(1);
        assertThat(response.getCollection().get(0).getTags().get(0)).isEqualTo(itemTag.getTag());
        assertThat(response.getCollection().get(0).getCount()).isEqualTo(item1.getCount());
        assertThat(response.getCollection().get(1).getId()).isEqualTo(itemDefinition2.getId());
        assertThat(response.getCollection().get(1).getTags()).isNotNull();
        assertThat(response.getCollection().get(1).getTags()).hasSize(1);
        assertThat(response.getCollection().get(1).getTags().get(0)).isEqualTo(itemTag.getTag());
        assertThat(response.getCollection().get(1).getCount()).isEqualTo(item2.getCount());
    }

    @Test
    public void givenMissingPlayerId_whenAddCollectionItems_thenThrowException() {
        // WHEN & THEN
        assertThatExceptionOfType(ApiException.class)
                .isThrownBy(() -> collectionService.addCollectionItems(null, null))
                .withMessage(ApiErrors.MISSING_PLAYER_ID_MESSAGE);
    }

    @Test
    public void givenMissingItemDefinitionId_whenAddCollectionItems_thenThrowException() {
        // GIVEN
        AddCollectionItemsRequest request = new AddCollectionItemsRequest();

        // WHEN & THEN
        assertThatExceptionOfType(ApiException.class)
                .isThrownBy(() -> collectionService.addCollectionItems("testPlayer", request))
                .withMessage(ApiErrors.MISSING_ITEM_DEFINITION_MESSAGE);
    }

    @Test
    public void givenUnknownItemDefinition_whenAddCollectionItems_thenThrowException() {
        // GIVEN
        AddCollectionItemsRequest request = new AddCollectionItemsRequest();
        request.setItemDefinitionId("testItemDefinition");

        // WHEN & THEN
        assertThatExceptionOfType(ApiException.class)
                .isThrownBy(() -> collectionService.addCollectionItems("testPlayer", request))
                .withMessage(ApiErrors.UNKNOWN_ITEM_DEFINITION_MESSAGE);
    }

    @Test
    public void givenInvalidItemCount_whenAddCollectionItems_thenThrowException() {
        // GIVEN
        String itemDefinitionId = "testItemDefinition";

        ItemDefinition itemDefinition = mock(ItemDefinition.class);
        when(itemDefinitionRepository.findById(itemDefinitionId)).thenReturn(Optional.of(itemDefinition));

        AddCollectionItemsRequest request = new AddCollectionItemsRequest();
        request.setItemDefinitionId(itemDefinitionId);

        // WHEN & THEN
        assertThatExceptionOfType(ApiException.class)
                .isThrownBy(() -> collectionService.addCollectionItems("testPlayer", request))
                .withMessage(ApiErrors.INVALID_ITEM_COUNT_MESSAGE);
    }

    @Test
    public void givenCollectionItems_whenAddCollectionItems_thenSaveItems() throws ApiException {
        // GIVEN
        String itemDefinitionId = "testItemDefinition";

        ItemDefinition itemDefinition = mock(ItemDefinition.class);
        when(itemDefinitionRepository.findById(itemDefinitionId)).thenReturn(Optional.of(itemDefinition));

        AddCollectionItemsRequest request = new AddCollectionItemsRequest();
        request.setItemDefinitionId(itemDefinitionId);
        request.setItemCount(2);

        String playerId = "testPlayer";

        // WHEN
        collectionService.addCollectionItems(playerId, request);

        // THEN
        ArgumentCaptor<CollectionItem> argumentCaptor = ArgumentCaptor.forClass(CollectionItem.class);
        verify(collectionItemRepository).save(argumentCaptor.capture());

        CollectionItem savedItem = argumentCaptor.getValue();

        assertThat(savedItem).isNotNull();
        assertThat(savedItem.getPlayerId()).isEqualTo(playerId);
        assertThat(savedItem.getItemDefinition()).isEqualTo(itemDefinition);
        assertThat(savedItem.getCount()).isEqualTo(request.getItemCount());
    }

    @Test
    public void givenCollectionItems_whenAddCollectionItems_thenIncreasesCount() throws ApiException {
        // GIVEN
        String playerId = "testPlayer";
        String itemDefinitionId = "testItemDefinition";
        int oldCount = 2;

        ItemDefinition itemDefinition = mock(ItemDefinition.class);
        when(itemDefinitionRepository.findById(itemDefinitionId)).thenReturn(Optional.of(itemDefinition));

        CollectionItem collectionItem = mock(CollectionItem.class);
        when(collectionItem.getCount()).thenReturn(oldCount);
        when(collectionItemRepository.findByPlayerIdAndItemDefinition(playerId, itemDefinition)).thenReturn(Optional.of(collectionItem));

        AddCollectionItemsRequest request = new AddCollectionItemsRequest();
        request.setItemDefinitionId(itemDefinitionId);
        request.setItemCount(3);

        // WHEN
        collectionService.addCollectionItems(playerId, request);

        // THEN
        verify(collectionItem).setCount(oldCount + request.getItemCount());
        verify(collectionItemRepository).save(collectionItem);
    }

    @Test
    public void givenMissingPlayerId_whenPutCollectionItems_thenThrowException() {
        // WHEN & THEN
        assertThatExceptionOfType(ApiException.class)
                .isThrownBy(() -> collectionService.putCollectionItems(null, null, null))
                .withMessage(ApiErrors.MISSING_PLAYER_ID_MESSAGE);
    }

    @Test
    public void givenMissingItemDefinitionId_whenPutCollectionItems_thenThrowException() {
        // WHEN & THEN
        assertThatExceptionOfType(ApiException.class)
                .isThrownBy(() -> collectionService.putCollectionItems("testPlayer", null, null))
                .withMessage(ApiErrors.MISSING_ITEM_DEFINITION_MESSAGE);
    }

    @Test
    public void givenUnknownItemDefinition_whenPutCollectionItems_thenThrowException() {
        // GIVEN
        PutCollectionItemsRequest request = new PutCollectionItemsRequest();

        // WHEN & THEN
        assertThatExceptionOfType(ApiException.class)
                .isThrownBy(() -> collectionService.putCollectionItems("testPlayer", "testItemDefinition", request))
                .withMessage(ApiErrors.UNKNOWN_ITEM_DEFINITION_MESSAGE);
    }

    @Test
    public void givenInvalidItemCount_whenPutCollectionItems_thenThrowException() {
        // GIVEN
        String itemDefinitionId = "testItemDefinition";

        ItemDefinition itemDefinition = mock(ItemDefinition.class);
        when(itemDefinitionRepository.findById(itemDefinitionId)).thenReturn(Optional.of(itemDefinition));

        PutCollectionItemsRequest request = new PutCollectionItemsRequest();

        // WHEN & THEN
        assertThatExceptionOfType(ApiException.class)
                .isThrownBy(() -> collectionService.putCollectionItems("testPlayer", itemDefinitionId, request))
                .withMessage(ApiErrors.INVALID_ITEM_COUNT_MESSAGE);
    }

    @Test
    public void givenInvalidItem_whenPutCollectionItems_thenThrowException() {
        // GIVEN
        String itemDefinitionId = "testItemDefinition";

        ItemDefinition itemDefinition = mock(ItemDefinition.class);
        when(itemDefinitionRepository.findById(itemDefinitionId)).thenReturn(Optional.of(itemDefinition));

        PutCollectionItemsRequest request = new PutCollectionItemsRequest();
        request.setItemCount(2);

        // WHEN & THEN
        assertThatExceptionOfType(ApiException.class)
                .isThrownBy(() -> collectionService.putCollectionItems("testPlayer", itemDefinitionId, request))
                .withMessage(ApiErrors.PLAYER_DOES_NOT_OWN_ITEM_MESSAGE);
    }

    @Test
    public void givenCollectionItems_whenPutCollectionItems_thenSaveItems() throws ApiException {
        // GIVEN
        String playerId = "testPlayer";
        String itemDefinitionId = "testItemDefinition";

        ItemDefinition itemDefinition = mock(ItemDefinition.class);
        when(itemDefinitionRepository.findById(itemDefinitionId)).thenReturn(Optional.of(itemDefinition));

        CollectionItem collectionItem = mock(CollectionItem.class);
        when(collectionItemRepository.findByPlayerIdAndItemDefinition(playerId, itemDefinition))
                .thenReturn(Optional.of(collectionItem));

        PutCollectionItemsRequest request = new PutCollectionItemsRequest();
        request.setItemCount(2);

        // WHEN
        collectionService.putCollectionItems(playerId, itemDefinitionId, request);

        // THEN
        verify(collectionItem).setCount(request.getItemCount());
        verify(collectionItemRepository).save(collectionItem);
    }

    @Test
    public void givenMissingPlayerId_whenRemoveCollectionItems_thenThrowException() {
        // WHEN & THEN
        assertThatExceptionOfType(ApiException.class)
                .isThrownBy(() -> collectionService.removeCollectionItems(null, null))
                .withMessage(ApiErrors.MISSING_PLAYER_ID_MESSAGE);
    }

    @Test
    public void givenMissingItemDefinitionId_whenRemoveCollectionItems_thenThrowException() {
        // WHEN & THEN
        assertThatExceptionOfType(ApiException.class)
                .isThrownBy(() -> collectionService.removeCollectionItems("testPlayer", null))
                .withMessage(ApiErrors.MISSING_ITEM_DEFINITION_MESSAGE);
    }

    @Test
    public void givenUnknownItemDefinition_whenRemoveCollectionItems_thenThrowException() {
        // WHEN & THEN
        assertThatExceptionOfType(ApiException.class)
                .isThrownBy(() -> collectionService.removeCollectionItems("testPlayer", "testItemDefinition"))
                .withMessage(ApiErrors.UNKNOWN_ITEM_DEFINITION_MESSAGE);
    }

    @Test
    public void givenCollectionItems_whenDeleteCollectionItems_thenDeleteItems() throws ApiException {
        // GIVEN
        String playerId = "testPlayer";
        String itemDefinitionId = "testItemDefinition";

        ItemDefinition itemDefinition = mock(ItemDefinition.class);
        when(itemDefinitionRepository.findById(itemDefinitionId)).thenReturn(Optional.of(itemDefinition));

        // WHEN
        collectionService.removeCollectionItems(playerId, itemDefinitionId);

        // THEN
        verify(collectionItemRepository).deleteByPlayerIdAndItemDefinition(playerId, itemDefinition);
    }

    @Test
    public void givenItemDefinitions_whenGetItemDefinitions_thenReturnDefinitions() {
        // GIVEN
        ItemTag itemTag = mock(ItemTag.class);
        when(itemTag.getTag()).thenReturn("testItemTag");

        ItemDefinition itemDefinition1 = mock(ItemDefinition.class);
        when(itemDefinition1.getId()).thenReturn("testItemDefinition1");
        when(itemDefinition1.getItemTags()).thenReturn(Lists.list(itemTag));

        ItemDefinition itemDefinition2 = mock(ItemDefinition.class);
        when(itemDefinition2.getId()).thenReturn("testItemDefinition2");
        when(itemDefinition2.getItemTags()).thenReturn(Lists.list(itemTag));

        when(itemDefinitionRepository.findAll()).thenReturn(Lists.list(itemDefinition1, itemDefinition2));

        // WHEN
        GetItemDefinitionsResponse response = collectionService.getItemDefinitions();

        // THEN
        assertThat(response).isNotNull();
        assertThat(response.getItemDefinitions()).isNotNull();
        assertThat(response.getItemDefinitions()).hasSize(2);
        assertThat(response.getItemDefinitions().get(0).getId()).isEqualTo(itemDefinition1.getId());
        assertThat(response.getItemDefinitions().get(0).getTags()).isNotNull();
        assertThat(response.getItemDefinitions().get(0).getTags()).hasSize(1);
        assertThat(response.getItemDefinitions().get(0).getTags().get(0)).isEqualTo(itemTag.getTag());
        assertThat(response.getItemDefinitions().get(1).getId()).isEqualTo(itemDefinition2.getId());
        assertThat(response.getItemDefinitions().get(1).getTags()).isNotNull();
        assertThat(response.getItemDefinitions().get(1).getTags()).hasSize(1);
        assertThat(response.getItemDefinitions().get(1).getTags().get(0)).isEqualTo(itemTag.getTag());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void givenItemTags_whenPutItemDefinitions_thenAddsNewTags() {
        // GIVEN
        PutItemDefinitionsRequestItem item = mock(PutItemDefinitionsRequestItem.class);
        when(item.getId()).thenReturn("testItem");
        when(item.getTags()).thenReturn(Lists.list("A", "B"));

        PutItemDefinitionsRequest request = mock(PutItemDefinitionsRequest.class);
        when(request.getItemDefinitions()).thenReturn(Lists.list(item));

        // WHEN
        collectionService.putItemDefinitions(request);

        // THEN
        ArgumentCaptor<List<ItemTag>> argument = ArgumentCaptor.forClass(List.class);
        verify(itemTagRepository).saveAll(argument.capture());

        List<ItemTag> savedTags = argument.getValue();

        assertThat(savedTags).isNotNull();
        assertThat(savedTags).hasSize(2);
        assertThat(savedTags.get(0).getTag()).isEqualTo(item.getTags().get(0));
        assertThat(savedTags.get(1).getTag()).isEqualTo(item.getTags().get(1));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void givenItemTags_whenPutItemDefinitions_thenRemovesObsoleteTags() {
        // GIVEN
        ItemTag itemTagA = mock(ItemTag.class);
        when(itemTagA.getTag()).thenReturn("A");

        ItemTag itemTagB = mock(ItemTag.class);
        when(itemTagB.getTag()).thenReturn("B");

        List<ItemTag> existingTags = Lists.list(itemTagA, itemTagB);
        when(itemTagRepository.findAll()).thenReturn(existingTags);

        PutItemDefinitionsRequest request = mock(PutItemDefinitionsRequest.class);

        // WHEN
        collectionService.putItemDefinitions(request);

        // THEN
        ArgumentCaptor<List<ItemTag>> argument = ArgumentCaptor.forClass(List.class);
        verify(itemTagRepository).deleteAll(argument.capture());

        List<ItemTag> deletedTags = argument.getValue();

        assertThat(deletedTags).isNotNull();
        assertThat(deletedTags).hasSize(2);
        assertThat(deletedTags.get(0)).isEqualTo(itemTagA);
        assertThat(deletedTags.get(1)).isEqualTo(itemTagB);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void givenItemTags_whenPutItemDefinitions_thenRetainsExistingTags() {
        // GIVEN
        ItemTag itemTagA = mock(ItemTag.class);
        when(itemTagA.getTag()).thenReturn("A");

        ItemTag itemTagB = mock(ItemTag.class);
        when(itemTagB.getTag()).thenReturn("B");

        List<ItemTag> existingTags = Lists.list(itemTagA, itemTagB);
        when(itemTagRepository.findAll()).thenReturn(existingTags);

        PutItemDefinitionsRequestItem item = mock(PutItemDefinitionsRequestItem.class);
        when(item.getId()).thenReturn("testItem");
        when(item.getTags()).thenReturn(Lists.list("A", "B", "C"));

        PutItemDefinitionsRequest request = mock(PutItemDefinitionsRequest.class);
        when(request.getItemDefinitions()).thenReturn(Lists.list(item));

        // WHEN
        collectionService.putItemDefinitions(request);

        // THEN
        ArgumentCaptor<List<ItemTag>> argument = ArgumentCaptor.forClass(List.class);
        verify(itemTagRepository).deleteAll(argument.capture());
        List<ItemTag> deletedTags = argument.getValue();

        verify(itemTagRepository).saveAll(argument.capture());
        List<ItemTag> savedTags = argument.getValue();

        assertThat(deletedTags).isNotNull();
        assertThat(deletedTags).doesNotContain(itemTagA, itemTagB);
        assertThat(savedTags).isNotNull();
        assertThat(savedTags).doesNotContain(itemTagA, itemTagB);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void givenItemDefinitions_whenPutItemDefinitions_thenAddsNewDefinitions() {
        // GIVEN
        String itemTag = "A";

        PutItemDefinitionsRequestItem item1 = mock(PutItemDefinitionsRequestItem.class);
        when(item1.getId()).thenReturn("Item1");

        PutItemDefinitionsRequestItem item2 = mock(PutItemDefinitionsRequestItem.class);
        when(item2.getId()).thenReturn("Item2");
        when(item2.getTags()).thenReturn(Lists.list(itemTag));

        PutItemDefinitionsRequest request = mock(PutItemDefinitionsRequest.class);
        when(request.getItemDefinitions()).thenReturn(Lists.list(item1, item2));

        // WHEN
        collectionService.putItemDefinitions(request);

        // THEN
        ArgumentCaptor<List<ItemDefinition>> argument = ArgumentCaptor.forClass(List.class);
        verify(itemDefinitionRepository).saveAll(argument.capture());

        List<ItemDefinition> savedDefinitions = argument.getValue();

        assertThat(savedDefinitions).isNotNull();
        assertThat(savedDefinitions).hasSize(2);
        assertThat(savedDefinitions.get(0).getId()).isEqualTo(item1.getId());
        assertThat(savedDefinitions.get(1).getId()).isEqualTo(item2.getId());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void givenItemDefinitions_whenPutItemDefinitions_thenRemovesObsoleteDefinitions() {
        // GIVEN
        ItemDefinition item1 = mock(ItemDefinition.class);
        when(item1.getId()).thenReturn("Item1");

        ItemDefinition item2 = mock(ItemDefinition.class);
        when(item2.getId()).thenReturn("Item2");

        List<ItemDefinition> existingDefinitions = Lists.list(item1, item2);
        when(itemDefinitionRepository.findAll()).thenReturn(existingDefinitions);

        PutItemDefinitionsRequest request = mock(PutItemDefinitionsRequest.class);

        // WHEN
        collectionService.putItemDefinitions(request);

        // THEN
        ArgumentCaptor<List<ItemDefinition>> argument = ArgumentCaptor.forClass(List.class);
        verify(itemDefinitionRepository).deleteAll(argument.capture());

        List<ItemDefinition> deletedDefinitions = argument.getValue();

        assertThat(deletedDefinitions).isNotNull();
        assertThat(deletedDefinitions).hasSize(2);
        assertThat(deletedDefinitions.get(0)).isEqualTo(item1);
        assertThat(deletedDefinitions.get(1)).isEqualTo(item2);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void givenItemDefinitions_whenPutItemDefinitions_thenRetainsExistingDefinitions() {
        // GIVEN
        String itemId1 = "Item1";
        String itemId2 = "Item2";

        ItemDefinition item1 = mock(ItemDefinition.class);
        when(item1.getId()).thenReturn(itemId1);

        ItemDefinition item2 = mock(ItemDefinition.class);
        when(item2.getId()).thenReturn(itemId2);

        List<ItemDefinition> existingDefinitions = Lists.list(item1, item2);
        when(itemDefinitionRepository.findAll()).thenReturn(existingDefinitions);

        PutItemDefinitionsRequest request = mock(PutItemDefinitionsRequest.class);

        PutItemDefinitionsRequestItem requestedItem1 = mock(PutItemDefinitionsRequestItem.class);
        when(requestedItem1.getId()).thenReturn(itemId1);

        PutItemDefinitionsRequestItem requestedItem2 = mock(PutItemDefinitionsRequestItem.class);
        when(requestedItem2.getId()).thenReturn(itemId2);

        when(request.getItemDefinitions()).thenReturn(Lists.list(requestedItem1, requestedItem2));

        // WHEN
        collectionService.putItemDefinitions(request);

        // THEN
        ArgumentCaptor<List<ItemDefinition>> argument = ArgumentCaptor.forClass(List.class);
        verify(itemDefinitionRepository).deleteAll(argument.capture());
        List<ItemDefinition> deletedDefinitions = argument.getValue();

        assertThat(deletedDefinitions).isNotNull();
        assertThat(deletedDefinitions).doesNotContain(item1, item2);
    }

    @Test
    public void givenMissingPlayerId_whenClaimItemSet_thenThrowException() {
        // WHEN & THEN
        assertThatExceptionOfType(ApiException.class)
                .isThrownBy(() -> collectionService.claimItemSet(""))
                .withMessage(ApiErrors.MISSING_PLAYER_ID_MESSAGE);
    }

    @Test
    public void givenNoUnclaimedItemSets_whenClaimItemSet_thenReturnEmptyResponse() throws ApiException {
        // GIVEN
        String playerId = "testPlayer";

        when(itemSetRepository.findUnclaimedItemSetsByPlayerId(playerId)).thenReturn(Lists.emptyList());

        // WHEN
        ClaimItemSetResponse response = collectionService.claimItemSet(playerId);

        // THEN
        assertThat(response).isNotNull();
        assertThat(response.getItemSetId()).isNull();
    }

    @Test
    public void givenUnclaimedItemSet_whenClaimItemSet_thenAddNewItemsToCollection() throws ApiException {
        // GIVEN
        String playerId = "testPlayer";

        ItemDefinition itemDefinition = mock(ItemDefinition.class);

        ItemSetItem itemSetItem = mock(ItemSetItem.class);
        when(itemSetItem.getItemDefinition()).thenReturn(itemDefinition);
        when(itemSetItem.getCount()).thenReturn(2);

        ItemSet itemSet = mock(ItemSet.class);
        when(itemSet.getItems()).thenReturn(Lists.list(itemSetItem));

        when(itemSetRepository.findUnclaimedItemSetsByPlayerId(playerId)).thenReturn(Lists.list(itemSet));

        // WHEN
        collectionService.claimItemSet(playerId);

        // THEN
        ArgumentCaptor<CollectionItem> argumentCaptor = ArgumentCaptor.forClass(CollectionItem.class);
        verify(collectionItemRepository).save(argumentCaptor.capture());

        CollectionItem collectionItem = argumentCaptor.getValue();

        assertThat(collectionItem).isNotNull();
        assertThat(collectionItem.getPlayerId()).isEqualTo(playerId);
        assertThat(collectionItem.getItemDefinition()).isEqualTo(itemSetItem.getItemDefinition());
        assertThat(collectionItem.getCount()).isEqualTo(itemSetItem.getCount());
    }

    @Test
    public void givenUnclaimedItemSet_whenClaimItemSet_thenIncreasesExistingCollectionItemCount() throws ApiException {
        // GIVEN
        String playerId = "testPlayer";
        int oldCount = 2;
        int additionalCount = 3;

        ItemDefinition itemDefinition = mock(ItemDefinition.class);

        CollectionItem collectionItem = mock(CollectionItem.class);
        when(collectionItem.getCount()).thenReturn(oldCount);
        when(collectionItemRepository.findByPlayerIdAndItemDefinition(playerId, itemDefinition)).thenReturn(Optional.of(collectionItem));

        ItemSetItem itemSetItem = mock(ItemSetItem.class);
        when(itemSetItem.getItemDefinition()).thenReturn(itemDefinition);
        when(itemSetItem.getCount()).thenReturn(additionalCount);

        ItemSet itemSet = mock(ItemSet.class);
        when(itemSet.getItems()).thenReturn(Lists.list(itemSetItem));

        when(itemSetRepository.findUnclaimedItemSetsByPlayerId(playerId)).thenReturn(Lists.list(itemSet));

        // WHEN
        collectionService.claimItemSet(playerId);

        // THEN
        verify(collectionItem).setCount(oldCount + additionalCount);
        verify(collectionItemRepository).save(collectionItem);
    }

    @Test
    public void givenUnclaimedItemSet_whenClaimItemSet_thenSavesClaim() throws ApiException {
        // GIVEN
        String playerId = "testPlayer";
        ItemSet itemSet = mock(ItemSet.class);

        when(itemSetRepository.findUnclaimedItemSetsByPlayerId(playerId)).thenReturn(Lists.list(itemSet));

        // WHEN
        collectionService.claimItemSet(playerId);

        // THEN
        ArgumentCaptor<ClaimedItemSet> argumentCaptor = ArgumentCaptor.forClass(ClaimedItemSet.class);
        verify(claimedItemSetRepository).save(argumentCaptor.capture());

        ClaimedItemSet claimedItemSet = argumentCaptor.getValue();

        assertThat(claimedItemSet).isNotNull();
        assertThat(claimedItemSet.getItemSet()).isEqualTo(itemSet);
        assertThat(claimedItemSet.getPlayerId()).isEqualTo(playerId);
    }

    @Test
    public void givenUnclaimedItemSet_whenClaimItemSet_thenReturnsClaimedItems() throws ApiException {
        // GIVEN
        String playerId = "testPlayer";

        ItemDefinition itemDefinition = mock(ItemDefinition.class);
        when(itemDefinition.getId()).thenReturn("testItem");

        ItemSetItem itemSetItem = mock(ItemSetItem.class);
        when(itemSetItem.getItemDefinition()).thenReturn(itemDefinition);
        when(itemSetItem.getCount()).thenReturn(2);

        ItemSet itemSet = mock(ItemSet.class);
        when(itemSet.getId()).thenReturn("testItemSet");
        when(itemSet.getItems()).thenReturn(Lists.list(itemSetItem));

        when(itemSetRepository.findUnclaimedItemSetsByPlayerId(playerId)).thenReturn(Lists.list(itemSet));

        // WHEN
        ClaimItemSetResponse response = collectionService.claimItemSet(playerId);

        // THEN
        assertThat(response).isNotNull();
        assertThat(response.getItemSetId()).isEqualTo(itemSet.getId());
        assertThat(response.getItems()).hasSize(1);
        assertThat(response.getItems().get(0).getId()).isEqualTo(itemSetItem.getItemDefinition().getId());
        assertThat(response.getItems().get(0).getCount()).isEqualTo(itemSetItem.getCount());
    }
}
