package com.cris959.Vacunar_te.repository;

import com.cris959.Vacunar_te.model.Ciudadano;
import com.cris959.Vacunar_te.model.enums.AmbitoTrabajo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CiudadanoRepository extends JpaRepository<Ciudadano, Integer> {

    // Procedimiento personalizado para las estadisticas que pide el instructivo
    List<Ciudadano> findByAmbitoTrabajo(AmbitoTrabajo ambito);

    // Buscar por nombre si necesitas filtrar en la GUI
    List<Ciudadano> findByNombreCompletoContainingIgnoreCase(String nombre);

    boolean existsByEmail(String email);

    // Tambien es util tener este por si necesitas recuperar al ciudadano por email luego
    Ciudadano findByEmail(String email);
}
