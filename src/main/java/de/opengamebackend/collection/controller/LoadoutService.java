package de.opengamebackend.collection.controller;

import com.google.common.base.Strings;
import de.opengamebackend.collection.model.entities.*;
import de.opengamebackend.collection.model.repositories.ItemDefinitionRepository;
import de.opengamebackend.collection.model.repositories.ItemTagRepository;
import de.opengamebackend.collection.model.repositories.LoadoutRepository;
import de.opengamebackend.collection.model.repositories.LoadoutTypeRepository;
import de.opengamebackend.collection.model.requests.*;
import de.opengamebackend.collection.model.responses.*;
import de.opengamebackend.net.ApiErrors;
import de.opengamebackend.net.ApiException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class LoadoutService {
    private final LoadoutRepository loadoutRepository;
    private final LoadoutTypeRepository loadoutTypeRepository;
    private final ItemTagRepository itemTagRepository;
    private final ItemDefinitionRepository itemDefinitionRepository;

    @Autowired
    public LoadoutService(LoadoutRepository loadoutRepository, LoadoutTypeRepository loadoutTypeRepository,
                          ItemTagRepository itemTagRepository, ItemDefinitionRepository itemDefinitionRepository) {
        this.loadoutRepository = loadoutRepository;
        this.loadoutTypeRepository = loadoutTypeRepository;
        this.itemTagRepository = itemTagRepository;
        this.itemDefinitionRepository = itemDefinitionRepository;
    }

    public AddLoadoutResponse addLoadout(String playerId, LoadoutRequest request) throws ApiException {
        if (Strings.isNullOrEmpty(playerId)) {
            throw new ApiException(ApiErrors.MISSING_PLAYER_ID_CODE, ApiErrors.MISSING_PLAYER_ID_MESSAGE);
        }

        LoadoutType loadoutType = loadoutTypeRepository.findById(request.getType()).orElse(null);

        if (loadoutType == null) {
            throw new ApiException(ApiErrors.UNKNOWN_LOADOUT_TYPE_CODE, ApiErrors.UNKNOWN_LOADOUT_TYPE_MESSAGE);
        }

        Loadout loadout = new Loadout();
        mapLoadout(playerId, loadoutType, request, loadout);
        loadoutRepository.save(loadout);

        return new AddLoadoutResponse(loadout.getId());
    }

    public GetLoadoutsResponse getLoadouts(String playerId) throws ApiException {
        if (Strings.isNullOrEmpty(playerId)) {
            throw new ApiException(ApiErrors.MISSING_PLAYER_ID_CODE, ApiErrors.MISSING_PLAYER_ID_MESSAGE);
        }

        List<Loadout> loadouts = loadoutRepository.findByPlayerId(playerId);
        ArrayList<GetLoadoutsResponseLoadout> responseLoadouts = new ArrayList<>();

        for (Loadout loadout : loadouts) {
            GetLoadoutsResponseLoadout responseLoadout = new GetLoadoutsResponseLoadout();
            responseLoadout.setId(loadout.getId());
            responseLoadout.setType(loadout.getType().getId());

            ArrayList<GetLoadoutsResponseLoadoutItem> responseItems = new ArrayList<>();

            for (LoadoutItem item : loadout.getItems()) {
                GetLoadoutsResponseLoadoutItem responseItem = new GetLoadoutsResponseLoadoutItem();
                responseItem.setItemDefinitionId(item.getItemDefinition().getId());
                responseItem.setCount(item.getCount());

                responseItems.add(responseItem);
            }

            responseLoadout.setItems(responseItems);

            responseLoadouts.add(responseLoadout);
        }

        GetLoadoutsResponse response = new GetLoadoutsResponse();
        response.setLoadouts(responseLoadouts);

        return response;
    }

    public void putLoadout(String playerId, long loadoutId, LoadoutRequest request) throws ApiException {
        if (Strings.isNullOrEmpty(playerId)) {
            throw new ApiException(ApiErrors.MISSING_PLAYER_ID_CODE, ApiErrors.MISSING_PLAYER_ID_MESSAGE);
        }

        Loadout loadout = loadoutRepository.findById(loadoutId).orElse(null);

        if (loadout == null) {
            throw new ApiException(ApiErrors.UNKNOWN_LOADOUT_CODE, ApiErrors.UNKNOWN_LOADOUT_MESSAGE);
        }

        LoadoutType loadoutType = loadoutTypeRepository.findById(request.getType()).orElse(null);

        if (loadoutType == null) {
            throw new ApiException(ApiErrors.UNKNOWN_LOADOUT_TYPE_CODE, ApiErrors.UNKNOWN_LOADOUT_TYPE_MESSAGE);
        }

        mapLoadout(playerId, loadoutType, request, loadout);
        loadoutRepository.save(loadout);
    }

    public void deleteLoadout(String playerId, long loadoutId) throws ApiException {
        if (Strings.isNullOrEmpty(playerId)) {
            throw new ApiException(ApiErrors.MISSING_PLAYER_ID_CODE, ApiErrors.MISSING_PLAYER_ID_MESSAGE);
        }

        Loadout loadout = loadoutRepository.findById(loadoutId).orElse(null);

        if (loadout == null) {
            throw new ApiException(ApiErrors.UNKNOWN_LOADOUT_CODE, ApiErrors.UNKNOWN_LOADOUT_MESSAGE);
        }

        loadoutRepository.delete(loadout);
    }

    public GetLoadoutTypesResponse getLoadoutTypes() {
        GetLoadoutTypesResponse response = new GetLoadoutTypesResponse();

        for (LoadoutType loadoutType : loadoutTypeRepository.findAll()) {
            GetLoadoutTypesResponseType responseType = new GetLoadoutTypesResponseType();
            responseType.setId(loadoutType.getId());

            for (LoadoutRule loadoutRule : loadoutType.getRules()) {
                GetLoadoutTypesResponseTypeRule responseRule = new GetLoadoutTypesResponseTypeRule();
                responseRule.setItemTag(loadoutRule.getItemTag().getTag());
                responseRule.setMinTotal(loadoutRule.getMinTotal());
                responseRule.setMaxTotal(loadoutRule.getMaxTotal());
                responseRule.setMaxCopies(loadoutRule.getMaxCopies());

                responseType.getRules().add(responseRule);
            }

            response.getLoadoutTypes().add(responseType);
        }

        return response;
    }

    public void putLoadoutTypes(PutLoadoutTypesRequest request) throws ApiException {
        // Build entities to save.
        ArrayList<LoadoutType> loadoutTypes = new ArrayList<>();

        for (PutLoadoutTypesRequestType loadoutType : request.getLoadoutTypes()) {
            LoadoutType loadoutTypeEntity = new LoadoutType();
            loadoutTypeEntity.setId(loadoutType.getId());

            for (PutLoadoutTypesRequestTypeRule loadoutRule : loadoutType.getRules()) {
                ItemTag itemTag = itemTagRepository.findById(loadoutRule.getItemTag()).orElse(null);

                if (itemTag == null) {
                    throw new ApiException(ApiErrors.UNKNOWN_ITEMTAG_CODE,
                            ApiErrors.UNKNOWN_ITEMTAG_MESSAGE + " - " + loadoutRule.getItemTag());
                }

                LoadoutRule loadoutRuleEntity = new LoadoutRule();
                loadoutRuleEntity.setType(loadoutTypeEntity);
                loadoutRuleEntity.setItemTag(itemTag);
                loadoutRuleEntity.setMinTotal(loadoutRule.getMinTotal());
                loadoutRuleEntity.setMaxTotal(loadoutRule.getMaxTotal());
                loadoutRuleEntity.setMaxCopies(loadoutRule.getMaxCopies());

                loadoutTypeEntity.getRules().add(loadoutRuleEntity);
            }

            loadoutTypes.add(loadoutTypeEntity);
        }

        // Save entities.
        loadoutTypeRepository.deleteAll();
        loadoutTypeRepository.saveAll(loadoutTypes);
    }

    private void mapLoadout(String playerId, LoadoutType loadoutType, LoadoutRequest request, Loadout loadout)
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
}
