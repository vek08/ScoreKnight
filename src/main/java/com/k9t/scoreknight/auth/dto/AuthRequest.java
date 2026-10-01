package com.k9t.scoreknight.auth.dto;
import lombok.Data;
@Data
public class AuthRequest {
    private String email;
    private String password;
}
