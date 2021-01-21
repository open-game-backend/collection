package de.opengamebackend.collection.controller;

import de.opengamebackend.collection.model.responses.GetCollectionResponse;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CollectionController {
    private CollectionService collectionService;

    public CollectionController(CollectionService collectionService) {
        this.collectionService = collectionService;
    }

    @GetMapping("/collection")
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
                            "Error " + ApiErrors.MISSING_PLAYER_ID_CODE + ": " + ApiErrors.MISSING_PLAYER_ID_MESSAGE + "<br />",
                    content = { @Content })
    })
    public ResponseEntity<GetCollectionResponse> getCollection(@RequestHeader(HttpHeader.PLAYER_ID) String playerId)
            throws ApiException {
        GetCollectionResponse response = collectionService.get(playerId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
