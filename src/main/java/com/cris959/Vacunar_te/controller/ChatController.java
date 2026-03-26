package com.cris959.Vacunar_te.controller;

import com.cris959.Vacunar_te.service.ai.ChatService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    /**
     * Endpoint para recibir preguntas del usuario.
     * Ejemplo: /api/chat/preguntar?mensaje=¿Que dia me puedo vacunar?
     */
    @GetMapping("/preguntar")
    public Map<String, String> preguntar(@RequestParam String mensaje) {
        // Llamamos al servicio que interactua con Mistral
        String respuesta = chatService.pedirSugerenciaFecha(mensaje);

        // Devolvemos un JSON para que el JS de la vista lo procese facilmente
        return Map.of("respuesta", respuesta);
    }
}
