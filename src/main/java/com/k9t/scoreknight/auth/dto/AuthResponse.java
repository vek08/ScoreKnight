package com.k9t.scoreknight.auth.dto;
import lombok.AllArgsConstructor;
import lombok.Data;
@Data @AllArgsConstructor
public class AuthResponse {
    private String token;
    private UserDto user;
}
