package com.cris959.Vacunar_te.repository;

import com.cris959.Vacunar_te.model.Vacuna;
import com.cris959.Vacunar_te.model.enums.EstadoVacuna;
import jakarta.persistence.QueryHint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface VacunaRepository extends JpaRepository<Vacuna, Integer> {

    // Recupera vacunas filtrando exclusivamente por su estado actual
    List<Vacuna> findByEstado(EstadoVacuna estado);

    // Recupera el objeto completo perteneciente al ultimo lote de vacunas registrado en el sistema
    @Query("SELECT v FROM Vacuna v WHERE v.nroSerieDosis = (SELECT MAX(v2.nroSerieDosis) FROM Vacuna v2)")
    Optional<Vacuna> findUltimoLoteRegistrado();

    // Verifica la existencia previa de un numero de serie para evitar duplicados en la carga
    boolean existsByNroSerieDosis(String nroSerie);

    @Modifying
    @Transactional
    @Query("UPDATE Vacuna v SET v.estado = com.cris959.Vacunar_te.model.enums.EstadoVacuna.VENCIDA " +
            "WHERE v.estado = com.cris959.Vacunar_te.model.enums.EstadoVacuna.DISPONIBLE " +
            "AND v.fechaCaduca < CURRENT_DATE")
    // ESTAS DOS LINEAS SON LA CLAVE:
    @QueryHints(@QueryHint(name = "jakarta.persistence.cache.retrieveMode", value = "BYPASS"))
    int actualizarEstadoVencidas();
}
