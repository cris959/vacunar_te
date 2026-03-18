package com.cris959.Vacunar_te.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Ciudadano {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int dni;

    private String nombreCompleto;

    private String email;

    private String celular;

    private String patologia;

    private String ambitoTrabajo;

    @OneToMany(mappedBy = "ciudadano")
    private List<CitaVacunacion> citas;
}
