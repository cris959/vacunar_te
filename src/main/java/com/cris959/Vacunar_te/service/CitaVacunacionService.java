package com.cris959.Vacunar_te.service;

import com.cris959.Vacunar_te.exception.CitaInvalidaException;
import com.cris959.Vacunar_te.model.CitaVacunacion;
import com.cris959.Vacunar_te.model.Ciudadano;
import com.cris959.Vacunar_te.model.Vacuna;
import com.cris959.Vacunar_te.model.enums.DosisRefuerzo;
import com.cris959.Vacunar_te.model.enums.EstadoCita;
import com.cris959.Vacunar_te.model.enums.EstadoVacuna;
import com.cris959.Vacunar_te.repository.CitaVacunacionRepository;
import com.cris959.Vacunar_te.repository.CiudadanoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class CitaVacunacionService {

    private final CitaVacunacionRepository citaVacunacionRepository;

    private final CiudadanoRepository ciudadanoRepository;

    public CitaVacunacionService(CitaVacunacionRepository citaVacunacionRepository, CiudadanoRepository ciudadanoRepository) {
        this.citaVacunacionRepository = citaVacunacionRepository;
        this.ciudadanoRepository = ciudadanoRepository;
    }

    @Transactional
    public CitaVacunacion programarCita(int dni, String centro, LocalDateTime fechaPropuesta) {
        // Truncamos la fecha de entrada apenas llega
        LocalDateTime fechaLimpia = fechaPropuesta.withNano(0);

        // 1. Verificar si el ciudadano existe
        Ciudadano ciudadano = ciudadanoRepository.findById(dni)
                .orElseThrow(() -> new CitaInvalidaException("Ciudadano no encontrado. Debe registrarse primero."));

        // 2. Obtener citas previas (que no esten canceladas)
        List<CitaVacunacion> historial = citaVacunacionRepository.findByCiudadanoAndEstadoNotOrderByFechaHoraCitaDesc(
                ciudadano, EstadoCita.CANCELADA
        );

        // 3. Validar Regla de Tiempo si ya tiene dosis anteriores
        if (!historial.isEmpty()) {
            CitaVacunacion ultimaCita = historial.getFirst();
            long diasTranscurridos = ChronoUnit.DAYS.between(ultimaCita.getFechaHoraCita(), fechaPropuesta);

            // Regla: Minimo 4 semanas (28 dias)
            if (diasTranscurridos < 28) {
                throw new CitaInvalidaException("No han pasado las 4 semanas mínimas requeridas desde la última dosis (" + diasTranscurridos + " días transcurridos).");
            }
        }

        // 4. Crear y Guardar la Cita
        CitaVacunacion nuevaCita = new CitaVacunacion();
        nuevaCita.setCiudadano(ciudadano);
        nuevaCita.setCentroVacunacion(centro);
        nuevaCita.setFechaHoraCita(fechaPropuesta.withNano(0));
        nuevaCita.setEstado(EstadoCita.PROGRAMADA);

        // 5. ASIGNAR EL ENUM DE REFUERZO (Solucion al error Column 'cod_refuerzo' cannot be null)
        int dosisAnteriores = historial.size();

        if (dosisAnteriores == 0) {
            nuevaCita.setCodRefuerzo(DosisRefuerzo.PRIMERA);
        } else if (dosisAnteriores == 1) {
            nuevaCita.setCodRefuerzo(DosisRefuerzo.SEGUNDA);
        } else {
            nuevaCita.setCodRefuerzo(DosisRefuerzo.TERCERA);
        }

        return citaVacunacionRepository.save(nuevaCita);
    }

    @Transactional
    public int postergarCitasPorFaltaDeStock(String centro) {
        // 1. Buscamos todas las citas que estan programadas para ese centro
        List<CitaVacunacion> citasAPostergar = citaVacunacionRepository.findByCentroVacunacionAndEstado(
                centro, EstadoCita.PROGRAMADA
        );

        if (citasAPostergar.isEmpty()) {
            return 0; // No habia citas para postergar
        }

        // 2. Aplicamos la postergacion de 14 dias a cada una
        for (CitaVacunacion cita : citasAPostergar) {
            LocalDateTime nuevaFecha = cita.getFechaHoraCita().plusDays(14);

            cita.setFechaHoraCita(nuevaFecha);
            cita.setEstado(EstadoCita.POSTERGADA); // Cambiamos el estado para auditoria
        }

        // 3. Guardamos los cambios (Spring Data JPA sincroniza automaticamente por @Transactional)
        citaVacunacionRepository.saveAll(citasAPostergar);

        return citasAPostergar.size();
    }

    @Transactional
    public void registrarVacunacionEfectiva(int codCita, Vacuna vacunaAsignada) {
        CitaVacunacion cita = citaVacunacionRepository.findById(codCita)
                .orElseThrow(() -> new CitaInvalidaException("La cita no existe."));

        if (cita.getEstado() == EstadoCita.CUMPLIDA) {
            throw new CitaInvalidaException("Esta cita ya fue marcada como cumplida anteriormente.");
        }

        // Actualizamos los datos de la cita
        cita.setFechaHoraColoca(LocalDateTime.now());
        cita.setVacuna(vacunaAsignada);
        cita.setEstado(EstadoCita.CUMPLIDA);

        // IMPORTANTE: Tambien debemos actualizar el estado de la VACUNA
        // (Recuerda que creamos el Enum EstadoVacuna anteriormente)
        vacunaAsignada.setEstado(EstadoVacuna.APLICADA);

        citaVacunacionRepository.save(cita);
    }
}
