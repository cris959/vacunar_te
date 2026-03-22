package com.cris959.Vacunar_te.controller;

import com.cris959.Vacunar_te.exception.RegistroVacunacionException;
import com.cris959.Vacunar_te.model.Laboratorio;
import com.cris959.Vacunar_te.service.LaboratorioService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/laboratorios")
public class LavoratorioController {

    private final LaboratorioService  laboratorioService;

    public LavoratorioController(LaboratorioService laboratorioService) {
        this.laboratorioService = laboratorioService;
    }

    // 1. Muestra el listado de laboratorios activos con opcion de busqueda por nombre
    @GetMapping("/listado")
    public String listarLaboratorios(@RequestParam(name = "buscarNombre", required = false) String nombre, Model model) {
        List<Laboratorio> lista;
        if (nombre != null && !nombre.isBlank()) {
            // Si hay texto, busca por nombre
            lista = laboratorioService.buscarPorNombre(nombre);
        } else {
            // SI NO HAY BUSQUEDA, TRAE TODOS LOS ACTIVOS
            lista = laboratorioService.listarTodosActivos();
        }

        model.addAttribute("laboratorios", lista);
        return "laboratorio-listado";
    }

    // 2. Despliega el formulario para el registro de un nuevo laboratorio
    @GetMapping("/nuevo")
    public String formularioNuevo(Model model) {
        model.addAttribute("laboratorio", new Laboratorio());
        model.addAttribute("subtitulo", "Registrar Nuevo Proveedor");
        return "laboratorio-form";
    }

    // 3. Procesa el guardado del laboratorio gestionando errores de CUIT duplicado
    @PostMapping("/guardar")
    public String guardar(@ModelAttribute("laboratorio") Laboratorio lab, RedirectAttributes flash) {
//        try {
//            laboratorioService.registrarLaboratorio(lab);
//            flash.addFlashAttribute("success", "Laboratorio guardado con exito.");
//            return "redirect:/laboratorios/listado";
//        } catch (RegistroVacunacionException e) {
//            flash.addFlashAttribute("error", e.getMessage());
//            // Si el ID es > 0, devolvemos al form de editar, sino al de nuevo
//            if (lab.getIdLaboratorio() > 0) {
//                return "redirect:/laboratorios/editar/" + lab.getIdLaboratorio();
//            }
//            return "redirect:/laboratorios/nuevo";
//        }
        // PRUEBA DE FUEGO: Si aca el print sale con el dato viejo, el error es el HTML
        System.out.println("Nombre que llega del form: " + lab.getNomComercial());

        try {
            // El objeto 'lab' ya trae el ID y los cambios si el HTML esta bien
            laboratorioService.guardar(lab);
            flash.addFlashAttribute("success", "Datos actualizados.");
        } catch (Exception e) {
            flash.addFlashAttribute("error", "Error: " + e.getMessage());
        }
        return "redirect:/laboratorios/listado";
    }

    // 4. Carga los datos de un laboratorio existente para su modificacion
    @GetMapping("/editar/{id}")
    public String editar(@PathVariable("id") int id, Model model) {
        Laboratorio lab = laboratorioService.buscarPorId(id);
        model.addAttribute("laboratorio", lab);
        return "laboratorio-form";
    }

    // 5. Ejecuta el borrado logico (Soft Delete) del laboratorio
    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable("id") int id, RedirectAttributes flash) {
        laboratorioService.eliminarLogico(id);
        flash.addFlashAttribute("warning", "El laboratorio fue dado de baja correctamente.");
        return "redirect:/laboratorios/listado";
    }
}
