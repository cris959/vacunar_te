package com.cris959.Vacunar_te.repository;

import com.cris959.Vacunar_te.model.CitaVacunacion;
import com.cris959.Vacunar_te.model.Ciudadano;
import com.cris959.Vacunar_te.model.enums.EstadoCita;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CitaVacunacionRepository extends JpaRepository<CitaVacunacion, Integer> {

    // Busca la ultima cita NO cancelada de un ciudadano para ver que dosis le toca
    List<CitaVacunacion> findByCiudadanoAndEstadoNotOrderByFechaHoraCitaDesc(
            Ciudadano ciudadano, EstadoCita estado
    );

    // Para las estadisticas de la Etapa 2: Citas por centro de vacunacion
    List<CitaVacunacion> findByCentroVacunacion(String centro);

    // Buscar citas por centro y por un estado especifico
    List<CitaVacunacion> findByCentroVacunacionAndEstado(String centro, EstadoCita estado);
}
