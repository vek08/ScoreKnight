package com.k9t.scoreknight.service.cricket;

import com.k9t.scoreknight.client.CricApiClient;
import com.k9t.scoreknight.dto.cricket.CricketMatchCardDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ServiceImpl implements com.k9t.scoreknight.service.cricket.Service {

    private final CricApiClient cricApiClient;
    private final ObjectMapper objectMapper;

    @Override
    public List<CricketMatchCardDto> getHomeMatches() {

        try {

            // 1. Call CricAPI
            String response = cricApiClient.getCurrentMatches();

            // 2. Convert JSON String into JsonNode
            JsonNode root = objectMapper.readTree(response);

            // 3. Create result list
            List<CricketMatchCardDto> matches = new ArrayList<>();

            // 4. Get data array
            JsonNode data = root.get("data");

            if (data == null || !data.isArray()) {
                return matches;
            }

            // 5. Loop through every match
            for (JsonNode match : data) {

                CricketMatchCardDto dto = new CricketMatchCardDto();

                // -------------------------
                // Match ID
                // -------------------------
                if (match.has("id")) {
                    dto.setMatchId(
                            match.get("id").asString()
                    );
                }

                // -------------------------
                // Teams
                // -------------------------
                JsonNode teams = match.get("teams");

                if (teams != null
                        && teams.isArray()
                        && teams.size() >= 2) {

                    dto.setTeamA(
                            teams.get(0).asString()
                    );

                    dto.setTeamB(
                            teams.get(1).asString()
                    );
                }

                // -------------------------
                // Team Logos
                // -------------------------
                JsonNode teamInfo = match.get("teamInfo");

                if (teamInfo != null
                        && teamInfo.isArray()) {

                    // Team A Logo
                    if (!teamInfo.isEmpty()
                            && teamInfo.get(0).has("img")) {

                        dto.setTeamALogo(
                                teamInfo.get(0)
                                        .get("img")
                                        .asString()
                        );
                    }

                    // Team B Logo
                    if (teamInfo.size() >= 2
                            && teamInfo.get(1).has("img")) {

                        dto.setTeamBLogo(
                                teamInfo.get(1)
                                        .get("img")
                                        .asString()
                        );
                    }
                }

                // -------------------------
                // Status
                // -------------------------
                if (match.has("status")) {
                    dto.setStatus(
                            match.get("status").asString()
                    );
                }

                // -------------------------
                // Venue
                // -------------------------
                if (match.has("venue")) {
                    dto.setVenue(
                            match.get("venue").asString()
                    );
                }

                // -------------------------
                // Match Date
                // -------------------------
                if (match.has("date")) {
                    dto.setMatchDate(
                            match.get("date").asString()
                    );
                }

                // -------------------------
                // Score
                // -------------------------
                if (match.has("score")) {
                    dto.setScore(
                            match.get("score").toString()
                    );
                }

                // Add DTO to list
                matches.add(dto);
            }

            // 6. Return all matches
            return matches;

        } catch (Exception e) {

            throw new RuntimeException(
                    "Failed to parse cricket API response",
                    e
            );
        }
    }
}