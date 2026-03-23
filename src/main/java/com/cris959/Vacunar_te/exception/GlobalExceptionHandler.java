package com.cris959.Vacunar_te.exception;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.resource.NoResourceFoundException;


@ControllerAdvice
public class GlobalExceptionHandler {

    // Captura errores de validación de negocio (ej: CUIT duplicado)
    @ExceptionHandler(RegistroVacunacionException.class)
    public String handleRegistroVacunacion(RegistroVacunacionException ex, Model model) {
        model.addAttribute("errorTitulo", "Error de Validación");
        model.addAttribute("errorMsg", ex.getMessage());
        model.addAttribute("codigo", "400 - Bad Request");
        return "error";
    }

    // Captura cualquier otro error interno de Java (el famoso 500)
    @ExceptionHandler(Exception.class)
    public String handleAllExceptions(Exception ex, Model model) {
        model.addAttribute("errorTitulo", "Error Crítico");
        model.addAttribute("errorMsg", "Error inesperado: " + ex.getMessage());
        model.addAttribute("codigo", "500");
        return "error";
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public String handleNoResourceFound(NoResourceFoundException ex, Model model) {
        model.addAttribute("errorTitulo", "Ruta Inválida");
        model.addAttribute("errorMsg", "La página '" + ex.getResourcePath() + "' no existe. Por favor, verificá que la URL sea correcta (ej: /laboratorios/listado).");
        model.addAttribute("codigo", "404");
        return "error";
    }
}
