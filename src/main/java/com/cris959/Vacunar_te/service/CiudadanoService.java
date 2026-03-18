package com.cris959.Vacunar_te.service;

import com.cris959.Vacunar_te.exception.RegistroVacunacionException;
import com.cris959.Vacunar_te.model.Ciudadano;
import com.cris959.Vacunar_te.model.enums.EstadoCita;
import com.cris959.Vacunar_te.repository.CiudadanoRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class CiudadanoService {

    private final CiudadanoRepository ciudadanoRepository;

    public CiudadanoService(CiudadanoRepository ciudadanoRepository) {
        this.ciudadanoRepository = ciudadanoRepository;
    }

    @Transactional
    public Ciudadano registrarCiudadano(Ciudadano ciudadano) {
// 1. Validaciones de consistencia (Primero lo que NO requiere DB)
        if (ciudadano.getDni() <= 1000000) { // Un DNI real tiene mas de 1M
            throw new RegistroVacunacionException("El número de DNI ingresado no es válido.");
        }

        if (ciudadano.getNombreCompleto() == null || ciudadano.getNombreCompleto().strip().isEmpty()) {
            throw new RegistroVacunacionException("El nombre completo es obligatorio para el registro.");
        }

        // 2. Validaciones de Existencia (Consultas a la DB)
        if (ciudadanoRepository.existsById(ciudadano.getDni())) {
            throw new RegistroVacunacionException("El ciudadano con DNI " + ciudadano.getDni() + " ya se encuentra registrado.");
        }

        if (ciudadano.getEmail() != null && !ciudadano.getEmail().isBlank()) {
            if (ciudadanoRepository.existsByEmail(ciudadano.getEmail())) {
                throw new RegistroVacunacionException("El email '" + ciudadano.getEmail() + "' ya pertenece a otro ciudadano.");
            }
        }

        // 3. Persistencia
        return ciudadanoRepository.save(ciudadano);
    }


    public boolean puedeRecibirDosis(int dni) {
        // 1. Buscamos al ciudadano. Si no existe, NO puede recibir dosis aun
        // porque el instructivo dice que primero debe inscribirse al plan.
        Ciudadano c = ciudadanoRepository.findById(dni).orElse(null);

        if (c == null) {
            throw new RegistroVacunacionException("El ciudadano con DNI " + dni + " no está registrado en el sistema.");
        }

        // 2. Validamos la cantidad de citas.
        // OJO: No todas las citas cuentan. Solo cuentan las que no han sido CANCELADAS.
        long citasActivas = c.getCitas().stream()
                .filter(cita -> cita.getEstado() != EstadoCita.CANCELADA)
                .count();

        if (citasActivas >= 3) {
            return false; // Ya completo el esquema de 3 dosis (o tiene 3 turnos vigentes)
        }

        // 3. Validacion de intervalo (Si ya tiene al menos una dosis)
        // Esta logica es mas compleja y suele ir en CitaVacunacionService,
        // pero aqui ya puedes retornar true si paso el filtro de cantidad.
        return true;
    }
}
