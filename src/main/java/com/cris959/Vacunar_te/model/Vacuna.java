package com.cris959.Vacunar_te.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Vacuna {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idVacuna;

    private int nroSerieDosis;

    private String marca;

    private double medida;

    private String antigeno;

    private LocalDate fechaCaduca;

    private boolean colocada;

    @ManyToOne
    @JoinColumn(name = "laboratorio")
    private Laboratorio laboratorio;
}
