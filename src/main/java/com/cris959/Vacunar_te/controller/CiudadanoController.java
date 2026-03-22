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

import com.cris959.Vacunar_te.model.Ciudadano;
import com.cris959.Vacunar_te.model.enums.AmbitoTrabajo;
import com.cris959.Vacunar_te.service.CiudadanoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/ciudadanos")
public class CiudadanoController {

    private final CiudadanoService ciudadanoService;

    public CiudadanoController(CiudadanoService ciudadanoService) {
        this.ciudadanoService = ciudadanoService;
    }

    // 1. Prepara el modelo con un nuevo objeto Ciudadano y los ambitos laborales para el formulario
    @GetMapping("/nuevo")
    public String formularioRegistro(Model model) {
        model.addAttribute("ciudadano", new Ciudadano());
        model.addAttribute("ambitos", AmbitoTrabajo.values());
        return "ciudadano-form";
    }

    // 2. Gestiona el alta de ciudadanos manejando excepciones de validacion y mensajes temporales (Flash)
    @PostMapping("/guardar")
    public String guardarCiudadano(@ModelAttribute("ciudadano") Ciudadano ciudadano, Model model, RedirectAttributes flash) {
        try {
            ciudadanoService.registrarCiudadano(ciudadano);
            flash.addFlashAttribute("success", "Ciudadano registrado con exito.");
            return "redirect:/ciudadanos/listado";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("ambitos", AmbitoTrabajo.values());
            return "ciudadano-form";
        }
    }

    // 3. Visualiza el listado de ciudadanos activos permitiendo el filtrado opcional por numero de DNI
    @GetMapping("/listado")
    public String listarCiudadanos(@RequestParam(name = "buscarDni", required = false) String buscarDni, Model model) {
        List<Ciudadano> lista;

        if (buscarDni != null && !buscarDni.isEmpty()) {
            lista = ciudadanoService.buscarPorDNI(buscarDni);
        } else {
            lista = ciudadanoService.listarCiudadanosActivos();
        }

        model.addAttribute("ciudadanos", lista);
        return "ciudadano-listado";
    }

    // 4. Ejecuta el borrado logico del ciudadano y redirige al listado principal con confirmacion
    @GetMapping("/eliminar/{dni}")
    public String eliminar(@PathVariable("dni") int dni, RedirectAttributes flash) {
        ciudadanoService.eliminarCiudadano(dni);
        flash.addFlashAttribute("success", "Ciudadano enviado a la papelera.");
        return "redirect:/ciudadanos/listado";
    }

    // 5. Carga la vista de la papelera con aquellos ciudadanos que fueron dados de baja logicamente
    @GetMapping("/papelera")
    public String mostrarPapelera(Model model) {
        List<Ciudadano> inactivos = ciudadanoService.obtenerInactivos();
        model.addAttribute("inactivos", inactivos);
        return "ciudadano-papelera";
    }

    // 6. Revierte la baja logica de un ciudadano permitiendo que vuelva a figurar en los listados operativos
    @GetMapping("/restaurar/{dni}")
    public String restaurar(@PathVariable int dni, RedirectAttributes flash) {
        ciudadanoService.restaurarCiudadano(dni);
        flash.addFlashAttribute("success", "Ciudadano restaurado con exito.");
        return "redirect:/ciudadanos/listado";
    }

    // 7. Carga los datos en el formulario para editar
    @GetMapping("/editar/{dni}")
    public String mostrarFormularioEditar(@PathVariable("dni") int dni, Model model) {
        Ciudadano ciudadano = ciudadanoService.buscarPorId(dni);
        model.addAttribute("ciudadano", ciudadano);
        model.addAttribute("ambitos", AmbitoTrabajo.values());
        model.addAttribute("editando", true); // Para cambiar el titulo en el HTML
        return "ciudadano-form";
    }

    // 8. Procesa la edicion (puedes crear un POST especifico o ajustar el anterior)
    @PostMapping("/actualizar")
    public String actualizar(@ModelAttribute("ciudadano") Ciudadano ciudadano, RedirectAttributes flash) {
        try {
            ciudadanoService.actualizarCiudadano(ciudadano);
            flash.addFlashAttribute("success", "Datos actualizados correctamente.");
            return "redirect:/ciudadanos/listado";
        } catch (Exception e) {
            flash.addFlashAttribute("error", e.getMessage());
            return "redirect:/ciudadanos/editar/" + ciudadano.getDni();
        }
    }
}



