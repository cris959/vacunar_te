package com.cris959.Vacunar_te.model.enums;

public enum MedidaDosis {
    TRES_ML(0.3),
    CINCO_ML(0.5),
    NUEVE_ML(0.9);

    private final double valor;
    MedidaDosis(double valor) { this.valor = valor; }
    public double getValor() { return valor; }
}
