package com.dangerzone.backend.controller;

import com.dangerzone.backend.model.Report;
import com.dangerzone.backend.model.User;
import com.dangerzone.backend.repository.ReportRepository;
import com.dangerzone.backend.repository.UserRepository;
import com.dangerzone.backend.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportRepository reportRepository;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    @PostMapping
    public ResponseEntity<?> createReport(
            @RequestHeader("Authorization") String tokenHeader,
            @RequestBody Report report
    ) {
        try {
            // Extrai token do header (sem o prefixo Bearer)
            String token = tokenHeader.replace("Bearer ", "").trim();

            // Extrai userId
            Long userId = jwtUtil.extractUserId(token);
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("Usuário não encontrado."));

            // Associa o usuário
            report.setUser(user);

            // Se data ou horário não vierem, define agora
            if (report.getData() == null) {
                report.setData(LocalDate.now());
            }
            if (report.getHorario() == null) {
                report.setHorario(LocalDateTime.now());
            }

            // Salva no banco
            Report saved = reportRepository.save(report);
            return ResponseEntity.ok(saved);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erro ao registrar report: " + e.getMessage());
        }
    }
    
    @GetMapping("/me")
    public ResponseEntity<?> getUserReports(
            @RequestHeader("Authorization") String tokenHeader
    ) {
        try {
            String token = tokenHeader.replace("Bearer ", "").trim();
            Long userId = jwtUtil.extractUserId(token);

            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("Usuário não encontrado."));

            List<Report> reports = reportRepository.findByUser(user);
            return ResponseEntity.ok(reports);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erro ao buscar histórico: " + e.getMessage());
        }
    }
}