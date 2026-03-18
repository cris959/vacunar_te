package com.cris959.Vacunar_te.service;

import com.cris959.Vacunar_te.exception.RegistroVacunacionException;
import com.cris959.Vacunar_te.model.Laboratorio;
import com.cris959.Vacunar_te.repository.LaboratorioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LaboratorioService {

    private final LaboratorioRepository laboratorioRepository;

    public LaboratorioService(LaboratorioRepository laboratorioRepository) {
        this.laboratorioRepository = laboratorioRepository;
    }

    @Transactional
    public Laboratorio registrarLaboratorio(Laboratorio lab) {
        // 1. Validar CUIT (Debe tener 11 digitos, sin guiones para la DB)
        if (lab.getCuit() == null || lab.getCuit().length() != 11) {
            throw new RegistroVacunacionException("El CUIT debe tener exactamente 11 dígitos numéricos.");
        }

        // 2. Validar duplicados
        if (laboratorioRepository.existsByCuit(lab.getCuit())) {
            throw new RegistroVacunacionException("Ya existe un laboratorio registrado con el CUIT: " + lab.getCuit());
        }

        return laboratorioRepository.save(lab);
    }
}
