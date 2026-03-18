package com.cris959.Vacunar_te.repository;

import com.cris959.Vacunar_te.model.Vacuna;
import com.cris959.Vacunar_te.model.enums.EstadoVacuna;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface VacunaRepository extends JpaRepository<Vacuna, Integer> {

    // Para el control de stock en los centros
    List<Vacuna> findByEstado(EstadoVacuna estado);

    // Para encontrar vacunas que vencen pronto y darles prioridad
    List<Vacuna> findByEstadoAndFechaCaducaBefore(EstadoVacuna estado, LocalDate fecha);

    // Contar cuantas vacunas quedan de una marca especifica
    long countByMarcaAndEstado(String marca, EstadoVacuna estado);
}
