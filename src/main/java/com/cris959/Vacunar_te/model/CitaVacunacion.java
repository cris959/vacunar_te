/*
Copyright 2026 Christian Garay

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/
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
