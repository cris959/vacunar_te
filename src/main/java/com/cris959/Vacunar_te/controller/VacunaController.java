package com.cris959.Vacunar_te.controller;

import com.cris959.Vacunar_te.model.Vacuna;
import com.cris959.Vacunar_te.model.enums.MedidaDosis;
import com.cris959.Vacunar_te.model.enums.TipoAntigeno;
import com.cris959.Vacunar_te.service.LaboratorioService;
import com.cris959.Vacunar_te.service.VacunaService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/vacunas")
public class VacunaController {

    private final VacunaService vacunaService;

    private final LaboratorioService laboratorioService;

    public VacunaController(VacunaService vacunaService, LaboratorioService laboratorioService) {
        this.vacunaService = vacunaService;
        this.laboratorioService = laboratorioService;
    }
    // 1. Prepara el modelo con un objeto vacio y carga las listas para el formulario de alta
    @GetMapping("/nuevo")
    public String mostrarFormularioNuevo(Model model) {
        model.addAttribute("vacuna", new Vacuna());
        cargarListasEnModel(model);
        return "vacuna-form";
    }

    // 2. Procesa el guardado validando errores de formulario, duplicados de serie y excepciones de servicio
    @PostMapping("/guardar")
    public String guardarVacuna(@Valid @ModelAttribute("vacuna") Vacuna vacuna,
                                BindingResult result,
                                Model model,
                                RedirectAttributes flash) {

        // 1. Validaciones de JSR-303 (Anotaciones en la Entidad como @Size, @Pattern)
        if (result.hasErrors()) {
            cargarListasEnModel(model);
            return "vacuna-form";
        }

        // 2. Validacion de Duplicados (Solo para nuevas vacunas)
        // Nota: Simplifique la logica del ID y el numero de serie
        if (vacuna.getIdVacuna() == 0) {
            if (vacunaService.existeNumeroSerie(vacuna.getNroSerieDosis())) {
                result.rejectValue("nroSerieDosis", "error.vacuna", "Este número de serie ya existe en el sistema.");
                cargarListasEnModel(model);
                return "vacuna-form";
            }
        }

        try {
            vacunaService.guardar(vacuna);
            // El exito requiere redirect para limpiar el formulario
            flash.addFlashAttribute("success", "Lote de vacunas guardado correctamente.");
            return "redirect:/vacunas/listado";

        } catch (Exception e) {
            // ERROR CRITICO CORREGIDO:
            // Si hay error y vuelves a la vista (return), usa model.addAttribute, NO flash.
            model.addAttribute("error", "Error al guardar el lote: " + e.getMessage());
            cargarListasEnModel(model);
            return "vacuna-form";
        }
    }

    // 3. Metodo privado para centralizar la carga de Enums y laboratorios en el Model
    private void cargarListasEnModel(Model model) {
        model.addAttribute("laboratorios", laboratorioService.listarTodos());
        model.addAttribute("medidas", MedidaDosis.values());
        model.addAttribute("antigenos", TipoAntigeno.values());
    }

    // 4. Carga la vista de listado con todas las vacunas que poseen un estado activo
    @GetMapping("/listado")
    public String listarVacunas(Model model) {
        // 1. Marcamos las vencidas en la DB (Tu log ya confirmó que esto funciona)
        vacunaService.depurarVacunasVencidas();

        // 2. Traemos SOLO las que quedaron como DISPONIBLE
        List<Vacuna> disponibles = vacunaService.listarActivas();

        model.addAttribute("vacunas", disponibles);
        return "vacuna-listado";
    }

    // 5. Busca una vacuna por ID y precarga el formulario para su edicion
    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable("id") int id, Model model) {
        Vacuna vacuna = vacunaService.buscarPorId(id);
        model.addAttribute("vacuna", vacuna);
        cargarListasEnModel(model);
        return "vacuna-form";
    }

    // 6. Ejecuta una baja logica del lote seleccionado para que deje de aparecer en las listas activas
    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable("id") int id, RedirectAttributes flash) {
        try {
            vacunaService.eliminarLogico(id);
            flash.addFlashAttribute("success", "El lote ha sido marcado como DESECHADO.");
        } catch (Exception e) {
            flash.addFlashAttribute("error", "Error al eliminar: " + e.getMessage());
        }
        return "redirect:/vacunas/listado";
    }

    // 7. Dispara el proceso de mantenimiento para marcar como vencidas las vacunas caducadas
    @GetMapping("/depurar")
    public String depurarVencidas(RedirectAttributes flash) {
        try {
            int cantidad = vacunaService.depurarVacunasVencidas();
            if (cantidad > 0) {
                flash.addFlashAttribute("success", "Mantenimiento exitoso: " + cantidad + " lotes actualizados.");
            } else {
                flash.addFlashAttribute("info", "No se encontraron lotes para depurar hoy.");
            }
        } catch (Exception e) {
            flash.addFlashAttribute("error", "Error en el proceso: " + e.getMessage());
        }
        return "redirect:/vacunas/listado";
    }

    // 8. Reduce el contador de dosis de un lote especifico de forma directa desde la vista
    @GetMapping("/descontar/{id}")
    public String descontarDosis(@PathVariable("id") int id, RedirectAttributes flash) {
        try {
            vacunaService.descontarStock(id);
            flash.addFlashAttribute("success", "Se ha descontado 1 dosis del lote correctamente.");
        } catch (Exception e) {
            flash.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/vacunas/listado";
    }

    // Si alguien intenta entrar por GET a la ruta de guardado, lo redirigimos al formulario
    @GetMapping("/guardar")
    public String redireccionarAlFormulario() {
        return "redirect:/vacunas/nuevo";
    }
}
