package com.cris959.Vacunar_te.model.enums;

public enum EstadoVacuna {
    DISPONIBLE, // Lista para ser asignada a una cita
    APLICADA,   // Ya fue colocada a un ciudadano
    VENCIDA,    // Supero la fecha de caducidad
    DESECHADA   // Por rotura o perdida de cadena de frio
}
