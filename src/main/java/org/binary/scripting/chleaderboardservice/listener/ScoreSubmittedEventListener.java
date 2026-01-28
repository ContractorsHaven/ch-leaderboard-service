package org.binary.scripting.chleaderboardservice.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.binary.scripting.chleaderboardservice.dto.ScoreSubmittedEvent;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ScoreSubmittedEventListener {

    private static final String LEADERBOARD_KEY_PREFIX = "leaderboard:game:";

    private final ReactiveRedisTemplate<String, String> redisTemplate;

    @KafkaListener(topics = "${app.kafka.topic.score-submitted}", groupId = "${spring.kafka.consumer.group-id}")
    public void handleScoreSubmitted(ScoreSubmittedEvent event) {
        log.info("Received score submitted event: eventId={}, gameId={}, playerId={}, score={}",
                event.getEventId(), event.getGameId(), event.getPlayerId(), event.getScore());

        String leaderboardKey = LEADERBOARD_KEY_PREFIX + event.getGameId();

        redisTemplate.opsForZSet()
                .add(leaderboardKey, event.getPlayerId(), event.getScore().doubleValue())
                .doOnSuccess(added -> {
                    if (Boolean.TRUE.equals(added)) {
                        log.debug("Added new score for player {} to leaderboard {}", event.getPlayerId(), leaderboardKey);
                    } else {
                        log.debug("Updated score for player {} in leaderboard {}", event.getPlayerId(), leaderboardKey);
                    }
                })
                .doOnError(error -> log.error("Failed to update leaderboard for event {}: {}",
                        event.getEventId(), error.getMessage()))
                .subscribe();
    }
}