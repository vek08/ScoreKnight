package com.k9t.scoreknight.football.client;

import com.k9t.scoreknight.football.dto.api.ApiFootballResponse;
import com.k9t.scoreknight.football.dto.api.ApiFixtureResponse;
import com.k9t.scoreknight.football.dto.api.ApiEventResponse;
import com.k9t.scoreknight.football.dto.api.ApiStandingResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class FootballApiClient {

    private final RestClient restClient;
    private final String apiKey;

    public FootballApiClient(@Value("${football.api.key:dummy}") String apiKey) {
        this.apiKey = apiKey;
        this.restClient = RestClient.builder()
                .baseUrl("https://v3.football.api-sports.io")
                .defaultHeader("x-apisports-key", apiKey)
                .build();
    }

    public ApiFootballResponse<ApiFixtureResponse> getLiveFixtures() {
        return restClient.get()
                .uri("/fixtures?live=all")
                .retrieve()
                .body(new ParameterizedTypeReference<ApiFootballResponse<ApiFixtureResponse>>() {});
    }

    public ApiFootballResponse<ApiFixtureResponse> getRecentFixtures() {
        return restClient.get()
                .uri("/fixtures?last=15")
                .retrieve()
                .body(new ParameterizedTypeReference<ApiFootballResponse<ApiFixtureResponse>>() {});
    }

    public ApiFootballResponse<ApiFixtureResponse> getFixturesByDate(String date) {
        return restClient.get()
                .uri("/fixtures?date=" + date)
                .retrieve()
                .body(new ParameterizedTypeReference<ApiFootballResponse<ApiFixtureResponse>>() {});
    }
    
    public ApiFootballResponse<ApiFixtureResponse> getFixtureById(String fixtureId) {
        return restClient.get()
                .uri("/fixtures?id=" + fixtureId)
                .retrieve()
                .body(new ParameterizedTypeReference<ApiFootballResponse<ApiFixtureResponse>>() {});
    }

    public ApiFootballResponse<ApiEventResponse> getFixtureEvents(String fixtureId) {
        return restClient.get()
                .uri("/fixtures/events?fixture=" + fixtureId)
                .retrieve()
                .body(new ParameterizedTypeReference<ApiFootballResponse<ApiEventResponse>>() {});
    }
    
    public ApiFootballResponse<ApiStandingResponse> getStandings(String league, String season) {
        return restClient.get()
                .uri("/standings?league=" + league + "&season=" + season)
                .retrieve()
                .body(new ParameterizedTypeReference<ApiFootballResponse<ApiStandingResponse>>() {});
    }
}

