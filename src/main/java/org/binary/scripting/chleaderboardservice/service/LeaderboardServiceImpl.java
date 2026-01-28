package org.binary.scripting.chleaderboardservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.binary.scripting.chleaderboardservice.dto.LeaderboardEntry;
import org.binary.scripting.chleaderboardservice.dto.PlayerRankResponse;
import org.springframework.data.domain.Range;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.ReactiveZSetOperations;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
@Service
@RequiredArgsConstructor
public class LeaderboardServiceImpl implements LeaderboardService {

    private static final String LEADERBOARD_KEY_PREFIX = "leaderboard:game:";

    private final ReactiveRedisTemplate<String, String> redisTemplate;

    @Override
    public Flux<LeaderboardEntry> getTopPlayers(UUID gameId, int limit) {
        String leaderboardKey = LEADERBOARD_KEY_PREFIX + gameId;
        log.debug("Fetching top {} players for game {}", limit, gameId);

        AtomicLong rank = new AtomicLong(1);
        return redisTemplate.opsForZSet()
                .reverseRangeWithScores(leaderboardKey, Range.closed(0L, (long) limit - 1))
                .map(tuple -> LeaderboardEntry.builder()
                        .rank(rank.getAndIncrement())
                        .playerId(tuple.getValue())
                        .score(tuple.getScore())
                        .build());
    }

    @Override
    public Mono<PlayerRankResponse> getPlayerRank(UUID gameId, String playerId) {
        String leaderboardKey = LEADERBOARD_KEY_PREFIX + gameId;
        log.debug("Fetching rank for player {} in game {}", playerId, gameId);

        ReactiveZSetOperations<String, String> zSetOps = redisTemplate.opsForZSet();

        Mono<Long> rankMono = zSetOps.reverseRank(leaderboardKey, playerId);
        Mono<Double> scoreMono = zSetOps.score(leaderboardKey, playerId);
        Mono<Long> totalMono = zSetOps.size(leaderboardKey);

        return Mono.zip(rankMono, scoreMono, totalMono)
                .map(tuple -> PlayerRankResponse.builder()
                        .gameId(gameId)
                        .playerId(playerId)
                        .rank(tuple.getT1() + 1)
                        .score(tuple.getT2())
                        .totalPlayers(tuple.getT3())
                        .build())
                .switchIfEmpty(Mono.empty());
    }

    @Override
    public Flux<LeaderboardEntry> getNearbyPlayers(UUID gameId, String playerId, int range) {
        String leaderboardKey = LEADERBOARD_KEY_PREFIX + gameId;
        log.debug("Fetching nearby players for {} in game {} with range {}", playerId, gameId, range);

        return redisTemplate.opsForZSet()
                .reverseRank(leaderboardKey, playerId)
                .flatMapMany(playerRank -> {
                    long start = Math.max(0, playerRank - range);
                    long end = playerRank + range;

                    AtomicLong rank = new AtomicLong(start + 1);
                    return redisTemplate.opsForZSet()
                            .reverseRangeWithScores(leaderboardKey, Range.closed(start, end))
                            .map(tuple -> LeaderboardEntry.builder()
                                    .rank(rank.getAndIncrement())
                                    .playerId(tuple.getValue())
                                    .score(tuple.getScore())
                                    .build());
                });
    }
}