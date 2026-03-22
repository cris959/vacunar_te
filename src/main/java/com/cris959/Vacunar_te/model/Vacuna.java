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

import com.cris959.Vacunar_te.model.enums.EstadoVacuna;
import com.cris959.Vacunar_te.model.enums.MedidaDosis;
import com.cris959.Vacunar_te.model.enums.TipoAntigeno;
import jakarta.persistence.*;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
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
    @Column(name = "id_vacuna")
    private int idVacuna;

    @Column(name = "nro_serie_dosis", length = 20, nullable = false)
    @Size(min = 10, max = 20, message = "El número de serie debe tener entre 10 y 20 dígitos")
    @Pattern(regexp = "^[0-9]{10,20}$", message = "El número de serie debe contener entre 10 y 20 dígitos numéricos")
    private String nroSerieDosis;

    @Enumerated(EnumType.STRING)
    @Column(name = "medida")
    private MedidaDosis medida;

    @Enumerated(EnumType.STRING)
    @Column(name = "antigeno")
    private TipoAntigeno antigeno;

    @Column(name = "fecha_caduca")
    private LocalDate fechaCaduca;

    @Enumerated(EnumType.STRING)
    private EstadoVacuna estado = EstadoVacuna.DISPONIBLE;

    @Column(name = "cantidad_dosis", nullable = false)
    private int cantidadDosis;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "laboratorio")
    private Laboratorio laboratorio;
}
