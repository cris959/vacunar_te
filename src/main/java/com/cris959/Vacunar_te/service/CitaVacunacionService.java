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
import com.cris959.Vacunar_te.repository.VacunaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class CitaVacunacionService {

    private final CitaVacunacionRepository citaVacunacionRepository;

    private final CiudadanoRepository ciudadanoRepository;

    private final VacunaRepository vacunaRepository;

    public CitaVacunacionService(CitaVacunacionRepository citaVacunacionRepository, CiudadanoRepository ciudadanoRepository, VacunaRepository vacunaRepository) {
        this.citaVacunacionRepository = citaVacunacionRepository;
        this.ciudadanoRepository = ciudadanoRepository;
        this.vacunaRepository = vacunaRepository;
    }

    // 1. Valida el historial del ciudadano y programa una nueva cita asignando el refuerzo correspondiente
    @Transactional
    public CitaVacunacion programarCita(int dni, String centro, LocalDateTime fechaPropuesta) {
        Ciudadano ciudadano = ciudadanoRepository.findById(dni)
                .orElseThrow(() -> new CitaInvalidaException("Ciudadano no encontrado. Debe registrarse primero."));

        List<CitaVacunacion> historial = citaVacunacionRepository.findByCiudadanoAndEstadoNotOrderByFechaHoraCitaDesc(
                ciudadano, EstadoCita.CANCELADA
        );

        if (!historial.isEmpty()) {
            CitaVacunacion ultimaCita = historial.get(0);
            long diasTranscurridos = ChronoUnit.DAYS.between(ultimaCita.getFechaHoraCita(), fechaPropuesta);

            if (diasTranscurridos < 28) {
                throw new CitaInvalidaException("No han pasado las 4 semanas minimas requeridas (" + diasTranscurridos + " dias).");
            }
        }

        CitaVacunacion nuevaCita = new CitaVacunacion();
        nuevaCita.setCiudadano(ciudadano);
        nuevaCita.setCentroVacunacion(centro);
        nuevaCita.setFechaHoraCita(fechaPropuesta.withNano(0));
        nuevaCita.setEstado(EstadoCita.PROGRAMADA);

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

    // 2. Desplaza las citas programadas de un centro especifico por un periodo de 14 dias ante falta de insumos
    @Transactional
    public int postergarCitasPorFaltaDeStock(String centro) {
        List<CitaVacunacion> citasAPostergar = citaVacunacionRepository.findByCentroVacunacionAndEstado(
                centro, EstadoCita.PROGRAMADA
        );

        if (citasAPostergar.isEmpty()) {
            return 0;
        }

        for (CitaVacunacion cita : citasAPostergar) {
            cita.setFechaHoraCita(cita.getFechaHoraCita().plusDays(14));
            cita.setEstado(EstadoCita.POSTERGADA);
        }

        citaVacunacionRepository.saveAll(citasAPostergar);
        return citasAPostergar.size();
    }

    // 3. Registra la aplicacion efectiva de la dosis vinculando la vacuna y actualizando el estado de ambos registros
    @Transactional
    public void registrarVacunacionEfectiva(int codCita, Vacuna vacunaAsignada) {
        CitaVacunacion cita = citaVacunacionRepository.findById(codCita)
                .orElseThrow(() -> new CitaInvalidaException("La cita no existe."));

        if (cita.getEstado() == EstadoCita.CUMPLIDA) {
            throw new CitaInvalidaException("Esta cita ya fue marcada como cumplida anteriormente.");
        }

        cita.setFechaHoraColoca(LocalDateTime.now());
        cita.setVacuna(vacunaAsignada);
        cita.setEstado(EstadoCita.CUMPLIDA);
        vacunaAsignada.setEstado(EstadoVacuna.APLICADA);

        vacunaRepository.save(vacunaAsignada);
        citaVacunacionRepository.save(cita);
    }
}
