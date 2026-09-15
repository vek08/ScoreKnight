package com.k9t.scoreknight.dto.cricket;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MatchSquadDto {
    private String teamName;
    private String img;
    private List<MatchPlayerDto> players;
}

