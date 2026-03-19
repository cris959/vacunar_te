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

import com.cris959.Vacunar_te.model.enums.AmbitoTrabajo;
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
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int dni;

    private String nombreCompleto;

    @Column(unique = true)
    private String email;

    private String celular;

    private String patologia;

    @Enumerated(EnumType.STRING)
    private AmbitoTrabajo ambitoTrabajo;

    @OneToMany(mappedBy = "ciudadano", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<CitaVacunacion> citas;

    @Column(name = "activo", nullable = false)
    private boolean activo = true; // Por defecto todos nacen activos
}
