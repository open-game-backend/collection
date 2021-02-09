package de.opengamebackend.collection.controller;

import com.google.common.base.Strings;
import de.opengamebackend.collection.model.entities.CollectionItem;
import de.opengamebackend.collection.model.entities.ItemDefinition;
import de.opengamebackend.collection.model.entities.ItemTag;
import de.opengamebackend.collection.model.repositories.CollectionItemRepository;
import de.opengamebackend.collection.model.repositories.ItemDefinitionRepository;
import de.opengamebackend.collection.model.repositories.ItemTagRepository;
import de.opengamebackend.collection.model.requests.*;
import de.opengamebackend.collection.model.responses.GetCollectionResponse;
import de.opengamebackend.collection.model.responses.GetCollectionResponseItem;
import de.opengamebackend.collection.model.responses.GetItemDefinitionsResponse;
import de.opengamebackend.collection.model.responses.GetItemDefinitionsResponseItem;
import de.opengamebackend.net.ApiErrors;
import de.opengamebackend.net.ApiException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
public class CollectionService {
    private final CollectionItemRepository collectionItemRepository;
    private final ItemDefinitionRepository itemDefinitionRepository;
    private final ItemTagRepository itemTagRepository;

    @Autowired
    public CollectionService(CollectionItemRepository collectionItemRepository,
                             ItemDefinitionRepository itemDefinitionRepository, ItemTagRepository itemTagRepository) {
        this.collectionItemRepository = collectionItemRepository;
        this.itemDefinitionRepository = itemDefinitionRepository;
        this.itemTagRepository = itemTagRepository;
    }

    public GetCollectionResponse getCollection(String playerId) throws ApiException {
        if (Strings.isNullOrEmpty(playerId)) {
            throw new ApiException(ApiErrors.MISSING_PLAYER_ID_CODE, ApiErrors.MISSING_PLAYER_ID_MESSAGE);
        }

        ArrayList<GetCollectionResponseItem> collection = new ArrayList<>();

        List<CollectionItem> items = collectionItemRepository.findByPlayerId(playerId);

        for (CollectionItem item : items) {
            GetCollectionResponseItem responseItem = new GetCollectionResponseItem();
            responseItem.setId(item.getItemDefinition().getId());
            responseItem.setCount(item.getCount());
            responseItem.setTags(item.getItemDefinition().getItemTags().stream()
                    .map(ItemTag::getTag)
                    .collect(Collectors.toList()));

            collection.add(responseItem);
        }

        return new GetCollectionResponse(collection);
    }

    public void addCollectionItems(String playerId, AddCollectionItemsRequest request) throws ApiException {
        if (Strings.isNullOrEmpty(playerId)) {
            throw new ApiException(ApiErrors.MISSING_PLAYER_ID_CODE, ApiErrors.MISSING_PLAYER_ID_MESSAGE);
        }

        if (Strings.isNullOrEmpty(request.getItemDefinitionId())) {
            throw new ApiException(ApiErrors.MISSING_ITEM_DEFINITION_CODE, ApiErrors.MISSING_ITEM_DEFINITION_MESSAGE);
        }

        ItemDefinition itemDefinition = itemDefinitionRepository.findById(request.getItemDefinitionId()).orElse(null);

        if (itemDefinition == null) {
            throw new ApiException(ApiErrors.UNKNOWN_ITEM_DEFINITION_CODE, ApiErrors.UNKNOWN_ITEM_DEFINITION_MESSAGE);
        }

        if (request.getItemCount() <= 0) {
            throw new ApiException(ApiErrors.INVALID_ITEM_COUNT_CODE, ApiErrors.INVALID_ITEM_COUNT_MESSAGE);
        }

        CollectionItem collectionItem =
                collectionItemRepository.findByPlayerIdAndItemDefinition(playerId, itemDefinition).orElse(null);

        if (collectionItem == null) {
            collectionItem = new CollectionItem();
            collectionItem.setPlayerId(playerId);
            collectionItem.setItemDefinition(itemDefinition);
        }

        collectionItem.setCount(collectionItem.getCount() + request.getItemCount());

        collectionItemRepository.save(collectionItem);
    }

    public void putCollectionItems(String playerId, String itemDefinitionId, PutCollectionItemsRequest request)
            throws ApiException {
        if (Strings.isNullOrEmpty(playerId)) {
            throw new ApiException(ApiErrors.MISSING_PLAYER_ID_CODE, ApiErrors.MISSING_PLAYER_ID_MESSAGE);
        }

        if (Strings.isNullOrEmpty(itemDefinitionId)) {
            throw new ApiException(ApiErrors.MISSING_ITEM_DEFINITION_CODE, ApiErrors.MISSING_ITEM_DEFINITION_MESSAGE);
        }

        ItemDefinition itemDefinition = itemDefinitionRepository.findById(itemDefinitionId).orElse(null);

        if (itemDefinition == null) {
            throw new ApiException(ApiErrors.UNKNOWN_ITEM_DEFINITION_CODE, ApiErrors.UNKNOWN_ITEM_DEFINITION_MESSAGE);
        }

        if (request.getItemCount() <= 0) {
            throw new ApiException(ApiErrors.INVALID_ITEM_COUNT_CODE, ApiErrors.INVALID_ITEM_COUNT_MESSAGE);
        }

        CollectionItem collectionItem =
                collectionItemRepository.findByPlayerIdAndItemDefinition(playerId, itemDefinition).orElse(null);

        if (collectionItem == null) {
            throw new ApiException(ApiErrors.PLAYER_DOES_NOT_OWN_ITEM_CODE, ApiErrors.PLAYER_DOES_NOT_OWN_ITEM_MESSAGE);
        }

        collectionItem.setCount(request.getItemCount());
        collectionItemRepository.save(collectionItem);
    }

    public void removeCollectionItems(String playerId, String itemDefinitionId) throws ApiException {
        if (Strings.isNullOrEmpty(playerId)) {
            throw new ApiException(ApiErrors.MISSING_PLAYER_ID_CODE, ApiErrors.MISSING_PLAYER_ID_MESSAGE);
        }

        if (Strings.isNullOrEmpty(itemDefinitionId)) {
            throw new ApiException(ApiErrors.MISSING_ITEM_DEFINITION_CODE, ApiErrors.MISSING_ITEM_DEFINITION_MESSAGE);
        }

        ItemDefinition itemDefinition = itemDefinitionRepository.findById(itemDefinitionId).orElse(null);

        if (itemDefinition == null) {
            throw new ApiException(ApiErrors.UNKNOWN_ITEM_DEFINITION_CODE, ApiErrors.UNKNOWN_ITEM_DEFINITION_MESSAGE);
        }

        collectionItemRepository.deleteByPlayerIdAndItemDefinition(playerId, itemDefinition);
    }

    public GetItemDefinitionsResponse getItemDefinitions() {
        List<String> itemTags = new ArrayList<>();

        for (ItemTag itemTag : itemTagRepository.findAll()) {
            itemTags.add(itemTag.getTag());
        }

        List<GetItemDefinitionsResponseItem> itemDefinitions = new ArrayList<>();

        for (ItemDefinition itemDefinition : itemDefinitionRepository.findAll()) {
            itemDefinitions.add(new GetItemDefinitionsResponseItem(itemDefinition.getId(),
                    itemDefinition.getItemTags().stream()
                            .map(ItemTag::getTag)
                            .collect(Collectors.toList())));
        }

        return new GetItemDefinitionsResponse(itemTags, itemDefinitions);
    }

    public void putItemDefinitions(PutItemDefinitionsRequest request) {
        // Prepare collections.
        HashMap<String, ItemTag> itemTags = new HashMap<>();
        HashMap<String, ItemDefinition> itemDefinitions = new HashMap<>();

        ArrayList<ItemTag> itemTagsToSave = new ArrayList<>();
        ArrayList<ItemTag> itemTagsToDelete = new ArrayList<>();
        ArrayList<ItemDefinition> itemDefinitionsToSave = new ArrayList<>();
        ArrayList<ItemDefinition> itemDefinitionsToDelete = new ArrayList<>();

        // Query current state from database.
        for (ItemTag itemTag : itemTagRepository.findAll()) {
            itemTags.put(itemTag.getTag(), itemTag);
        }

        for (ItemDefinition itemDefinition : itemDefinitionRepository.findAll()) {
            itemDefinitions.put(itemDefinition.getId(), itemDefinition);
        }

        // Collect requested item tags.
        for (PutItemDefinitionsRequestItem itemDefinition : request.getItemDefinitions()) {
            for (String itemTag : itemDefinition.getTags()) {
                ItemTag itemTagEntity = itemTags.get(itemTag);

                if (itemTagEntity == null) {
                    itemTagEntity = new ItemTag(itemTag);
                    itemTagsToSave.add(itemTagEntity);
                    itemTags.put(itemTag, itemTagEntity);
                }
            }
        }

        // Collect requested item definitions.
        for (PutItemDefinitionsRequestItem itemDefinition : request.getItemDefinitions()) {

            ItemDefinition itemDefinitionEntity = itemDefinitions.get(itemDefinition.getId());

            if (itemDefinitionEntity == null) {
                itemDefinitionEntity = new ItemDefinition();
                itemDefinitionEntity.setId(itemDefinition.getId());
                itemDefinitions.put(itemDefinition.getId(), itemDefinitionEntity);
            }

            itemDefinitionEntity.setItemTags(itemDefinition.getTags().stream()
                    .map(itemTags::get)
                    .collect(Collectors.toList()));

            itemDefinitionsToSave.add(itemDefinitionEntity);
        }

        // Find tags to remove.
        for (Map.Entry<String, ItemTag> itemTag : itemTags.entrySet()) {
            if (request.getItemDefinitions().stream().noneMatch(i -> i.getTags().contains(itemTag.getKey()))) {
                itemTagsToDelete.add(itemTag.getValue());
            }
        }

        // Find definitions to remove.
        for (Map.Entry<String, ItemDefinition> itemDefinition : itemDefinitions.entrySet()) {
            if (request.getItemDefinitions().stream().noneMatch(i -> i.getId().equals(itemDefinition.getKey()))) {
                itemDefinitionsToDelete.add(itemDefinition.getValue());
            }
        }

        // Apply changes.
        itemTagRepository.saveAll(itemTagsToSave);
        itemTagRepository.deleteAll(itemTagsToDelete);

        itemDefinitionRepository.saveAll(itemDefinitionsToSave);
        itemDefinitionRepository.deleteAll(itemDefinitionsToDelete);
    }
}
