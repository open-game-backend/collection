package de.opengamebackend.collection.controller;

import de.opengamebackend.collection.model.requests.LoadoutRequest;
import de.opengamebackend.collection.model.requests.PutLoadoutTypesRequest;
import de.opengamebackend.collection.model.responses.AddLoadoutResponse;
import de.opengamebackend.collection.model.responses.GetLoadoutTypesResponse;
import de.opengamebackend.collection.model.responses.GetLoadoutsResponse;
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
public class LoadoutController {
    private final LoadoutService loadoutService;

    public LoadoutController(LoadoutService loadoutService) {
        this.loadoutService = loadoutService;
    }

    @PostMapping("/client/loadouts")
    @Operation(summary = "Adds a new loadout for a player.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Loadout added."),
            @ApiResponse(
                    responseCode = "400",
                    description =
                            "Error " + ApiErrors.MISSING_PLAYER_ID_CODE + ": " + ApiErrors.MISSING_PLAYER_ID_MESSAGE + "<br />" +
                            "Error " + ApiErrors.UNKNOWN_LOADOUT_TYPE_CODE + ": " + ApiErrors.UNKNOWN_LOADOUT_TYPE_MESSAGE + "<br />" +
                            "Error " + ApiErrors.UNKNOWN_ITEM_DEFINITION_CODE + ": " + ApiErrors.UNKNOWN_ITEM_DEFINITION_MESSAGE,
                    content = { @Content})
    })
    public ResponseEntity<AddLoadoutResponse> postLoadout(@RequestHeader(HttpHeader.PLAYER_ID) String playerId,
                                                          @RequestBody LoadoutRequest request) throws ApiException {
        AddLoadoutResponse response = loadoutService.addLoadout(playerId, request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/client/loadouts")
    @Operation(summary = "Gets all loadouts of a player.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Loadouts of the specified player."),
            @ApiResponse(
                    responseCode = "400",
                    description =
                            "Error " + ApiErrors.MISSING_PLAYER_ID_CODE + ": " + ApiErrors.MISSING_PLAYER_ID_MESSAGE,
                    content = { @Content})
    })
    public ResponseEntity<GetLoadoutsResponse> getLoadouts(@RequestHeader(HttpHeader.PLAYER_ID) String playerId)
            throws ApiException {
        GetLoadoutsResponse response = loadoutService.getLoadouts(playerId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("/client/loadouts/{loadoutId}")
    @Operation(summary = "Updates an existing loadout.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Loadout updated."),
            @ApiResponse(
                    responseCode = "400",
                    description =
                            "Error " + ApiErrors.MISSING_PLAYER_ID_CODE + ": " + ApiErrors.MISSING_PLAYER_ID_MESSAGE + "<br />" +
                            "Error " + ApiErrors.UNKNOWN_LOADOUT_CODE + ": " + ApiErrors.UNKNOWN_LOADOUT_MESSAGE + "<br />" +
                            "Error " + ApiErrors.UNKNOWN_LOADOUT_TYPE_CODE + ": " + ApiErrors.UNKNOWN_LOADOUT_TYPE_MESSAGE + "<br />" +
                            "Error " + ApiErrors.UNKNOWN_ITEM_DEFINITION_CODE + ": " + ApiErrors.UNKNOWN_ITEM_DEFINITION_MESSAGE,
                    content = { @Content})
    })
    public ResponseEntity<Void> putLoadout(@RequestHeader(HttpHeader.PLAYER_ID) String playerId,
                                           @PathVariable long loadoutId, @RequestBody LoadoutRequest request)
            throws ApiException {
        loadoutService.putLoadout(playerId, loadoutId, request);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/client/loadouts/{loadoutId}")
    @Operation(summary = "Deletes an existing loadout.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Loadout deleted."),
            @ApiResponse(
                    responseCode = "400",
                    description =
                            "Error " + ApiErrors.MISSING_PLAYER_ID_CODE + ": " + ApiErrors.MISSING_PLAYER_ID_MESSAGE + "<br />" +
                                    "Error " + ApiErrors.UNKNOWN_LOADOUT_CODE + ": " + ApiErrors.UNKNOWN_LOADOUT_MESSAGE,
                    content = { @Content})
    })
    public ResponseEntity<Void> deleteLoadout(@RequestHeader(HttpHeader.PLAYER_ID) String playerId,
                                              @PathVariable long loadoutId)
            throws ApiException {
        loadoutService.deleteLoadout(playerId, loadoutId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/admin/loadouttypes")
    @Operation(summary = "Gets the definitions of all loadout types.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Loadout types fetched.",
                    content = { @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = GetLoadoutTypesResponse.class)) })
    })
    public ResponseEntity<GetLoadoutTypesResponse> getLoadoutTypes() {
        GetLoadoutTypesResponse response = loadoutService.getLoadoutTypes();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("/admin/loadouttypes")
    @Operation(summary = "Sets the definitions of all loadout types.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Definitions updated."),
            @ApiResponse(
                    responseCode = "400",
                    description =
                            "Error " + ApiErrors.UNKNOWN_ITEMTAG_CODE + ": " + ApiErrors.UNKNOWN_ITEMTAG_MESSAGE,
                    content = { @Content})
    })
    public ResponseEntity<Void> putLoadoutTypes(@RequestBody PutLoadoutTypesRequest request) throws ApiException {
        loadoutService.putLoadoutTypes(request);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
