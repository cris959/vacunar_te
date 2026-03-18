package com.cris959.Vacunar_te.model;

import com.cris959.Vacunar_te.model.enums.DosisRefuerzo;
import com.cris959.Vacunar_te.model.enums.EstadoCita;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class CitaVacunacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int codCita;


    @ManyToOne
    @JoinColumn(name = "dni_ciudadano")
    private Ciudadano ciudadano;

    private String centroVacunacion;

    @Enumerated(EnumType.ORDINAL) // Aqui usamos el numero (1, 2, 3)
    private DosisRefuerzo codRefuerzo;

    private LocalDateTime fechaHoraCita;

    private LocalDateTime fechaHoraColoca;

    
    @OneToOne
    @JoinColumn(name = "id_vacuna")
    private Vacuna vacuna;

    @Enumerated(EnumType.STRING)
    private EstadoCita estado;
}
