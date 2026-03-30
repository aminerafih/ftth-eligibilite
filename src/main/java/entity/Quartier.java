package com.orange.maroc.fttheligibilite.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// Cette classe représente un quartier (district) de Casablanca
@Entity
@Table(name = "quartiers")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Quartier {

    // ID unique du quartier
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Nom du quartier (ex: BELVEDERE, FONCIERE, HOPITAUX)
    @Column(nullable = false, unique = true)
    private String nom;

    // Code du quartier pour identifier la zone
    @Column(unique = true)
    private String code;

    // Description du quartier
    @Column(length = 500)
    private String description;
}