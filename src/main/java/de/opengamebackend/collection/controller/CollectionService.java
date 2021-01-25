package de.opengamebackend.collection.controller;

import com.google.common.base.Strings;
import de.opengamebackend.collection.model.entities.CollectionItem;
import de.opengamebackend.collection.model.entities.ItemDefinition;
import de.opengamebackend.collection.model.entities.ItemTag;
import de.opengamebackend.collection.model.repositories.CollectionItemRepository;
import de.opengamebackend.collection.model.repositories.ItemDefinitionRepository;
import de.opengamebackend.collection.model.repositories.ItemTagRepository;
import de.opengamebackend.collection.model.responses.GetCollectionResponse;
import de.opengamebackend.collection.model.responses.GetCollectionResponseItem;
import de.opengamebackend.collection.model.responses.GetItemDefinitionsResponse;
import de.opengamebackend.collection.model.responses.GetItemDefinitionsResponseItem;
import de.opengamebackend.net.ApiErrors;
import de.opengamebackend.net.ApiException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
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

    public GetCollectionResponse get(String playerId) throws ApiException {
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
}
