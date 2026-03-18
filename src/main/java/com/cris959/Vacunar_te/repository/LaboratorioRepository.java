package com.cris959.Vacunar_te.repository;

import com.cris959.Vacunar_te.model.Laboratorio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LaboratorioRepository extends JpaRepository<Laboratorio, Long> {

    // Para evitar duplicados legales
    boolean existsByCuit(String cuit);

    // Buscar por nombre comercial para el buscador de la GUI
    List<Laboratorio> findByNomComercialContainingIgnoreCase(String nombre);
}
