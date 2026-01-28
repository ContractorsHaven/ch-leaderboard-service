package org.binary.scripting.chleaderboardservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.binary.scripting.chleaderboardservice.dto.LeaderboardEntry;
import org.binary.scripting.chleaderboardservice.dto.PlayerRankResponse;
import org.binary.scripting.chleaderboardservice.service.LeaderboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/v1/leaderboard")
@RequiredArgsConstructor
@Tag(name = "Leaderboard", description = "Leaderboard query operations")
public class LeaderboardController {

    private static final int DEFAULT_TOP_LIMIT = 10;
    private static final int MAX_TOP_LIMIT = 100;
    private static final int DEFAULT_NEARBY_RANGE = 5;
    private static final int MAX_NEARBY_RANGE = 50;

    private final LeaderboardService leaderboardService;

    @Operation(
            summary = "Get top players",
            description = "Retrieves the top N players for a specific game, ordered by score descending"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved top players",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = LeaderboardEntry.class)))
            )
    })
    @GetMapping("/{gameId}/top")
    public Flux<LeaderboardEntry> getTopPlayers(
            @Parameter(description = "The unique identifier of the game", required = true)
            @PathVariable UUID gameId,
            @Parameter(description = "Number of top players to retrieve (1-100, default 10)")
            @RequestParam(defaultValue = "10") int limit) {
        log.info("Request for top {} players for game {}", limit, gameId);
        int effectiveLimit = Math.min(Math.max(1, limit), MAX_TOP_LIMIT);
        return leaderboardService.getTopPlayers(gameId, effectiveLimit);
    }

    @Operation(
            summary = "Get player rank",
            description = "Retrieves the rank and score of a specific player in a game"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved player rank",
                    content = @Content(schema = @Schema(implementation = PlayerRankResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Player not found in the leaderboard",
                    content = @Content
            )
    })
    @GetMapping("/{gameId}/player/{playerId}/rank")
    public Mono<ResponseEntity<PlayerRankResponse>> getPlayerRank(
            @Parameter(description = "The unique identifier of the game", required = true)
            @PathVariable UUID gameId,
            @Parameter(description = "The unique identifier of the player", required = true)
            @PathVariable String playerId) {
        log.info("Request for rank of player {} in game {}", playerId, gameId);
        return leaderboardService.getPlayerRank(gameId, playerId)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @Operation(
            summary = "Get nearby players",
            description = "Retrieves players ranked around a specific player (above and below)"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved nearby players",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = LeaderboardEntry.class)))
            )
    })
    @GetMapping("/{gameId}/player/{playerId}/nearby")
    public Flux<LeaderboardEntry> getNearbyPlayers(
            @Parameter(description = "The unique identifier of the game", required = true)
            @PathVariable UUID gameId,
            @Parameter(description = "The unique identifier of the player", required = true)
            @PathVariable String playerId,
            @Parameter(description = "Number of players above and below to retrieve (1-50, default 5)")
            @RequestParam(defaultValue = "5") int range) {
        log.info("Request for nearby players around {} in game {} with range {}", playerId, gameId, range);
        int effectiveRange = Math.min(Math.max(1, range), MAX_NEARBY_RANGE);
        return leaderboardService.getNearbyPlayers(gameId, playerId, effectiveRange);
    }
}
