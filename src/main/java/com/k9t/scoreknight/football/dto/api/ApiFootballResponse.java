package com.k9t.scoreknight.football.dto.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ApiFootballResponse<T> {
    private List<T> response;
    
    public List<T> getResponse() { return response; }
    public void setResponse(List<T> response) { this.response = response; }
}
