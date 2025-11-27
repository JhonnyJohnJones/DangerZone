package com.dangerzone.backend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "reports")
@Getter
@Setter
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Relacionamento com User (muitos relatórios para um usuário)
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private boolean anonimo;

    @Column(name = "crime_type", nullable = false)
    private String crimeType;

    // Coordenadas (latitude, longitude)
    @Column(nullable = true)
    private Double latitude;

    @Column(nullable = true)
    private Double longitude;

    @Column(length = 9) // Ex: "12345-678"
    private String cep;

    private String pais;
    private String estado;
    private String cidade;
    private String bairro;
    private String endereco;

    @Column(nullable = false)
    private LocalDate data;

    @Column(nullable = false)
    private LocalDateTime horario;

    @Column(columnDefinition = "TEXT")
    private String descricao;
}
