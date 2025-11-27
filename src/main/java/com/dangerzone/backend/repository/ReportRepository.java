package com.dangerzone.backend.repository;

import com.dangerzone.backend.model.Report;
import com.dangerzone.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query; 
import org.springframework.data.repository.query.Param; 

import java.util.List;

public interface ReportRepository extends JpaRepository<Report, Long> {

    // Busca todos os relatórios de um usuário específico
    List<Report> findByUser(User user);

    // Caso queira buscar por tipo de crime
    List<Report> findByCrimeType(String crimeType);

    // Caso queira buscar apenas relatórios anônimos
    List<Report> findByAnonymousTrue();

    // Busca relatórios próximos a uma localização (dentro de um raio em km)
    @Query("""
        SELECT r FROM Report r
        WHERE 
            r.latitude BETWEEN (:latitude - :radius) AND (:latitude + :radius)
            AND 
            r.longitude BETWEEN (:longitude - :radius) AND (:longitude + :radius)
        """)
    List<Report> findReportsNearLocation(
        @Param("latitude") Double latitude,
        @Param("longitude") Double longitude,
        @Param("radius") Double radiusDegrees
    );

}
