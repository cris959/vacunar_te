package com.cris959.Vacunar_te.infra.springai;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ChatConfig {

    @Bean
    public ChatClient chatClient(ChatClient.Builder builder) {
        return builder
                .defaultSystem("""
                    Eres el asistente virtual de 'Vacunar-te'.
                    Tu objetivo es ayudar a ciudadanos de Córdoba a elegir fechas de vacunación.
                    REGLAS CRÍTICAS:
                    1. Solo puedes sugerir turnos de Lunes a Viernes.
                    2. El centro de salud está CERRADO sábados, domingos y feriados nacionales.
                    3. Si el usuario pide un fin de semana, ofrece el lunes siguiente.
                    4. Sé cordial y profesional.
                    """)
                .build();
    }
}
