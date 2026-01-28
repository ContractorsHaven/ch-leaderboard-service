package org.binary.scripting.chleaderboardservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Response containing a player's rank information")
public class PlayerRankResponse {

    @Schema(description = "The unique identifier of the game", example = "550e8400-e29b-41d4-a716-446655440000")
    private UUID gameId;

    @Schema(description = "The unique identifier of the player", example = "player-123")
    private String playerId;

    @Schema(description = "The player's position in the leaderboard (1-based)", example = "5")
    private Long rank;

    @Schema(description = "The player's score", example = "12500.0")
    private Double score;

    @Schema(description = "Total number of players in the leaderboard", example = "1000")
    private Long totalPlayers;

}
