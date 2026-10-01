package com.k9t.scoreknight.auth.dto;
import lombok.Data;
@Data
public class RegisterRequest {
    private String name;
    private String email;
    private String password;
}
