package com.k9t.scoreknight.dto.cricket;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MatchPlayerDto {
    private String id;
    private String name;
    private String role;
    private String battingStyle;
    private String bowlingStyle;
    private String country;
    private String playerImg;
}

