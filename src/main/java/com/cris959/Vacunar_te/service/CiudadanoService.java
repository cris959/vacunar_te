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

    // 1_ Metodo Registrar Ciudadano
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

    // 2_ Metodo Puede Recibir Dosis
    @Transactional(readOnly = true)
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

    // 3_ Metodo Listar Todos
    @Transactional(readOnly = true)
    public List<Ciudadano> listarTodos() {
        return ciudadanoRepository.findAll();
    }

    // 4_ Metodo Buscar por DNI
    @Transactional(readOnly = true)
    public List<Ciudadano> buscarPorDNI(String dniTexto) {
        try {
            // Convertimos el String del formulario al int de la Entidad
            int dniIntero = Integer.parseInt(dniTexto);
            return ciudadanoRepository.findByDni(dniIntero);
        } catch (NumberFormatException e) {
            // Si el usuario escribe letras, devolvemos lista vacía para que no explote
            return new ArrayList<>();
        }
    }

    // 5_ Metodo Borrado Logico
    @Transactional
    public void eliminarCiudadano(int dni) {
        Ciudadano ciudadano = ciudadanoRepository.findById(dni)
                .orElseThrow(() -> new RuntimeException("No encontrado"));

        ciudadano.setActivo(false); // Soft Delete: Solo cambiamos el estado
        ciudadanoRepository.save(ciudadano);
    }

    // 6_ Metodo Listar Activos
    @Transactional(readOnly = true)
    public List<Ciudadano> obtenerTodosActivos() {
        // Ahora solo listaremos los que tengan activo = true
        return ciudadanoRepository.findByActivoTrue();
    }

    // 7_ Metodo Obtener Inactivos
    @Transactional(readOnly = true)
    public List<Ciudadano> obtenerInactivos() {
        return ciudadanoRepository.findByActivoFalse();
    }

    // 8_ Metodo Restaurar Inactivo
    @Transactional
    public void restaurarCiudadano(int dni) {
        Ciudadano ciudadano = ciudadanoRepository.findById(dni)
                .orElseThrow(() -> new RuntimeException("No se encontró el ciudadano"));
        ciudadano.setActivo(true);
        ciudadanoRepository.save(ciudadano);
    }
}
