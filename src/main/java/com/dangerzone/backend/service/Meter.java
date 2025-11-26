package com.dangerzone.backend.service;

import com.dangerzone.backend.model.Report;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
public class Meter {

    private String crimeType;
    private double latitude;
    private double longitude;
    private int count;
    private int veracity;
    private double dangerLevel;
    private double score;
    private LocalDateTime dateTime;
    private final List<Report> reports = new ArrayList<>();

    public Meter(Report report) {
        this.crimeType = report.getCrimeType();
        this.latitude = report.getLatitude();
        this.longitude = report.getLongitude();
        this.dateTime = report.getHorario();
        this.count = 1;
        this.veracity = 1;
        this.dangerLevel = getDangerLevelFromType(report.getCrimeType());
        this.score = calculateScore();
        reports.add(report);
    }

    public void addReport(Report report) {
        reports.add(report);
        this.count++;
        this.veracity++;
        this.latitude = reports.stream().mapToDouble(Report::getLatitude).average().orElse(latitude);
        this.longitude = reports.stream().mapToDouble(Report::getLongitude).average().orElse(longitude);
        this.score = calculateScore();
    }

    private double calculateScore() {
        return (veracity * 1.2) + dangerLevel;
    }

    private double getDangerLevelFromType(String crimeType) {
        switch (crimeType.toLowerCase()) {
            case "homicídio":
                return 5.0;
            case "roubo":
                return 4.0;
            case "assalto":
                return 3.5;
            case "furto":
                return 2.0;
            case "vandalismo":
                return 1.0;
            default:
                return 1.0;
        }
    }
}
