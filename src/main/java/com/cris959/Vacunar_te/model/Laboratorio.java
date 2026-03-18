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
public class Laboratorio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idLaboratorio;

    @Column(unique = true, length = 15)
    private String cuit;

    private String nomComercial;

    private String pais;

    private String domComercial;

    @OneToMany(mappedBy = "laboratorio", cascade = CascadeType.ALL)
    private List<Vacuna> vacunas;
}
