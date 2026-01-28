package org.binary.scripting.chleaderboardservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "A single entry in the leaderboard")
public class LeaderboardEntry {

    @Schema(description = "The player's position in the leaderboard (1-based)", example = "1")
    private Long rank;

    @Schema(description = "The unique identifier of the player", example = "player-123")
    private String playerId;

    @Schema(description = "The player's score", example = "15000.0")
    private Double score;

}