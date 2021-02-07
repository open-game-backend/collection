package de.opengamebackend.collection.model;

import de.opengamebackend.collection.model.entities.ItemDefinition;
import de.opengamebackend.collection.model.entities.ItemTag;
import de.opengamebackend.collection.model.repositories.ItemDefinitionRepository;
import de.opengamebackend.collection.model.repositories.ItemTagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.transaction.Transactional;
import java.util.Arrays;

@Component
public class DatabaseSeed {
    private final ItemTagRepository itemTagRepository;
    private final ItemDefinitionRepository itemDefinitionRepository;

    @Autowired
    public DatabaseSeed(ItemTagRepository itemTagRepository, ItemDefinitionRepository itemDefinitionRepository) {
        this.itemTagRepository = itemTagRepository;
        this.itemDefinitionRepository = itemDefinitionRepository;
    }

    @PostConstruct
    public void postConstruct() {
        // Seed item tags.
        ItemTag headTag = getOrCreateItemTag("Head");
        ItemTag chestTag = getOrCreateItemTag("Chest");
        ItemTag mainHandTag = getOrCreateItemTag("Main Hand");
        ItemTag feetTag = getOrCreateItemTag("Feet");
        ItemTag epicTag = getOrCreateItemTag("Epic");

        // Seed item definitions.
        createItemDefinitionIfNotExists("FancyHat", headTag);
        createItemDefinitionIfNotExists("ShinyArmor", chestTag);
        createItemDefinitionIfNotExists("MightySword", mainHandTag, epicTag);
        createItemDefinitionIfNotExists("RuggedBoots", feetTag);
    }

    @Transactional
    private ItemTag getOrCreateItemTag(String tagName) {
        ItemTag itemTag = itemTagRepository.findById(tagName).orElse(null);

        if (itemTag != null) {
            return itemTag;
        }

        itemTag = new ItemTag(tagName);
        itemTagRepository.save(itemTag);

        return itemTag;
    }

    @Transactional
    private void createItemDefinitionIfNotExists(String id, ItemTag... tags) {
        ItemDefinition itemDefinition = itemDefinitionRepository.findById(id).orElse(null);

        if (itemDefinition != null) {
            return;
        }

        itemDefinition = new ItemDefinition();
        itemDefinition.setId(id);
        itemDefinition.setItemTags(Arrays.asList(tags));

        itemDefinitionRepository.save(itemDefinition);
    }
}
