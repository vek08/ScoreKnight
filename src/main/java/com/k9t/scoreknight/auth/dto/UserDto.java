package com.k9t.scoreknight.auth.dto;
import lombok.Builder;
import lombok.Data;
@Data @Builder
public class UserDto {
    private Long id;
    private String name;
    private String email;
    private String profileImage;
    private String authProvider;
}
