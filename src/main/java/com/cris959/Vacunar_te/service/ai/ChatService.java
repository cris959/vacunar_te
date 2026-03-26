package com.cris959.Vacunar_te.service.ai;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class ChatService {

    private final ChatClient chatClient;

    public ChatService(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    public String pedirSugerenciaFecha(String mensajeUsuario) {
        // Obtenemos la fecha y el dia de la semana actual en Argentina
        LocalDate hoy = LocalDate.now();
        String diaSemana = hoy.getDayOfWeek().toString(); // Ej: THURSDAY

        // Creamos un contexto dinamico que se envia "invisible" para el usuario
        String mensajeConContexto = String.format(
                "Contexto actual: Hoy es %s, %s. " +
                        "Mensaje del usuario: %s",
                diaSemana, hoy, mensajeUsuario
        );

        return chatClient.prompt()
                .user(mensajeConContexto)
                .call()
                .content();
    }
}
