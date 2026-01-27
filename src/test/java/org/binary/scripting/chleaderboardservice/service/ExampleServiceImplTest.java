package org.binary.scripting.chleaderboardservice.service;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.binary.scripting.chleaderboardservice.dto.ExampleRequest;
import org.binary.scripting.chleaderboardservice.dto.ChLeaderboardServiceEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import reactor.test.StepVerifier;

import java.lang.reflect.Field;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ExampleServiceImplTest {

    @Mock
    private KafkaTemplate<String, ChLeaderboardServiceEvent> kafkaTemplate;

    private ExampleServiceImpl exampleService;

    private static final String TOPIC = "leaderboard";

    @BeforeEach
    void setUp() throws Exception {
        exampleService = new ExampleServiceImpl(kafkaTemplate);
        Field topicField = ExampleServiceImpl.class.getDeclaredField("primaryTopic");
        topicField.setAccessible(true);
        topicField.set(exampleService, TOPIC);
    }

    @Test
    void processRequest_success() {
        UUID resourceId = UUID.randomUUID();
        String name = "test-name";
        String data = "test-data";

        ExampleRequest request = ExampleRequest.builder()
                .id(resourceId)
                .name(name)
                .data(data)
                .build();

        SendResult<String, ChLeaderboardServiceEvent> sendResult = new SendResult<>(
                new ProducerRecord<>(TOPIC, resourceId.toString(), null),
                new RecordMetadata(null, 0, 0, 0, 0, 0)
        );

        when(kafkaTemplate.send(eq(TOPIC), eq(resourceId.toString()), any(ChLeaderboardServiceEvent.class)))
                .thenReturn(CompletableFuture.completedFuture(sendResult));

        StepVerifier.create(exampleService.processRequest(request))
                .assertNext(event -> {
                    assertThat(event.getEventId()).isNotNull();
                    assertThat(event.getResourceId()).isEqualTo(resourceId);
                    assertThat(event.getData()).isEqualTo(data);
                    assertThat(event.getTimestamp()).isNotNull();
                })
                .verifyComplete();
    }

    @Test
    void processRequest_kafkaFailure() {
        UUID resourceId = UUID.randomUUID();

        ExampleRequest request = ExampleRequest.builder()
                .id(resourceId)
                .name("test-name")
                .data("test-data")
                .build();

        CompletableFuture<SendResult<String, ChLeaderboardServiceEvent>> failedFuture = new CompletableFuture<>();
        failedFuture.completeExceptionally(new RuntimeException("Kafka unavailable"));

        when(kafkaTemplate.send(eq(TOPIC), eq(resourceId.toString()), any(ChLeaderboardServiceEvent.class)))
                .thenReturn(failedFuture);

        StepVerifier.create(exampleService.processRequest(request))
                .expectErrorMatches(throwable ->
                        throwable instanceof RuntimeException &&
                        throwable.getMessage().contains("Kafka unavailable"))
                .verify();
    }
}
