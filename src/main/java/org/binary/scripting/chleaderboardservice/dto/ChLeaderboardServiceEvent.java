package org.binary.scripting.chleaderboardservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChLeaderboardServiceEvent {

    private UUID eventId;

    private UUID resourceId;

    private String action;

    private String data;

    private Instant timestamp;

}
