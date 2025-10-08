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

    public Report createReport(User user, boolean anonymous, String crimeType,
                               Double latitude, Double longitude, String cep,
                               String pais, String estado, String cidade,
                               String bairro, String endereco,
                               LocalDate data, LocalDateTime horario,
                               String descricao) {

        Report report = new Report();
        report.setUser(user);
        report.setAnonymous(anonymous);
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

    public List<Report> getReportsNearLocation(Double latitude, Double longitude, Double radiusKm) {
        return reportRepository.findReportsNearLocation(latitude, longitude, radiusKm);
    }

    public List<Report> getAllReports() {
        return reportRepository.findAll();
    }

    // ==========================================
    // Lógica do Heatmap
    // ==========================================

    public Map<String, Object> generateHeatmap(Double latitude, Double longitude, Double radiusKm) {
        List<Report> reports = reportRepository.findReportsNearLocation(latitude, longitude, radiusKm);
        List<ConsolidatedCrime> consolidated = consolidateReports(reports);

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

    private List<ConsolidatedCrime> consolidateReports(List<Report> reports) {
        List<ConsolidatedCrime> groups = new ArrayList<>();

        for (Report report : reports) {
            boolean merged = false;

            for (ConsolidatedCrime group : groups) {
                if (isSimilar(group, report)) {
                    group.addReport(report);
                    merged = true;
                    break;
                }
            }

            if (!merged) {
                groups.add(new ConsolidatedCrime(report));
            }
        }

        return groups;
    }

    private boolean isSimilar(ConsolidatedCrime group, Report report) {
        if (!group.getCrimeType().equalsIgnoreCase(report.getCrimeType())) return false;

        double distance = haversine(
                group.getLatitude(), group.getLongitude(),
                report.getLatitude(), report.getLongitude()
        );

        if (distance > 0.2) return false; // > 200 metros

        if (group.getDateTime() != null && report.getHorario() != null) {
            long minutesDiff = Duration.between(
                    group.getDateTime(),
                    report.getHorario()
            ).toMinutes();

            if (Math.abs(minutesDiff) > 30) return false; // mais de 30 minutos de diferença
        }

        return true;
    }

    private double haversine(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }
}
