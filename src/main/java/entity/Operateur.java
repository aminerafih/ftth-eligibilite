package com.orange.maroc.fttheligibilite.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// Cette classe représente un opérateur télécom (Orange, Inwi, IAM)
@Entity
@Table(name = "operateurs")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Operateur {

    // ID unique de l'opérateur
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Nom de l'opérateur (Orange, Inwi, IAM)
    @Column(nullable = false, unique = true)
    private String nom;

    // Priorité de l'opérateur (1 = Orange en premier, 2 = Inwi, 3 = IAM)
    @Column(nullable = false)
    private Integer priorite;

    // Description de l'opérateur
    @Column(length = 500)
    private String description;
}