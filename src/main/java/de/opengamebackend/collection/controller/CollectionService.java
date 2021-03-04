package de.opengamebackend.collection.controller;

import com.google.common.base.Strings;
import de.opengamebackend.collection.model.entities.*;
import de.opengamebackend.collection.model.repositories.*;
import de.opengamebackend.collection.model.requests.*;
import de.opengamebackend.collection.model.responses.*;
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
    private final ItemSetRepository itemSetRepository;
    private final ClaimedItemSetRepository claimedItemSetRepository;

    @Autowired
    public CollectionService(CollectionItemRepository collectionItemRepository,
                             ItemDefinitionRepository itemDefinitionRepository, ItemTagRepository itemTagRepository,
                             ItemSetRepository itemSetRepository, ClaimedItemSetRepository claimedItemSetRepository) {
        this.collectionItemRepository = collectionItemRepository;
        this.itemDefinitionRepository = itemDefinitionRepository;
        this.itemTagRepository = itemTagRepository;
        this.itemSetRepository = itemSetRepository;
        this.claimedItemSetRepository = claimedItemSetRepository;
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
        List<GetItemDefinitionsResponseItem> itemDefinitions = new ArrayList<>();

        for (ItemDefinition itemDefinitionEntity : itemDefinitionRepository.findAll()) {
            GetItemDefinitionsResponseItem itemDefinition = new GetItemDefinitionsResponseItem();
            itemDefinition.setId(itemDefinitionEntity.getId());
            itemDefinition.setMaxCount(itemDefinitionEntity.getMaxCount());
            itemDefinition.setTags(itemDefinitionEntity.getItemTags().stream()
                    .map(ItemTag::getTag)
                    .collect(Collectors.toList()));

            ArrayList<GetItemDefinitionsResponseItemContainer> itemContainers = new ArrayList<>();

            for (ItemContainer itemContainerEntity : itemDefinitionEntity.getContainers()) {
                GetItemDefinitionsResponseItemContainer itemContainer = new GetItemDefinitionsResponseItemContainer();
                itemContainer.setItemCount(itemContainerEntity.getItemCount());

                ArrayList<GetItemDefinitionsResponseItemContainerItem> containedItems = new ArrayList<>();

                for (ContainedItem containedItemEntity : itemContainerEntity.getContainedItems()) {
                    GetItemDefinitionsResponseItemContainerItem item = new GetItemDefinitionsResponseItemContainerItem();

                    item.setRequiredTags(containedItemEntity.getRequiredTags().stream()
                            .map(ItemTag::getTag)
                            .collect(Collectors.toList()));
                    item.setRelativeProbability(containedItemEntity.getRelativeProbability());

                    containedItems.add(item);
                }

                itemContainer.setContainedItems(containedItems);

                itemContainers.add(itemContainer);
            }

            itemDefinition.setContainers(itemContainers);

            itemDefinitions.add(itemDefinition);
        }

        return new GetItemDefinitionsResponse(itemDefinitions);
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

            itemDefinitionEntity.setMaxCount(itemDefinition.getMaxCount());
            itemDefinitionEntity.setItemTags(itemDefinition.getTags().stream()
                    .map(itemTags::get)
                    .collect(Collectors.toList()));

            ArrayList<ItemContainer> itemContainerEntities = new ArrayList<>();

            for (PutItemDefinitionsRequestItemContainer itemContainer : itemDefinition.getContainers()) {
                ItemContainer itemContainerEntity = new ItemContainer();
                itemContainerEntity.setOwningItemDefinition(itemDefinitionEntity);
                itemContainerEntity.setItemCount(itemContainer.getItemCount());

                ArrayList<ContainedItem> containedItemEntities = new ArrayList<>();

                for (PutItemDefinitionsRequestItemContainerItem item : itemContainer.getContainedItems()) {
                    ContainedItem containedItemEntity = new ContainedItem();

                    containedItemEntity.setItemContainer(itemContainerEntity);
                    containedItemEntity.setRequiredTags(item.getRequiredTags().stream()
                            .map(itemTags::get)
                            .collect(Collectors.toList()));
                    containedItemEntity.setRelativeProbability(item.getRelativeProbability());

                    containedItemEntities.add(containedItemEntity);
                }

                itemContainerEntity.setContainedItems(containedItemEntities);

                itemContainerEntities.add(itemContainerEntity);
            }

            itemDefinitionEntity.getContainers().clear();
            itemDefinitionEntity.getContainers().addAll(itemContainerEntities);

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

    public GetItemSetsResponse getItemSets() {
        ArrayList<GetItemSetsResponseItemSet> itemSets = new ArrayList<>();

        for (ItemSet itemSet : itemSetRepository.findAll()) {
            itemSets.add(new GetItemSetsResponseItemSet(itemSet.getId(),
                    itemSet.getItems().stream()
                            .map(i -> new GetItemSetsResponseItem(i.getItemDefinition().getId(), i.getCount()))
                            .collect(Collectors.toList())));
        }

        return new GetItemSetsResponse(itemSets);
    }

    public void putItemSets(PutItemSetsRequest request) throws ApiException {
        // Prepare collections.
        HashMap<String, ItemSet> itemSets = new HashMap<>();
        HashMap<String, ItemDefinition> itemDefinitions = new HashMap<>();

        ArrayList<ItemSet> itemSetsToSave = new ArrayList<>();
        ArrayList<ItemSet> itemSetsToDelete = new ArrayList<>();

        // Query current state from database.
        for (ItemSet itemSet : itemSetRepository.findAll()) {
            itemSets.put(itemSet.getId(), itemSet);
        }

        for (ItemDefinition itemDefinition : itemDefinitionRepository.findAll()) {
            itemDefinitions.put(itemDefinition.getId(), itemDefinition);
        }

        // Collect requested item sets.
        for (PutItemSetsRequestItemSet itemSet : request.getItemSets()) {
            ItemSet itemSetEntity = itemSets.get(itemSet.getId());

            if (itemSetEntity == null) {
                itemSetEntity = new ItemSet();
                itemSetEntity.setId(itemSet.getId());
                itemSets.put(itemSet.getId(), itemSetEntity);
            }

            ArrayList<ItemSetItem> itemEntities = new ArrayList<>();

            for (PutItemSetsRequestItem item : itemSet.getItems()) {
                ItemDefinition itemDefinition = itemDefinitions.get(item.getItemDefinitionId());

                if (itemDefinition == null) {
                    throw new ApiException(ApiErrors.UNKNOWN_ITEM_DEFINITION_CODE, ApiErrors.UNKNOWN_ITEM_DEFINITION_MESSAGE);
                }

                itemEntities.add(new ItemSetItem(itemSetEntity, itemDefinition, item.getCount()));
            }

            itemSetEntity.getItems().clear();
            itemSetEntity.getItems().addAll(itemEntities);

            itemSetsToSave.add(itemSetEntity);
        }

        // Find definitions to remove.
        for (Map.Entry<String, ItemSet> itemSet : itemSets.entrySet()) {
            if (request.getItemSets().stream().noneMatch(i -> i.getId().equals(itemSet.getKey()))) {
                itemSetsToDelete.add(itemSet.getValue());
            }
        }

        // Apply changes.
        itemSetRepository.saveAll(itemSetsToSave);
        itemSetRepository.deleteAll(itemSetsToDelete);
    }

    public GetClaimedItemSetsResponse getClaimedItemSets(String playerId) {
        List<ClaimedItemSet> claimedItemSets = claimedItemSetRepository.findByPlayerId(playerId);

        return new GetClaimedItemSetsResponse(claimedItemSets.stream()
                .map(s -> s.getItemSet().getId())
                .collect(Collectors.toList()));
    }

    public ClaimItemSetResponse claimItemSet(String playerId) throws ApiException {
        if (Strings.isNullOrEmpty(playerId)) {
            throw new ApiException(ApiErrors.MISSING_PLAYER_ID_CODE, ApiErrors.MISSING_PLAYER_ID_MESSAGE);
        }

        // Find unclaimed item set.
        List<ItemSet> unclaimedItemSets = itemSetRepository.findUnclaimedItemSetsByPlayerId(playerId);

        if (unclaimedItemSets == null || unclaimedItemSets.size() <= 0) {
            return new ClaimItemSetResponse();
        }

        // Add items to collection.
        ItemSet itemSet = unclaimedItemSets.get(0);

        for (ItemSetItem itemSetItem : itemSet.getItems()) {
            CollectionItem collectionItem = collectionItemRepository.findByPlayerIdAndItemDefinition
                    (playerId, itemSetItem.getItemDefinition()).orElse(null);

            if (collectionItem == null) {
                collectionItem = new CollectionItem();
                collectionItem.setPlayerId(playerId);
                collectionItem.setItemDefinition(itemSetItem.getItemDefinition());
            }

            collectionItem.setCount(collectionItem.getCount() + itemSetItem.getCount());

            collectionItemRepository.save(collectionItem);
        }

        // Save claim.
        ClaimedItemSet claimedItemSet = new ClaimedItemSet();
        claimedItemSet.setPlayerId(playerId);
        claimedItemSet.setItemSet(itemSet);

        claimedItemSetRepository.save(claimedItemSet);

        // Send response.
        ClaimItemSetResponse response = new ClaimItemSetResponse();
        response.setItemSetId(itemSet.getId());
        response.setItems(itemSet.getItems().stream()
                .map(i -> new ClaimItemSetResponseItem(i.getItemDefinition().getId(), i.getCount()))
                .collect(Collectors.toList()));
        return response;
    }
}
