package com.k9t.scoreknight.dto.cricket;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CricketMatchCardDto {
    private String matchId;

    private String teamA;
    private String teamB;

    private String teamALogo;
    private String teamBLogo;

    private String status;

    private String score;

    private String venue;

    private String matchDate;
}
