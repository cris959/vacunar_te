package com.cris959.Vacunar_te.controller;

import com.cris959.Vacunar_te.model.Ciudadano;
import com.cris959.Vacunar_te.model.enums.AmbitoTrabajo;
import com.cris959.Vacunar_te.service.CiudadanoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
    @GetMapping("/listado")
    public String listarCiudadanos(Model model) {
        model.addAttribute("ciudadanos", ciudadanoService.listarTodos());
        return "ciudadano-listado"; // Nombre del HTML que crearemos abajo
    }
}
