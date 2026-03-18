package com.cris959.Vacunar_te.model;

import com.cris959.Vacunar_te.model.enums.EstadoVacuna;
import com.cris959.Vacunar_te.model.enums.MedidaDosis;
import com.cris959.Vacunar_te.model.enums.TipoAntigeno;
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

    @Enumerated(EnumType.STRING)
    private MedidaDosis medida;

    @Enumerated(EnumType.STRING)
    private TipoAntigeno antigeno;

    private LocalDate fechaCaduca;

    @Enumerated(EnumType.STRING)
    private EstadoVacuna estado = EstadoVacuna.DISPONIBLE;

    @ManyToOne
    @JoinColumn(name = "laboratorio")
    private Laboratorio laboratorio;
}
