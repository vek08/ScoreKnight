package com.k9t.scoreknight.client;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class CricApiClient {

    private final RestClient restClient;

    @Value("${cric.api.key}")
    private String apiKey;

    public CricApiClient() {
        this.restClient = RestClient.builder()
                .baseUrl("https://api.cricapi.com/v1")
                .build();
    }

    public String getCurrentMatches() {

        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/currentMatches")
                        .queryParam("apikey", apiKey)
                        .queryParam("offset", 0)
                        .build())
                .retrieve()
                .body(String.class);
    }
}