package com.k9t.scoreknight.service.cricket;

import com.k9t.scoreknight.client.CricApiClient;
import com.k9t.scoreknight.dto.cricket.CricketMatchCardDto;
import lombok.RequiredArgsConstructor;

import java.util.List;

@org.springframework.stereotype.Service
@RequiredArgsConstructor
public class ServiceImpl implements Service {

    private final CricApiClient cricApiClient;

    @Override
    public List<CricketMatchCardDto> getHomeMatches() {

        String response = cricApiClient.getCurrentMatches();

        System.out.println(response);

        return List.of();
    }
}