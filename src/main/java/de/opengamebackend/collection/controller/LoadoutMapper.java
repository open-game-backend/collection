package de.opengamebackend.collection.controller;

import de.opengamebackend.collection.model.entities.*;
import de.opengamebackend.collection.model.repositories.ItemDefinitionRepository;
import de.opengamebackend.collection.model.requests.LoadoutRequest;
import de.opengamebackend.collection.model.requests.LoadoutRequestItem;
import de.opengamebackend.net.ApiErrors;
import de.opengamebackend.net.ApiException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;

@Component
public class LoadoutMapper {
    private final ItemDefinitionRepository itemDefinitionRepository;

    @Autowired
    public LoadoutMapper(ItemDefinitionRepository itemDefinitionRepository) {
        this.itemDefinitionRepository = itemDefinitionRepository;
    }

    public void mapLoadout(String playerId, LoadoutType loadoutType, LoadoutRequest request, Loadout loadout)
            throws ApiException {
        loadout.setPlayerId(playerId);
        loadout.setType(loadoutType);

        loadout.getItems().clear();

        for (LoadoutRequestItem item : request.getItems()) {
            ItemDefinition itemDefinition = itemDefinitionRepository.findById(item.getId()).orElse(null);

            if (itemDefinition == null) {
                throw new ApiException(ApiErrors.UNKNOWN_ITEM_DEFINITION_CODE, ApiErrors.UNKNOWN_ITEM_DEFINITION_MESSAGE);
            }

            LoadoutItem itemEntity = new LoadoutItem();
            itemEntity.setLoadout(loadout);
            itemEntity.setItemDefinition(itemDefinition);
            itemEntity.setCount(item.getCount());

            loadout.getItems().add(itemEntity);
        }
    }

    public void verifyLoadout(Loadout loadout) throws ApiException {
        for (LoadoutRule rule : loadout.getType().getRules()) {
            verifyLoadoutRule(loadout, rule);
        }
    }

    private void verifyLoadoutRule(Loadout loadout, LoadoutRule rule) throws ApiException {
        int count = 0;
        HashMap<String, Integer> copies = new HashMap<>();

        for (LoadoutItem item : loadout.getItems()) {
            if (item.getItemDefinition().getItemTags().stream().noneMatch(t -> t.equals(rule.getItemTag()))) {
                continue;
            }

            count += item.getCount();

            int newCopies = copies.getOrDefault(item.getItemDefinition().getId(), 0) + item.getCount();

            if (newCopies > rule.getMaxCopies()) {
                throw new ApiException(ApiErrors.INVALID_LOADOUT_CODE, ApiErrors.INVALID_LOADOUT_MESSAGE + " - " +
                        String.format("Item %s with tag %s occurs more than %d times.",
                                item.getItemDefinition().getId(), rule.getItemTag().getTag(), rule.getMaxCopies()));
            }

            copies.put(item.getItemDefinition().getId(), newCopies);
        }

        if (count < rule.getMinTotal()) {
            throw new ApiException(ApiErrors.INVALID_LOADOUT_CODE, ApiErrors.INVALID_LOADOUT_MESSAGE + " - " +
                    String.format("Items with tag %s occur less than %d times.",
                            rule.getItemTag().getTag(), rule.getMinTotal()));
        }

        if (count > rule.getMaxTotal()) {
            throw new ApiException(ApiErrors.INVALID_LOADOUT_CODE, ApiErrors.INVALID_LOADOUT_MESSAGE + " - " +
                    String.format("Items with tag %s occur more than %d times.",
                            rule.getItemTag().getTag(), rule.getMaxTotal()));
        }
    }
}
