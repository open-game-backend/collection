package de.opengamebackend.collection.controller;

import de.opengamebackend.collection.model.requests.AddCollectionItemsRequest;
import de.opengamebackend.collection.model.requests.PutCollectionItemsRequest;
import de.opengamebackend.collection.model.requests.PutItemDefinitionsRequest;
import de.opengamebackend.collection.model.responses.GetCollectionResponse;
import de.opengamebackend.collection.model.responses.GetItemDefinitionsResponse;
import de.opengamebackend.net.ApiErrors;
import de.opengamebackend.net.ApiException;
import de.opengamebackend.net.HttpHeader;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class CollectionController {
    private CollectionService collectionService;

    public CollectionController(CollectionService collectionService) {
        this.collectionService = collectionService;
    }

    @GetMapping("/client/collection")
    @Operation(summary = "Gets the full item collection of the player.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Collection fetched.",
                    content = { @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = GetCollectionResponse.class)) }),
            @ApiResponse(
                    responseCode = "400",
                    description =
                            "Error " + ApiErrors.MISSING_PLAYER_ID_CODE + ": " + ApiErrors.MISSING_PLAYER_ID_MESSAGE,
                    content = { @Content })
    })
    public ResponseEntity<GetCollectionResponse> getCollection(@RequestHeader(HttpHeader.PLAYER_ID) String playerId)
            throws ApiException {
        GetCollectionResponse response = collectionService.getCollection(playerId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/admin/collection/{playerId}")
    @Operation(summary = "Gets the full item collection of a player.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Collection fetched.",
                    content = { @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = GetCollectionResponse.class)) }),
            @ApiResponse(
                    responseCode = "400",
                    description =
                            "Error " + ApiErrors.MISSING_PLAYER_ID_CODE + ": " + ApiErrors.MISSING_PLAYER_ID_MESSAGE,
                    content = { @Content })
    })
    public ResponseEntity<GetCollectionResponse> getCollectionAdmin(@PathVariable String playerId)
            throws ApiException {
        GetCollectionResponse response = collectionService.getCollection(playerId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/admin/collection/{playerId}/items")
    @Operation(summary = "Adds a new item to the collection of a player.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Item(s) added."),
            @ApiResponse(
                    responseCode = "400",
                    description =
                            "Error " + ApiErrors.MISSING_PLAYER_ID_CODE + ": " + ApiErrors.MISSING_PLAYER_ID_MESSAGE + "<br />" +
                            "Error " + ApiErrors.MISSING_ITEM_DEFINITION_CODE + ": " + ApiErrors.MISSING_ITEM_DEFINITION_MESSAGE + "<br />" +
                            "Error " + ApiErrors.UNKNOWN_ITEM_DEFINITION_CODE + ": " + ApiErrors.UNKNOWN_ITEM_DEFINITION_MESSAGE + "<br />" +
                            "Error " + ApiErrors.INVALID_ITEM_COUNT_CODE + ": " + ApiErrors.INVALID_ITEM_COUNT_MESSAGE,
                    content = { @Content })
    })
    public ResponseEntity<Void> postCollectionItem(@PathVariable String playerId,
                                               @RequestBody AddCollectionItemsRequest request) throws ApiException {
        collectionService.addCollectionItems(playerId, request);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PutMapping("/admin/collection/{playerId}/items/{itemDefinitionId}")
    @Operation(summary = "Sets the number of items of a specific type in the collection of a player.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Item count updated."),
            @ApiResponse(
                    responseCode = "400",
                    description =
                            "Error " + ApiErrors.MISSING_PLAYER_ID_CODE + ": " + ApiErrors.MISSING_PLAYER_ID_MESSAGE + "<br />" +
                                    "Error " + ApiErrors.MISSING_ITEM_DEFINITION_CODE + ": " + ApiErrors.MISSING_ITEM_DEFINITION_MESSAGE + "<br />" +
                                    "Error " + ApiErrors.UNKNOWN_ITEM_DEFINITION_CODE + ": " + ApiErrors.UNKNOWN_ITEM_DEFINITION_MESSAGE + "<br />" +
                                    "Error " + ApiErrors.INVALID_ITEM_COUNT_CODE + ": " + ApiErrors.INVALID_ITEM_COUNT_MESSAGE  + "<br />" +
                                    "Error " + ApiErrors.PLAYER_DOES_NOT_OWN_ITEM_CODE + ": " + ApiErrors.PLAYER_DOES_NOT_OWN_ITEM_MESSAGE,
                    content = { @Content })
    })
    public ResponseEntity<Void> putCollectionItem(@PathVariable String playerId, @PathVariable String itemDefinitionId,
                                              @RequestBody PutCollectionItemsRequest request) throws ApiException {
        collectionService.putCollectionItems(playerId, itemDefinitionId, request);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/admin/collection/{playerId}/items/{itemDefinitionId}")
    @Operation(summary = "Deletes all items of a specified type of a player.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Items deleted."),
            @ApiResponse(
                    responseCode = "400",
                    description =
                            "Error " + ApiErrors.MISSING_PLAYER_ID_CODE + ": " + ApiErrors.MISSING_PLAYER_ID_MESSAGE + "<br />" +
                                    "Error " + ApiErrors.MISSING_ITEM_DEFINITION_CODE + ": " + ApiErrors.MISSING_ITEM_DEFINITION_MESSAGE + "<br />" +
                                    "Error " + ApiErrors.UNKNOWN_ITEM_DEFINITION_CODE + ": " + ApiErrors.UNKNOWN_ITEM_DEFINITION_MESSAGE,
                    content = { @Content })
    })
    public ResponseEntity<Void> deleteCollectionItem(@PathVariable String playerId,
                                                     @PathVariable String itemDefinitionId) throws ApiException {
        collectionService.removeCollectionItems(playerId, itemDefinitionId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/admin/itemdefinitions")
    @Operation(summary = "Gets the definitions of all items and tags.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Definitions fetched.",
                    content = { @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = GetItemDefinitionsResponse.class)) })
    })
    public ResponseEntity<GetItemDefinitionsResponse> getItemDefinitions() {
        GetItemDefinitionsResponse response = collectionService.getItemDefinitions();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("/admin/itemdefinitions")
    @Operation(summary = "Sets the definitions of all items and tags.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Definitions updated.")
    })
    public ResponseEntity<Void> putItemDefinitions(@RequestBody PutItemDefinitionsRequest request) {
        collectionService.putItemDefinitions(request);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
