package com.cris959.Vacunar_te.service;

import com.cris959.Vacunar_te.exception.RegistroVacunacionException;
import com.cris959.Vacunar_te.model.Ciudadano;
import com.cris959.Vacunar_te.model.enums.EstadoCita;
import com.cris959.Vacunar_te.repository.CiudadanoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class CiudadanoService {

    private final CiudadanoRepository ciudadanoRepository;

    public CiudadanoService(CiudadanoRepository ciudadanoRepository) {
        this.ciudadanoRepository = ciudadanoRepository;
    }

    // 1. Valida la integridad de los datos (DNI y Nombre) y la unicidad (Email) antes de persistir
    @Transactional
    public Ciudadano registrarCiudadano(Ciudadano ciudadano) {
        if (ciudadano.getDni() <= 1000000) {
            throw new RegistroVacunacionException("El numero de DNI ingresado no es valido.");
        }

        if (ciudadano.getNombreCompleto() == null || ciudadano.getNombreCompleto().strip().isEmpty()) {
            throw new RegistroVacunacionException("El nombre completo es obligatorio para el registro.");
        }

        if (ciudadanoRepository.existsById(ciudadano.getDni())) {
            throw new RegistroVacunacionException("El ciudadano con DNI " + ciudadano.getDni() + " ya esta registrado.");
        }

        if (ciudadano.getEmail() != null && !ciudadano.getEmail().isBlank()) {
            if (ciudadanoRepository.existsByEmail(ciudadano.getEmail())) {
                throw new RegistroVacunacionException("El email ya pertenece a otro ciudadano.");
            }
        }

        return ciudadanoRepository.save(ciudadano);
    }

    // 2. Verifica si el ciudadano tiene menos de 3 citas activas para permitir un nuevo turno
    @Transactional(readOnly = true)
    public boolean puedeRecibirDosis(int dni) {
        Ciudadano c = ciudadanoRepository.findById(dni)
                .orElseThrow(() -> new RegistroVacunacionException("El ciudadano con DNI " + dni + " no esta registrado."));

        long citasActivas = c.getCitas().stream()
                .filter(cita -> cita.getEstado() != EstadoCita.CANCELADA)
                .count();

        return citasActivas < 3;
    }

    // 3. Recupera el listado completo de ciudadanos sin aplicar filtros de estado
    @Transactional(readOnly = true)
    public List<Ciudadano> listarTodos() {
        return ciudadanoRepository.findAll();
    }

    // 4. Realiza una busqueda por documento convirtiendo el texto de entrada y manejando errores de formato
    @Transactional(readOnly = true)
    public List<Ciudadano> buscarPorDNI(String dniTexto) {
        try {
            int dniEntero = Integer.parseInt(dniTexto);
            return ciudadanoRepository.findByDni(dniEntero).map(List::of).orElse(new ArrayList<>());
        } catch (NumberFormatException e) {
            return new ArrayList<>();
        }
    }

    // 5. Aplica el borrado logico cambiando el estado de activacion del ciudadano
    @Transactional
    public void eliminarCiudadano(int dni) {
        Ciudadano ciudadano = ciudadanoRepository.findById(dni)
                .orElseThrow(() -> new RegistroVacunacionException("Ciudadano no encontrado."));

        ciudadano.setActivo(false);
        ciudadanoRepository.save(ciudadano);
    }

    // 6. Obtiene unicamente a los ciudadanos que se encuentran habilitados (activo = true)
    @Transactional(readOnly = true)
    public List<Ciudadano> obtenerTodosActivos() {
        return ciudadanoRepository.findByActivoTrue();
    }

    // 7. Lista a los ciudadanos que han sido dados de baja logicamente
    @Transactional(readOnly = true)
    public List<Ciudadano> obtenerInactivos() {
        return ciudadanoRepository.findByActivoFalse();
    }

    // 8. Revierte el proceso de borrado logico restaurando el acceso del ciudadano al sistema
    @Transactional
    public void restaurarCiudadano(int dni) {
        Ciudadano ciudadano = ciudadanoRepository.findById(dni)
                .orElseThrow(() -> new RegistroVacunacionException("No se encontro el ciudadano."));
        ciudadano.setActivo(true);
        ciudadanoRepository.save(ciudadano);
    }
}
