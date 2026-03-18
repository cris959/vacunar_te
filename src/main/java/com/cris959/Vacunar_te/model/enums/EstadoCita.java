package com.cris959.Vacunar_te.model.enums;

public enum EstadoCita {
    PROGRAMADA("La cita está pendiente de asistencia"),
    CUMPLIDA("El ciudadano ya recibió la dosis"),
    CANCELADA("La cita fue anulada"),
    POSTERGADA("La cita se movió 2 semanas por falta de dosis");

    private final String descripcion;

    EstadoCita(String descripcion) {
        this.descripcion = descripcion;
    }
}
