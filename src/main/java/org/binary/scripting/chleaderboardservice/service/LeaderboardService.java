package org.binary.scripting.chleaderboardservice.service;

import org.binary.scripting.chleaderboardservice.dto.LeaderboardEntry;
import org.binary.scripting.chleaderboardservice.dto.PlayerRankResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface LeaderboardService {

    Flux<LeaderboardEntry> getTopPlayers(UUID gameId, int limit);

    Mono<PlayerRankResponse> getPlayerRank(UUID gameId, String playerId);

    Flux<LeaderboardEntry> getNearbyPlayers(UUID gameId, String playerId, int range);

}