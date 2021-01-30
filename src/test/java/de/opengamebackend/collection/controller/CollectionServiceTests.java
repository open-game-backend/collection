package de.opengamebackend.collection.controller;

import de.opengamebackend.collection.model.entities.CollectionItem;
import de.opengamebackend.collection.model.entities.ItemDefinition;
import de.opengamebackend.collection.model.entities.ItemTag;
import de.opengamebackend.collection.model.repositories.CollectionItemRepository;
import de.opengamebackend.collection.model.repositories.ItemDefinitionRepository;
import de.opengamebackend.collection.model.repositories.ItemTagRepository;
import de.opengamebackend.collection.model.requests.PutItemDefinitionsRequest;
import de.opengamebackend.collection.model.requests.PutItemDefinitionsRequestItem;
import de.opengamebackend.collection.model.responses.GetCollectionResponse;
import de.opengamebackend.collection.model.responses.GetItemDefinitionsResponse;
import de.opengamebackend.net.ApiErrors;
import de.opengamebackend.net.ApiException;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.*;

public class CollectionServiceTests {
    private CollectionItemRepository collectionItemRepository;
    private ItemDefinitionRepository itemDefinitionRepository;
    private ItemTagRepository itemTagRepository;

    private CollectionService collectionService;

    @BeforeEach
    public void beforeEach() {
        collectionItemRepository = mock(CollectionItemRepository.class);
        itemDefinitionRepository = mock(ItemDefinitionRepository.class);
        itemTagRepository = mock(ItemTagRepository.class);

        collectionService = new CollectionService(collectionItemRepository, itemDefinitionRepository, itemTagRepository);
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
    public void givenItemTags_whenGetItemDefinitions_thenReturnTags() {
        // GIVEN
        ItemTag itemTag1 = mock(ItemTag.class);
        when(itemTag1.getTag()).thenReturn("testItemTag1");

        ItemTag itemTag2 = mock(ItemTag.class);
        when(itemTag2.getTag()).thenReturn("testItemTag2");

        when(itemTagRepository.findAll()).thenReturn(Lists.list(itemTag1, itemTag2));

        // WHEN
        GetItemDefinitionsResponse response = collectionService.getItemDefinitions();

        // THEN
        assertThat(response).isNotNull();
        assertThat(response.getItemTags()).isNotNull();
        assertThat(response.getItemTags()).hasSize(2);
        assertThat(response.getItemTags().get(0)).isEqualTo(itemTag1.getTag());
        assertThat(response.getItemTags().get(1)).isEqualTo(itemTag2.getTag());
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
    public void givenItemTags_whenPutItemDefinitions_thenAddsNewTags() throws ApiException {
        // GIVEN
        PutItemDefinitionsRequest request = mock(PutItemDefinitionsRequest.class);
        when(request.getItemTags()).thenReturn(Lists.list("A", "B"));

        // WHEN
        collectionService.putItemDefinitions(request);

        // THEN
        ArgumentCaptor<List<ItemTag>> argument = ArgumentCaptor.forClass(List.class);
        verify(itemTagRepository).saveAll(argument.capture());

        List<ItemTag> savedTags = argument.getValue();

        assertThat(savedTags).isNotNull();
        assertThat(savedTags).hasSize(2);
        assertThat(savedTags.get(0).getTag()).isEqualTo(request.getItemTags().get(0));
        assertThat(savedTags.get(1).getTag()).isEqualTo(request.getItemTags().get(1));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void givenItemTags_whenPutItemDefinitions_thenRemovesObsoleteTags() throws ApiException {
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
    public void givenItemTags_whenPutItemDefinitions_thenRetainsExistingTags() throws ApiException {
        // GIVEN
        ItemTag itemTagA = mock(ItemTag.class);
        when(itemTagA.getTag()).thenReturn("A");

        ItemTag itemTagB = mock(ItemTag.class);
        when(itemTagB.getTag()).thenReturn("B");

        List<ItemTag> existingTags = Lists.list(itemTagA, itemTagB);
        when(itemTagRepository.findAll()).thenReturn(existingTags);

        PutItemDefinitionsRequest request = mock(PutItemDefinitionsRequest.class);
        when(request.getItemTags()).thenReturn(Lists.list("A", "B", "C"));

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
    public void givenItemDefinitions_whenPutItemDefinitions_thenAddsNewDefinitions() throws ApiException {
        // GIVEN
        PutItemDefinitionsRequestItem item1 = mock(PutItemDefinitionsRequestItem.class);
        when(item1.getId()).thenReturn("Item1");

        PutItemDefinitionsRequestItem item2 = mock(PutItemDefinitionsRequestItem.class);
        when(item2.getId()).thenReturn("Item2");

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
    public void givenItemDefinitions_whenPutItemDefinitions_thenRemovesObsoleteDefinitions() throws ApiException {
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
    public void givenItemDefinitions_whenPutItemDefinitions_thenRetainsExistingDefinitions() throws ApiException {
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
}
