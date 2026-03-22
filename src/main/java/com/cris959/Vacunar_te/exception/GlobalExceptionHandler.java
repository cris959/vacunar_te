package com.cris959.Vacunar_te.exception;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@ControllerAdvice
public class GlobalExceptionHandler {

    // 1. Captura errores de logica de turnos (Ej: Intervalo de 28 dias)
    @ExceptionHandler(CitaInvalidaException.class)
    public String manejarCitaInvalida(CitaInvalidaException ex, RedirectAttributes flash) {
        flash.addFlashAttribute("error", ex.getMessage());
        return "redirect:/citas/solicitar";
    }

    // 2. Captura errores de registro (Ej: DNI duplicado)
    @ExceptionHandler(RegistroVacunacionException.class)
    public String manejarRegistroFallido(RegistroVacunacionException ex, Model model) {
        model.addAttribute("error", ex.getMessage());
        // Aqui puedes decidir si redirigir o volver a la vista del formulario
        return "ciudadano-form";
    }

    // 3. Captura cualquier otro error inesperado del sistema
    @ExceptionHandler(Exception.class)
    public String manejarErroresGenerales(Exception ex, Model model) {
        model.addAttribute("error", "Ocurrio un error inesperado: " + ex.getMessage());
        return "error"; // Requiere que tengas una vista error.html
    }
}
