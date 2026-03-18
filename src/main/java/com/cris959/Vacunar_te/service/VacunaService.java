package com.cris959.Vacunar_te.service;

import com.cris959.Vacunar_te.exception.RegistroVacunacionException;
import com.cris959.Vacunar_te.model.Laboratorio;
import com.cris959.Vacunar_te.model.Vacuna;
import com.cris959.Vacunar_te.model.enums.EstadoVacuna;
import com.cris959.Vacunar_te.model.enums.MedidaDosis;
import com.cris959.Vacunar_te.model.enums.TipoAntigeno;
import com.cris959.Vacunar_te.repository.LaboratorioRepository;
import com.cris959.Vacunar_te.repository.VacunaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class VacunaService {

    private final VacunaRepository vacunaRepository;

    private final LaboratorioRepository laboratorioRepository;

    public VacunaService(VacunaRepository vacunaRepository, LaboratorioRepository laboratorioRepository) {
        this.vacunaRepository = vacunaRepository;
        this.laboratorioRepository = laboratorioRepository;
    }

    @Transactional
    public List<Vacuna> registrarLote(Long idLab, int cantidad, String marca,
                                      MedidaDosis medida, TipoAntigeno antigeno,
                                      LocalDate vencimiento) {

        // 1. Validar que el laboratorio existe
        Laboratorio lab = laboratorioRepository.findById(idLab)
                .orElseThrow(() -> new RegistroVacunacionException("Laboratorio no encontrado."));

        // 2. Validar que la fecha de vencimiento sea futura
        if (vencimiento.isBefore(LocalDate.now())) {
            throw new RegistroVacunacionException("No se pueden ingresar vacunas ya vencidas.");
        }

        List<Vacuna> loteNuevo = new ArrayList<>();

        // 3. Crear las dosis una por una
        for (int i = 0; i < cantidad; i++) {
            Vacuna v = new Vacuna();
            v.setLaboratorio(lab);
            v.setMarca(marca);
            v.setMedida(medida);
            v.setAntigeno(antigeno);
            v.setFechaCaduca(vencimiento);
            v.setEstado(EstadoVacuna.DISPONIBLE);

            // Generamos un nro de serie ficticio basado en el tiempo + índice
            v.setNroSerieDosis((int)(System.currentTimeMillis() % 100000) + i);

            loteNuevo.add(v);
        }

        return vacunaRepository.saveAll(loteNuevo);
    }

    @Transactional
    public int depurarVacunasVencidas() {
        List<Vacuna> vencidas = vacunaRepository.findByEstadoAndFechaCaducaBefore(
                EstadoVacuna.DISPONIBLE, LocalDate.now()
        );

        for (Vacuna v : vencidas) {
            v.setEstado(EstadoVacuna.VENCIDA);
        }

        vacunaRepository.saveAll(vencidas);
        return vencidas.size();
    }
}
