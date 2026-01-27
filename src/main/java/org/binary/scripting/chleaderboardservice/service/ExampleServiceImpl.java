package org.binary.scripting.chleaderboardservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.binary.scripting.chleaderboardservice.dto.ExampleRequest;
import org.binary.scripting.chleaderboardservice.dto.ChLeaderboardServiceEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.Instant;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExampleServiceImpl implements ExampleService {

    private final KafkaTemplate<String, ChLeaderboardServiceEvent> kafkaTemplate;

    @Value("${app.kafka.topic.leaderboard}")
    private String primaryTopic;

    @Override
    public Mono<ChLeaderboardServiceEvent> processRequest(ExampleRequest request) {
        log.debug("Processing request: {}", request);
        ChLeaderboardServiceEvent event = ChLeaderboardServiceEvent.builder()
                            .eventId(UUID.randomUUID())
                            .resourceId(request.getId())
                            .action("created")
                            .data(request.getData())
                            .timestamp(Instant.now())
                            .build();

        log.info("Publishing event: {} for resource: {}", event.getEventId(), event.getResourceId());
        return Mono.fromFuture(kafkaTemplate.send(primaryTopic, event.getResourceId().toString(), event))
                            .subscribeOn(Schedulers.boundedElastic())
                            .doOnSuccess(result -> log.debug("Event published successfully: {}", event.getEventId()))
                            .doOnError(error -> log.error("Failed to publish event: {}", event.getEventId(), error))
                            .thenReturn(event);
    }
}
