package com.cris959.Vacunar_te.model.enums;

public enum DosisRefuerzo {
    PRIMERA(1),
    SEGUNDA(2),
    TERCERA(3);

    private final int valor;
    DosisRefuerzo(int valor) { this.valor = valor; }
    public int getValor() { return valor; }
}
