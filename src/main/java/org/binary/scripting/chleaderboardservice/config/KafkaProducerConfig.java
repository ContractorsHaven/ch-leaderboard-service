package org.binary.scripting.chleaderboardservice.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaProducerConfig {

    @Value("${app.kafka.topic.leaderboard}")
    private String primaryTopic;

    @Bean
    public NewTopic primaryTopic() {
        return TopicBuilder.name(primaryTopic)
                .partitions(3)
                .replicas(1)
                .build();
    }
}
