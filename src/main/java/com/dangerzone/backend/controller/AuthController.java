package com.dangerzone.backend.controller;

import com.dangerzone.backend.dto.AuthRequest;
import com.dangerzone.backend.dto.TokenResponse;
import com.dangerzone.backend.dto.UserProfileResponse;
import com.dangerzone.backend.dto.RegisterRequest;
import com.dangerzone.backend.dto.ChangeDataRequest;
import com.dangerzone.backend.model.User;
import com.dangerzone.backend.repository.UserRepository;
import com.dangerzone.backend.security.JwtUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    public AuthController(UserRepository userRepository, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest authRequest) {
        User user = userRepository.findByEmail(authRequest.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!new BCryptPasswordEncoder().matches(authRequest.getPassword(), user.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }

        String token = jwtUtil.generateToken(user.getId());
        return ResponseEntity.ok(new TokenResponse(token));
    }


    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        // Verifica se já existe um usuário com o mesmo email
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Email already in use");
        }

        // Cria o usuário
        User user = new User();
        user.setFullName(request.getFullName());
        user.setNickname(request.getNickname());
        user.setEmail(request.getEmail());
        user.setPassword(!new BCryptPasswordEncoder().encode(request.getPassword()));

        userRepository.save(user);

        // Gera token automaticamente após o registro
        String token = jwtUtil.generateToken(user.getId());

        return ResponseEntity.status(HttpStatus.CREATED).body(new TokenResponse(token));
    }


    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(@RequestHeader("Authorization") String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token missing or invalid");
        }

        String token = authHeader.substring(7);
        if (!jwtUtil.isTokenValid(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
        }

        Long userId = jwtUtil.extractUserId(token);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        UserProfileResponse response = new UserProfileResponse(
                user.getId(),
                user.getFullName(),
                user.getNickname(),
                user.getEmail()
        );

        return ResponseEntity.ok(response);
    }

    @PutMapping("/change-data")
    public ResponseEntity<?> changeData(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody ChangeDataRequest request
    ) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token missing or invalid");
        }

        String token = authHeader.substring(7);
        if (!jwtUtil.isTokenValid(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
        }

        Long userId = jwtUtil.extractUserId(token);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Verifica senha obrigatória
        if (request.getPassword() == null ||
            !passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid password");
        }

        boolean updated = false;

        // Atualiza apenas o que foi enviado
        if (request.getNewEmail() != null && !request.getNewEmail().isBlank()) {
            if (userRepository.findByEmail(request.getNewEmail()).isPresent()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Email already in use");
            }
            user.setEmail(request.getNewEmail());
            updated = true;
        }

        if (request.getNewNickname() != null && !request.getNewNickname().isBlank()) {
            user.setNickname(request.getNewNickname());
            updated = true;
        }

        if (request.getNewPhone() != null && !request.getNewPhone().isBlank()) {
            user.setPhone(request.getNewPhone());
            updated = true;
        }

        if (!updated) {
            return ResponseEntity.badRequest().body("No valid fields to update");
        }

        userRepository.save(user);

        // Gera novo token (se email mudou)
        String newToken = jwtUtil.generateToken(user.getId());
        return ResponseEntity.ok(new TokenResponse(newToken));
    }
}
