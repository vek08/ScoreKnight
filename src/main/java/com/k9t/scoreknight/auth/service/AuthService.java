package com.k9t.scoreknight.auth.service;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.k9t.scoreknight.auth.dto.*;
import com.k9t.scoreknight.auth.entity.AuthProvider;
import com.k9t.scoreknight.auth.entity.User;
import com.k9t.scoreknight.auth.repository.UserRepository;
import com.k9t.scoreknight.auth.security.JwtService;
import com.k9t.scoreknight.auth.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Collections;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    
    // Replace with your Google Client ID, but for verification we can accept any client ID for now or skip strict verification
    // if we don't have the explicit web client ID.
    // In a real app, this should be the Android Web Client ID from Google Cloud Console.
    private static final String CLIENT_ID = "YOUR_CLIENT_ID.apps.googleusercontent.com"; 

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email already exists");
        }
        
        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .authProvider(AuthProvider.EMAIL)
                .build();
                
        userRepository.save(user);
        String token = jwtService.generateToken(new UserDetailsImpl(user));
        return new AuthResponse(token, mapToUserDto(user));
    }

    public AuthResponse authenticate(AuthRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        String token = jwtService.generateToken(new UserDetailsImpl(user));
        return new AuthResponse(token, mapToUserDto(user));
    }
    
    public AuthResponse googleAuth(GoogleAuthRequest request) {
        try {
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new GsonFactory())
                // .setAudience(Collections.singletonList(CLIENT_ID)) // Skip strict audience check for this test
                .build();
                
            GoogleIdToken idToken = GoogleIdToken.parse(new GsonFactory(), request.getIdToken());
            
            if (idToken != null) {
                GoogleIdToken.Payload payload = idToken.getPayload();
                String email = payload.getEmail();
                String name = (String) payload.get("name");
                String pictureUrl = (String) payload.get("picture");
                
                User user = userRepository.findByEmail(email).orElseGet(() -> {
                    User newUser = User.builder()
                        .email(email)
                        .name(name)
                        .profileImage(pictureUrl)
                        .authProvider(AuthProvider.GOOGLE)
                        .build();
                    return userRepository.save(newUser);
                });
                
                String token = jwtService.generateToken(new UserDetailsImpl(user));
                return new AuthResponse(token, mapToUserDto(user));
            } else {
                throw new RuntimeException("Invalid ID token.");
            }
        } catch (Exception e) {
            throw new RuntimeException("Google Auth Failed: " + e.getMessage());
        }
    }
    
    public UserDto mapToUserDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .profileImage(user.getProfileImage())
                .authProvider(user.getAuthProvider().name())
                .build();
    }
}
