package com.cris959.Vacunar_te.repository;

import com.cris959.Vacunar_te.model.CitaVacunacion;
import com.cris959.Vacunar_te.model.Ciudadano;
import com.cris959.Vacunar_te.model.enums.EstadoCita;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CitaVacunacionRepository extends JpaRepository<CitaVacunacion, Integer> {

    // 1. Obtiene el historial de citas de un ciudadano excluyendo un estado (ej: CANCELADA) y ordenando por fecha descendente
    List<CitaVacunacion> findByCiudadanoAndEstadoNotOrderByFechaHoraCitaDesc(
            Ciudadano ciudadano, EstadoCita estado
    );

    // 2. Recupera la totalidad de citas registradas para un centro de vacunacion especifico
    List<CitaVacunacion> findByCentroVacunacion(String centro);

    // 3. Filtra las citas de un centro de vacunacion segun su estado actual (ej: PENDIENTE, COMPLETADA)
    List<CitaVacunacion> findByCentroVacunacionAndEstado(String centro, EstadoCita estado);
}
