package com.cris959.Vacunar_te.model;

import com.cris959.Vacunar_te.model.enums.DosisRefuerzo;
import com.cris959.Vacunar_te.model.enums.EstadoCita;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

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
    @JoinColumn(name = "dni_ciudadano", referencedColumnName = "dni")
    private Ciudadano ciudadano;

    @Column(name = "centro_vacunacion")
    private String centroVacunacion;

    @Enumerated(EnumType.STRING)
    @Column(name = "cod_refuerzo", nullable = false) // Mantenemos el NOT NULL
    private DosisRefuerzo codRefuerzo;

    @Column(name = "fecha_hora_cita", columnDefinition = "DATETIME(0)")
    private LocalDateTime fechaHoraCita;

    @Column(name = "fecha_hora_coloca", columnDefinition = "DATETIME(0)")
    private LocalDateTime fechaHoraColoca;

    
    @OneToOne
    @JoinColumn(name = "id_vacuna")
    private Vacuna vacuna;

    @Enumerated(EnumType.STRING)
    private EstadoCita estado;
}
