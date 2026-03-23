package com.cris959.Vacunar_te.repository;

import com.cris959.Vacunar_te.model.Laboratorio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LaboratorioRepository extends JpaRepository<Laboratorio, Integer> {


    // Busca un laboratorio por CUIT exacto, util para validaciones de edicion
    Optional<Laboratorio> findByCuit(String cuit);

    // Verifica si ya existe un laboratorio con ese CUIT para evitar duplicados
    boolean existsByCuit(String cuit);

    // Busca laboratorios por nombre comercial ignorando mayusculas y minusculas
    List<Laboratorio> findByNomComercialContainingIgnoreCase(String nombre);

    // Retorna la lista de laboratorios que se encuentran en estado activo
    List<Laboratorio> findByActivoTrue();

    // Retorna la lista de laboratorios dados de baja (papelera)
    List<Laboratorio> findByActivoFalse();

    // Busca laboratorios que contengan una secuencia de numeros en su CUIT
    List<Laboratorio> findByCuitContaining(String cuit);
}
