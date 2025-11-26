package com.dangerzone.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserReportResponse {
    private Long id;
    private String crimeType;
    private Double latitude;
    private Double longitude;
    private String cidade;
    private String bairro;
    private String endereco;
    private String descricao;
    private String data;      // formatado como string
    private String horario;   // formatado como string
    private boolean anonymous;
}
