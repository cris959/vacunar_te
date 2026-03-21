package com.cris959.Vacunar_te.repository;

import com.cris959.Vacunar_te.model.Ciudadano;
import com.cris959.Vacunar_te.model.enums.AmbitoTrabajo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CiudadanoRepository extends JpaRepository<Ciudadano, Integer> {

    // 1. Recupera ciudadanos segun su categoria laboral para reportes estadisticos
    List<Ciudadano> findByAmbitoTrabajo(AmbitoTrabajo ambito);

    // 2. Filtro de busqueda por coincidencia parcial en el nombre (ignora mayusculas)
    List<Ciudadano> findByNombreCompletoContainingIgnoreCase(String nombre);

    // 3. Validacion de existencia y recuperacion mediante correo electronico
    boolean existsByEmail(String email);
    Optional<Ciudadano> findByEmail(String email);

    // 4. Busqueda por documento de identidad exacto
    Optional<Ciudadano> findByDni(int dni);

    // 5. Listados filtrados por estado de activacion (Borrado Logico)
    List<Ciudadano> findByActivoTrue();
    List<Ciudadano> findByActivoFalse();

    // 6. Busqueda combinada para asegurar que el ciudadano este habilitado en el sistema
    Optional<Ciudadano> findByDniAndActivoTrue(int dni);
}
