package com.dangerzone.backend.controller;

import com.dangerzone.backend.model.Report;
import com.dangerzone.backend.model.User;
import com.dangerzone.backend.service.ReportService;
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

    private final ReportService reportService;
    private final JwtUtil jwtUtil;

    @PostMapping
    public ResponseEntity<?> createReport(
            @RequestHeader("Authorization") String tokenHeader,
            @RequestBody Report reportRequest
    ) {
        try {
            // Extrai token
            String token = tokenHeader.replace("Bearer ", "").trim();

            // Extrai userId do token
            Long userId = jwtUtil.extractUserId(token);

            // Cria um "User" apenas com o ID
            User user = new User();
            user.setId(userId);

            // Define valores padrão
            LocalDate data = (reportRequest.getData() != null)
                    ? reportRequest.getData()
                    : LocalDate.now();

            LocalDateTime horario = (reportRequest.getHorario() != null)
                    ? reportRequest.getHorario()
                    : LocalDateTime.now();

            boolean anonymous = Boolean.TRUE.equals(reportRequest.isAnonymous());

            // Chama o service
            Report saved = reportService.createReport(
                    user,
                    anonymous,
                    reportRequest.getCrimeType(),
                    reportRequest.getLatitude(),
                    reportRequest.getLongitude(),
                    reportRequest.getCep(),
                    reportRequest.getPais(),
                    reportRequest.getEstado(),
                    reportRequest.getCidade(),
                    reportRequest.getBairro(),
                    reportRequest.getEndereco(),
                    data,
                    horario,
                    reportRequest.getDescricao()
            );

            return ResponseEntity.ok(saved);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erro ao registrar report: " + e.getMessage());
        }
    }
}