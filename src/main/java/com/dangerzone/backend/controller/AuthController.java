package com.dangerzone.backend.controller;

import com.dangerzone.backend.dto.AuthRequest;
import com.dangerzone.backend.dto.TokenResponse;
import com.dangerzone.backend.dto.UserProfileResponse;
import com.dangerzone.backend.dto.UserReportResponse;
import com.dangerzone.backend.dto.RegisterRequest;
import com.dangerzone.backend.dto.ChangeDataRequest;
import com.dangerzone.backend.model.Report;
import com.dangerzone.backend.model.User;
import com.dangerzone.backend.service.UserService;
import com.dangerzone.backend.service.ReportService;
import com.dangerzone.backend.security.JwtUtil;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;
    private final ReportService reportService;
    private final JwtUtil jwtUtil;

    public AuthController(UserService userService, ReportService reportService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.reportService = reportService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest authRequest) {
        User user = userService.findByEmail(authRequest.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!new BCryptPasswordEncoder().matches(authRequest.getPassword(), user.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }

        String token = jwtUtil.generateToken(user.getId());
        return ResponseEntity.ok(new TokenResponse(token));
    }


    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {

        try {
            // Agora usamos o método adequado do UserService
            User user = userService.register(
                request.getFullName(),
                request.getEmail(),
                request.getPassword()
            );

            // Gera token para o novo usuário
            String token = jwtUtil.generateToken(user.getId());

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(new TokenResponse(token));

        } catch (IllegalArgumentException e) {
            // Caso e-mail já exista, ou register lance erro
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(e.getMessage());
        }
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

        User user = userService.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // PEGAR REPORTS DO USUÁRIO
        List<Report> reports = reportService.getReportsByUser(user);

        List<UserReportResponse> reportResponses = reports.stream().map(r -> 
            new UserReportResponse(
                r.getId(),
                r.getCrimeType(),
                r.getLatitude(),
                r.getLongitude(),
                r.getPais(),
                r.getCidade(),
                r.getBairro(),
                r.getEndereco(),
                r.getCep(),
                r.getDescricao(),
                r.getData() != null ? r.getData().toString() : null,
                r.getHorario() != null ? r.getHorario().toString() : null,
                r.isAnonymous()
            )
        ).toList();

        UserProfileResponse response = new UserProfileResponse(
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                reportResponses
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
        User user = userService.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Verifica senha obrigatória
        if (request.getPassword() == null ||
            !userService.checkPassword(request.getPassword(), user.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid password");
        }

        boolean updated = false;

        // Atualiza apenas o que foi enviado
        if (request.getNewEmail() != null && !request.getNewEmail().isBlank()) {
            if (userService.findByEmail(request.getNewEmail()).isPresent()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Email already in use");
            }
            user.setEmail(request.getNewEmail());
            updated = true;
        }

        if (request.getNewFullName() != null && !request.getNewFullName().isBlank()) {
            user.setFullName(request.getNewFullName());
            updated = true;
        }

        if (!updated) {
            return ResponseEntity.badRequest().body("No valid fields to update");
        }

        userService.change(user);

        // Gera novo token (se email mudou)
        String newToken = jwtUtil.generateToken(user.getId());
        return ResponseEntity.ok(new TokenResponse(newToken));
    }
}
