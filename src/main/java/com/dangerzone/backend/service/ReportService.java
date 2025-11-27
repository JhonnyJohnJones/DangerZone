package com.dangerzone.backend.service;

import com.dangerzone.backend.model.Report;
import com.dangerzone.backend.model.User;
import com.dangerzone.backend.repository.ReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final ReportRepository reportRepository;

    public Report createReport(User user, boolean anonimo, String crimeType,
                               Double latitude, Double longitude, String cep,
                               String pais, String estado, String cidade,
                               String bairro, String endereco,
                               LocalDate data, LocalDateTime horario,
                               String descricao) {

        Report report = new Report();
        report.setUser(user);
        report.setAnonimo(anonimo);
        report.setCrimeType(crimeType);
        report.setLatitude(latitude);
        report.setLongitude(longitude);
        report.setCep(cep);
        report.setPais(pais);
        report.setEstado(estado);
        report.setCidade(cidade);
        report.setBairro(bairro);
        report.setEndereco(endereco);
        report.setData(data);
        report.setHorario(horario);
        report.setDescricao(descricao);

        return reportRepository.save(report);
    }

    public List<Report> getReportsByUser(User user) {
        return reportRepository.findByUser(user);
    }

    public List<Report> getReportsNearLocation(Double latitude, Double longitude, Double radiusDegrees) {
        return reportRepository.findReportsNearLocation(latitude, longitude, radiusDegrees);
    }

    // ==========================================
    // Lógica do Heatmap
    // ==========================================

    public Map<String, Object> generateHeatmap(Double latitude, Double longitude, Double radiusDegrees) {
        List<Report> reports = reportRepository.findReportsNearLocation(latitude, longitude, radiusDegrees);
        List<Meter> consolidated = consolidateReports(reports);

        List<Map<String, Object>> points = consolidated.stream()
                .map(c -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("lat", c.getLatitude());
                    map.put("lng", c.getLongitude());
                    map.put("type", c.getCrimeType());
                    map.put("count", c.getCount());
                    map.put("veracity", c.getVeracity());
                    map.put("danger", c.getDangerLevel());
                    map.put("score", c.getScore());
                    return map;
                })
                .collect(Collectors.toList());

        return Map.of("points", points);
    }

    private List<Meter> consolidateReports(List<Report> reports) {
        List<Meter> groups = new ArrayList<>();

        for (Report report : reports) {
            boolean merged = false;

            for (Meter group : groups) {
                if (isSimilar(group, report)) {
                    group.addReport(report);
                    merged = true;
                    break;
                }
            }

            if (!merged) {
                groups.add(new Meter(report));
            }
        }

        return groups;
    }

    private boolean isSimilar(Meter group, Report report) {
        if (!group.getCrimeType().equalsIgnoreCase(report.getCrimeType())) return false;

        // ---- PROXIMIDADE EM GRAUS ----
        double diffLat = Math.abs(group.getLatitude() - report.getLatitude());
        double diffLng = Math.abs(group.getLongitude() - report.getLongitude());

        // 0.004 graus ≈ 400m
        if (diffLat > 0.004 || diffLng > 0.004) return false;

        // ---- PROXIMIDADE DE TEMPO ----
        if (group.getDateTime() != null && report.getHorario() != null) {
            long minutesDiff = Duration.between(
                    group.getDateTime(),
                    report.getHorario()
            ).toMinutes();

            if (Math.abs(minutesDiff) > 30) return false; // mais de 30 minutos
        }

        return true;
    }

}
