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

    // 1. Mostrar el formulario de registro
    @GetMapping("/nuevo")
    public String formularioRegistro(Model model) {
        model.addAttribute("ciudadano", new Ciudadano());
        model.addAttribute("ambitos", AmbitoTrabajo.values()); // Enviamos los Enums para el select
        return "ciudadano-form"; // Nombre del archivo .html en templates
    }

    // 2. Procesar el registro
    @PostMapping("/guardar")
    public String guardarCiudadano(@ModelAttribute("ciudadano") Ciudadano ciudadano, Model model, RedirectAttributes flash) {
        try {
            ciudadanoService.registrarCiudadano(ciudadano);
            // Esto guarda un mensaje que solo dura un "salto" de pagina
            flash.addFlashAttribute("success", "¡Ciudadano registrado con éxito!");
            return "redirect:/ciudadanos/listado";  // Ahora apunta al listado
        } catch (Exception e) {
            // Si hay un error (DNI duplicado, etc.), volvemos al formulario con el mensaje
            model.addAttribute("error", e.getMessage());
            model.addAttribute("ambitos", AmbitoTrabajo.values());
            return "ciudadano-form";
        }
    }

    // 3. Mostrar listado
    @GetMapping("/listado")
    public String listarCiudadanos(@RequestParam(name = "buscarDni", required = false) String buscarDni, Model model) {
        List<Ciudadano> lista;

        if (buscarDni != null && !buscarDni.isEmpty()) {
            lista = ciudadanoService.buscarPorDNI(buscarDni);
        } else {
            lista = ciudadanoService.obtenerTodosActivos();
        }

        model.addAttribute("ciudadanos", lista);
        return "ciudadano-listado";
    }

    // 4. Eliminar Ciudadano
    @GetMapping("/eliminar/{dni}")
    public String eliminar(@PathVariable("dni") int dni, RedirectAttributes flash) {
        ciudadanoService.eliminarCiudadano(dni);
        flash.addFlashAttribute("success", "Ciudadano enviado a la papelera.");
        return "redirect:/ciudadanos/listado";
    }

    // 5. Mostar Papelera "borrados"
    @GetMapping("/papelera")
    public String mostrarPapelera(Model model) {
        List<Ciudadano> inactivos = ciudadanoService.obtenerInactivos();
        model.addAttribute("inactivos", inactivos);
        return "ciudadano-papelera";
    }

    // 6. Restaurar Ciudadano
    @GetMapping("/restaurar/{dni}")
    public String restaurar(@PathVariable int dni, RedirectAttributes flash) {
        ciudadanoService.restaurarCiudadano(dni);
        flash.addFlashAttribute("success", "Ciudadano restaurado con éxito.");
        return "redirect:/ciudadanos/listado";
    }
}



