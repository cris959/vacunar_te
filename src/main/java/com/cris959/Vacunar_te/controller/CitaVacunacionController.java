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
import com.cris959.Vacunar_te.service.VacunaService;
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

    // 1. Redirige a la vista inicial de busqueda para solicitar un turno mediante DNI
    @GetMapping("/solicitar")
    public String solicitarTurno() {
        return "cita-busqueda";
    }

    // 2. Verifica si el ciudadano es apto para recibir una dosis y carga los centros de vacunacion disponibles
    @RequestMapping(value = "/validar", method = {RequestMethod.GET, RequestMethod.POST})
    public String validarCiudadano(@RequestParam("dni") int dni, Model model) {
        try {
            if (ciudadanoService.puedeRecibirDosis(dni)) {
                model.addAttribute("dni", dni);
                cargarCentrosEnModel(model);
                return "cita-form";
            } else {
                model.addAttribute("error", "El ciudadano ya completo el esquema de 3 dosis.");
                return "cita-busqueda";
            }
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "cita-busqueda";
        }
    }

    // 3. Procesa el guardado de la cita asignando una fecha sugerida y gestionando excepciones de validacion
    @PostMapping("/guardar")
    public String guardarCita(@RequestParam("dni") int dni,
                              @RequestParam("centro") String centro,
                              Model model) {
        try {
            LocalDateTime fechaSugerida = LocalDateTime.now().plusDays(1);
            CitaVacunacion cita = citaVacunacionService.programarCita(dni, centro, fechaSugerida);

            model.addAttribute("mensaje", "Turno asignado con exito para el dia: " + cita.getFechaHoraCita().toLocalDate());
            model.addAttribute("dni", dni);
            model.addAttribute("centro", centro);

            return "cita-exito";
        } catch (CitaInvalidaException e) {
            model.addAttribute("error", e.getMessage());
            cargarCentrosEnModel(model);
            return "cita-form";
        }
    }

    // 4. Metodo auxiliar para centralizar el listado de centros de vacunacion activos en Cordoba
    private void cargarCentrosEnModel(Model model) {
        model.addAttribute("centros", List.of(
                "Centro de Convenciones",
                "Comedor Universitario",
                "Pabellon Argentina",
                "Autovac Kempes"
        ));
    }

    // Este es el procedimiento que falta y causa el error del botón verde
    @GetMapping("/nuevo")
    public String formularioNuevaCita(Model model) {
        // 1. Creamos un objeto vacio para el th:object del formulario
        model.addAttribute("cita", new CitaVacunacion());

        // 2. Cargamos los ciudadanos activos de Cordoba para el selector (el procedimiento que definimos antes)
        model.addAttribute("ciudadanos", ciudadanoService.listarCiudadanosActivos());

        // 3. Reutilizamos tu procedimiento auxiliar para cargar los centros disponibles
        cargarCentrosEnModel(model);

        // 4. Retornamos la vista del formulario
        return "cita-form";
    }
}
