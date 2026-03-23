/*
Copyright 2026 Christian Garay

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/
package com.cris959.Vacunar_te.controller;

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
    public String listarLaboratorios(@RequestParam(name = "cuit", required = false) String cuit, Model model) {
        List<Laboratorio> laboratorios;

        if (cuit != null && !cuit.isEmpty()) {
            laboratorios = laboratorioService.buscarPorCuit(cuit);
            model.addAttribute("cuitBusqueda", cuit); // Para mantener el texto en el input
        } else {
            laboratorios = laboratorioService.listarTodosActivos();
        }

        model.addAttribute("laboratorios", laboratorios);
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
    @PostMapping("/eliminar/{id}")
    public String eliminar(@PathVariable("id") int id, RedirectAttributes flash) {
        laboratorioService.eliminarLogico(id);
        flash.addFlashAttribute("warning", "El laboratorio fue dado de baja correctamente.");
        return "redirect:/laboratorios/listado";
    }

    // 6. Mostrar papelera => false
    @GetMapping("/papelera")
    public String mostrarPapelera(Model model) {
        List<Laboratorio> inactivos = laboratorioService.listarInactivos();
        model.addAttribute("laboratorios", inactivos);
        return "laboratorio-papelera";
    }

    // 7. Restaurar el laboratorio => true
    @PostMapping("/restaurar/{id}")
    public String restaurar(@PathVariable Long id, RedirectAttributes flash) {
        try {
            laboratorioService.restaurar(id);
            flash.addFlashAttribute("success", "Laboratorio restaurado con éxito.");
        } catch (Exception e) {
            flash.addFlashAttribute("error", "No se pudo restaurar: " + e.getMessage());
        }
        return "redirect:/laboratorios/papelera";
    }
}
