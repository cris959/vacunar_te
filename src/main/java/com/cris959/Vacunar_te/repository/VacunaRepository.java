package com.cris959.Vacunar_te.repository;

import com.cris959.Vacunar_te.model.Vacuna;
import com.cris959.Vacunar_te.model.enums.EstadoVacuna;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface VacunaRepository extends JpaRepository<Vacuna, Integer> {

    // Recupera vacunas filtrando exclusivamente por su estado actual
    List<Vacuna> findByEstado(EstadoVacuna estado);

    // Busca vacunas de un estado especifico con fecha de vencimiento previa a una fecha dada
    List<Vacuna> findByEstadoAndFechaCaducaBefore(EstadoVacuna estado, LocalDate fecha);

    // Cuenta existencias activas filtrando por el nombre comercial del laboratorio
    long countByLaboratorioNomComercialAndEstado(String nombre, EstadoVacuna estado);

    // Obtiene vacunas aptas para uso cuya fecha de caducidad es posterior a la fecha actual
    List<Vacuna> findByEstadoAndFechaCaducaAfter(EstadoVacuna estado, LocalDate hoy);

    // Lista todas las vacunas que no coincidan con un estado particular, util para exclusion logica
    List<Vacuna> findByEstadoNot(EstadoVacuna estado);

    // Obtiene el valor maximo registrado en la secuencia de numeros de serie de dosis
    @Query("SELECT MAX(v.nroSerieDosis) FROM Vacuna v")
    Integer findMaxNroSerieDosis();

    // Recupera el objeto completo perteneciente al ultimo lote de vacunas registrado en el sistema
    @Query("SELECT v FROM Vacuna v WHERE v.nroSerieDosis = (SELECT MAX(v2.nroSerieDosis) FROM Vacuna v2)")
    Optional<Vacuna> findUltimoLoteRegistrado();

    // Verifica la existencia previa de un numero de serie para evitar duplicados en la carga
    boolean existsByNroSerieDosis(String nroSerie);
}
