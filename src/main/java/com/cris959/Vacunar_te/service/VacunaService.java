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
import java.util.Optional;

@Service
public class VacunaService {

    private final VacunaRepository vacunaRepository;
    private final LaboratorioRepository laboratorioRepository;

    public VacunaService(VacunaRepository vacunaRepository, LaboratorioRepository laboratorioRepository) {
        this.vacunaRepository = vacunaRepository;
        this.laboratorioRepository = laboratorioRepository;
    }

    // 1. Registra un nuevo lote completo creando dosis individuales segun la cantidad indicada
    @Transactional
    public List<Vacuna> registrarLote(Long idLab, int cantidad,
                                      MedidaDosis medida, TipoAntigeno antigeno,
                                      LocalDate vencimiento) {

        Laboratorio lab = laboratorioRepository.findById(Math.toIntExact(idLab))
                .orElseThrow(() -> new RegistroVacunacionException("Laboratorio no encontrado."));

        if (vencimiento.isBefore(LocalDate.now())) {
            throw new RegistroVacunacionException("No se pueden ingresar vacunas ya vencidas.");
        }

        List<Vacuna> lote = new ArrayList<>();

        for (int i = 0; i < cantidad; i++) {
            Vacuna v = new Vacuna();
            v.setLaboratorio(lab);
            v.setAntigeno(antigeno);
            v.setFechaCaduca(vencimiento);
            v.setEstado(EstadoVacuna.DISPONIBLE);
            v.setNroSerieDosis(String.valueOf((int) (System.currentTimeMillis() % 100000) + i));
            lote.add(v);
        }

        return vacunaRepository.saveAll(lote);
    }

    // 2. Busca vacunas disponibles cuya fecha de caducidad ya paso y cambia su estado a VENCIDA
    @Transactional
    public int depurarVacunasVencidas() {
//        List<Vacuna> vencidas = vacunaRepository.findByEstadoAndFechaCaducaBefore(
//                EstadoVacuna.DISPONIBLE, LocalDate.now()
//        );
//
//        for (Vacuna v : vencidas) {
//            v.setEstado(EstadoVacuna.VENCIDA);
//        }
//
//        vacunaRepository.saveAll(vencidas);
//        return vencidas.size();
        // Esto ejecuta una sola sentencia SQL, evitando errores de validacion de Hibernate
        return vacunaRepository.actualizarEstadoVencidas();
    }

    // 3. Recupera una vacuna especifica por su identificador unico
    @Transactional(readOnly = true)
    public Vacuna buscarPorId(int id) {
        return vacunaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("No se encontro la vacuna con ID: " + id));
    }

    // 4. Retorna un listado de todas las vacunas que poseen estado DISPONIBLE
    @Transactional(readOnly = true)
    public List<Vacuna> obtenerDisponibles() {
        return vacunaRepository.findByEstado(EstadoVacuna.DISPONIBLE);
    }

    // 5. Gestiona el guardado de una vacuna y autogenera el numero de serie si es un registro nuevo
    @Transactional
    public void guardar(Vacuna vacuna) {
        if (vacuna.getIdVacuna() == 0 && (vacuna.getNroSerieDosis() == null ||
                vacuna.getNroSerieDosis().isEmpty() || vacuna.getNroSerieDosis().equals("0"))) {

            Optional<Vacuna> ultimoLote = vacunaRepository.findUltimoLoteRegistrado();

            if (ultimoLote.isPresent()) {
                Vacuna ultimo = ultimoLote.get();
                try {
                    long ultimoInicio = Long.parseLong(ultimo.getNroSerieDosis());
                    int cantidadAnterior = ultimo.getCantidadDosis();
                    vacuna.setNroSerieDosis(String.valueOf(ultimoInicio + cantidadAnterior));
                } catch (NumberFormatException e) {
                    vacuna.setNroSerieDosis("1000000000");
                }
            } else {
                vacuna.setNroSerieDosis("1000000000");
            }
        }

        if (vacuna.getIdVacuna() == 0 && vacuna.getEstado() == null) {
            vacuna.setEstado(EstadoVacuna.DISPONIBLE);
        }

        vacunaRepository.save(vacuna);
    }

    // 6. Lista la totalidad de los registros de vacunas en la base de datos sin filtros
    @Transactional(readOnly = true)
    public List<Vacuna> listarTodas() {
        return vacunaRepository.findAll();
    }

    // 7. Reduce en una unidad el stock de un lote y valida que existan dosis suficientes
    @Transactional
    public void descontarStock(int idVacuna) {
        Vacuna vacuna = vacunaRepository.findById(idVacuna)
                .orElseThrow(() -> new RuntimeException("No se encontro el lote de la vacuna"));

        if (vacuna.getCantidadDosis() <= 0) {
            throw new RuntimeException("No hay stock disponible para este lote");
        }

        vacuna.setCantidadDosis(vacuna.getCantidadDosis() - 1);
        vacunaRepository.save(vacuna);
    }

    // 8. Realiza una baja logica cambiando el estado a DESECHADA en lugar de eliminar el registro
    @Transactional
    public void eliminarLogico(int id) {
        Vacuna vacuna = vacunaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("No se encontro el lote para eliminar"));

        vacuna.setEstado(EstadoVacuna.DESECHADA);
        vacunaRepository.save(vacuna);
    }

    // 9. Devuelve todas las vacunas que no han sido marcadas como eliminadas/desechadas
    @Transactional(readOnly = true)
    public List<Vacuna> listarActivas() {

        return vacunaRepository.findByEstado(EstadoVacuna.DISPONIBLE);
    }

    // 10. Comprueba si un numero de serie ya existe en el sistema para evitar duplicidad
    @Transactional(readOnly = true)
    public boolean existeNumeroSerie(String nroSerie) {
        return vacunaRepository.existsByNroSerieDosis(nroSerie);
    }
}

