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

import com.cris959.Vacunar_te.exception.CitaInvalidaException;
import com.cris959.Vacunar_te.model.CitaVacunacion;
import com.cris959.Vacunar_te.service.CitaVacunacionService;
import com.cris959.Vacunar_te.service.CiudadanoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/citas")
public class CitaVacunacionController {

    private final CitaVacunacionService citaVacunacionService;

    private final CiudadanoService ciudadanoService;

    public CitaVacunacionController(CitaVacunacionService citaVacunacionService, CiudadanoService ciudadanoService) {
        this.citaVacunacionService = citaVacunacionService;
        this.ciudadanoService = ciudadanoService;
    }

    @GetMapping("/solicitar")
    public String solicitarTurno() {
        return "cita-busqueda";
    }

    // Cambiamos a @RequestMapping para que acepte tanto GET (de la tabla) como POST (del buscador)
    @RequestMapping(value = "/validar", method = {RequestMethod.GET, RequestMethod.POST})
    public String validarCiudadano(@RequestParam("dni") int dni, Model model) {
        try {
            if (ciudadanoService.puedeRecibirDosis(dni)) {
                model.addAttribute("dni", dni);

                // Agregamos los centros para que el <select> del HTML tenga opciones
                model.addAttribute("centros", List.of(
                        "Centro de Convenciones",
                        "Comedor Universitario",
                        "Pabellón Argentina",
                        "Autovac Kempes"
                ));

                return "cita-form";
            } else {
                model.addAttribute("error", "El ciudadano ya completó el esquema de 3 dosis.");
                return "cita-busqueda";
            }
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "cita-busqueda";
        }
    }

    @PostMapping("/guardar")
    public String guardarCita(@RequestParam("dni") int dni,
                              @RequestParam("centro") String centro,
                              Model model) {
        try {
            LocalDateTime fechaSugerida = LocalDateTime.now().plusDays(1);
            CitaVacunacion cita = citaVacunacionService.programarCita(dni, centro, fechaSugerida);

            model.addAttribute("mensaje", "Turno asignado con éxito para el día: " + cita.getFechaHoraCita().toLocalDate());
            model.addAttribute("dni", dni);
            model.addAttribute("centro", centro);

            return "cita-exito";
        } catch (CitaInvalidaException e) {
            model.addAttribute("error", e.getMessage());
            // Si falla, debemos volver a cargar los centros para el formulario
            model.addAttribute("centros", List.of("Centro de Convenciones", "Comedor Universitario", "Autovac Kempes"));
            return "cita-form";
        }
    }
}
