package com.cris959.Vacunar_te.controller;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class CustomErrorController implements ErrorController {

    @RequestMapping("/error")
    public String handleError(HttpServletRequest request, Model model) {
        // Obtenemos el codigo de estado (404, 500, etc.)
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);

        String titulo = "Ocurrió un problema";
        String mensaje = "Lo sentimos, hubo un error inesperado en el sistema.";
        String codigo = "Error";

        if (status != null) {
            Integer statusCode = Integer.valueOf(status.toString());
            codigo = statusCode.toString();

            if (statusCode == 404) {
                titulo = "Página no encontrada";
                mensaje = "La ruta que ingresaste no existe. Verificá si escribiste bien /laboratorios/listado.";
            } else if (statusCode == 500) {
                titulo = "Error Interno";
                mensaje = "Hubo un problema en el servidor. Revisá la conexión a la base de datos.";
            } else if (statusCode == 403) {
                titulo = "Acceso Denegado";
                mensaje = "No tenés permisos para ver este contenido.";
            }
        }

        model.addAttribute("errorTitulo", titulo);
        model.addAttribute("errorMsg", mensaje);
        model.addAttribute("codigo", codigo);

        return "error"; // Busca el archivo error.html en templates
    }
}
