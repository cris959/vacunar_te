package com.cris959.Vacunar_te.service;

import com.cris959.Vacunar_te.exception.RegistroVacunacionException;
import com.cris959.Vacunar_te.model.Laboratorio;
import com.cris959.Vacunar_te.repository.LaboratorioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class LaboratorioService {

    private final LaboratorioRepository laboratorioRepository;

    public LaboratorioService(LaboratorioRepository laboratorioRepository) {
        this.laboratorioRepository = laboratorioRepository;
    }

    // 1. Registra un nuevo laboratorio con validaciones de CUIT y duplicados
    @Transactional
    public Laboratorio registrarLaboratorio(Laboratorio lab) {
        // 1. Validacion de formato (Sigue igual)
        if (lab.getCuit() == null || lab.getCuit().length() != 11) {
            throw new RegistroVacunacionException("El CUIT debe tener exactamente 11 digitos numericos.");
        }

        // 2. Validacion de Duplicados Inteligente
        // Buscamos si existe OTRO laboratorio con ese CUIT que no sea el actual
        Optional<Laboratorio> existente = laboratorioRepository.findByCuit(lab.getCuit());

        if (existente.isPresent() && existente.get().getIdLaboratorio() != lab.getIdLaboratorio()) {
            throw new RegistroVacunacionException("Ya existe otro laboratorio registrado con el CUIT: " + lab.getCuit());
        }

        // 3. Persistencia
        lab.setActivo(true);
        return laboratorioRepository.save(lab);
    }

    // 2. Recupera el listado de laboratorios habilitados (Soft Delete aplicado)
    @Transactional(readOnly = true)
    public List<Laboratorio> listarTodosActivos() {
        return laboratorioRepository.findByActivoTrue();
    }

    // 3. Implementa el Soft Delete cambiando el estado de activacion del registro
    @Transactional
    public void eliminarLogico(int id) {
        Laboratorio lab = laboratorioRepository.findById(id)
                .orElseThrow(() -> new RegistroVacunacionException("No se encontro el laboratorio para eliminar."));

        lab.setActivo(false);
        laboratorioRepository.save(lab);
    }

    // 4. Permite restaurar el acceso de un laboratorio que fue dado de baja previamente
    @Transactional
    public void restaurarLaboratorio(int id) {
        Laboratorio lab = laboratorioRepository.findById(id)
                .orElseThrow(() -> new RegistroVacunacionException("No se encontro el laboratorio para restaurar."));

        lab.setActivo(true);
        laboratorioRepository.save(lab);
    }

    // 5. Busca un laboratorio especifico por ID para tareas de consulta o edicion
    @Transactional(readOnly = true)
    public Laboratorio buscarPorId(int id) {
        return laboratorioRepository.findById(id)
                .orElseThrow(() -> new RegistroVacunacionException("Laboratorio no encontrado con ID: " + id));
    }

    // 6. Filtra laboratorios activos por coincidencia en el nombre comercial
    @Transactional(readOnly = true)
    public List<Laboratorio> buscarPorNombre(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            return listarTodosActivos();
        }
        return laboratorioRepository.findByNomComercialContainingIgnoreCase(nombre)
                .stream()
                .filter(Laboratorio::isActivo)
                .toList();
    }

    // 7. Facilita la persistencia directa para actualizaciones de datos
    @Transactional
    public void guardar(Laboratorio lab) {
        lab.setActivo(true);
        laboratorioRepository.save(lab);
    }
}
