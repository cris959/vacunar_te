package com.cris959.Vacunar_te.repository;

import com.cris959.Vacunar_te.model.Laboratorio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LaboratorioRepository extends JpaRepository<Laboratorio, Integer> {


    // Este es el que te falta para la validacion de edicion
    Optional<Laboratorio> findByCuit(String cuit);

    // Para evitar duplicados nuevos
    boolean existsByCuit(String cuit);

    // Buscar por nombre comercial para el buscador de la GUI
    List<Laboratorio> findByNomComercialContainingIgnoreCase(String nombre);

    List<Laboratorio> findByActivoTrue();
    List<Laboratorio> findByActivoFalse();


}
