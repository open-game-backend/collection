package de.opengamebackend.collection.controller;

import com.google.common.base.Strings;
import de.opengamebackend.collection.model.entities.CollectionItem;
import de.opengamebackend.collection.model.entities.ItemDefinition;
import de.opengamebackend.collection.model.entities.ItemTag;
import de.opengamebackend.collection.model.repositories.CollectionItemRepository;
import de.opengamebackend.collection.model.repositories.ItemDefinitionRepository;
import de.opengamebackend.collection.model.repositories.ItemTagRepository;
import de.opengamebackend.collection.model.requests.PutItemDefinitionsRequest;
import de.opengamebackend.collection.model.requests.PutItemDefinitionsRequestItem;
import de.opengamebackend.collection.model.responses.GetCollectionResponse;
import de.opengamebackend.collection.model.responses.GetCollectionResponseItem;
import de.opengamebackend.collection.model.responses.GetItemDefinitionsResponse;
import de.opengamebackend.collection.model.responses.GetItemDefinitionsResponseItem;
import de.opengamebackend.net.ApiErrors;
import de.opengamebackend.net.ApiException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
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

    public void putItemDefinitions(PutItemDefinitionsRequest request) throws ApiException {
        // Check data integrity.
        for (PutItemDefinitionsRequestItem itemDefinition : request.getItemDefinitions()) {
            for (String itemTag : itemDefinition.getTags()) {
                if (!request.getItemTags().contains(itemTag)) {
                    throw new ApiException(ApiErrors.UNKNOWN_ITEM_TAG_CODE,
                            ApiErrors.UNKNOWN_ITEM_TAG_MESSAGE + " - Item Definition: " + itemDefinition.getId()
                                    + " - Item Tag: " + itemTag);
                }
            }
        }

        // Update database.
        HashMap<String, ItemTag> itemTags = new HashMap<>();
        HashMap<String, ItemDefinition> itemDefinitions = new HashMap<>();

        ArrayList<ItemTag> itemTagsToSave = new ArrayList<>();
        ArrayList<ItemTag> itemTagsToDelete = new ArrayList<>();
        ArrayList<ItemDefinition> itemDefinitionsToSave = new ArrayList<>();
        ArrayList<ItemDefinition> itemDefinitionsToDelete = new ArrayList<>();

        for (ItemTag itemTag : itemTagRepository.findAll()) {
            itemTags.put(itemTag.getTag(), itemTag);
        }

        for (ItemDefinition itemDefinition : itemDefinitionRepository.findAll()) {
            itemDefinitions.put(itemDefinition.getId(), itemDefinition);
        }

        for (String itemTag : request.getItemTags()) {
            ItemTag itemTagEntity = itemTags.get(itemTag);

            if (itemTagEntity == null) {
                itemTagEntity = new ItemTag(itemTag);
                itemTagsToSave.add(itemTagEntity);
                itemTags.put(itemTag, itemTagEntity);
            }
        }

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

        // Clean up database.
        for (Map.Entry<String, ItemTag> itemTag : itemTags.entrySet()) {
            if (!request.getItemTags().contains(itemTag.getKey())) {
                itemTagsToDelete.add(itemTag.getValue());
            }
        }

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
