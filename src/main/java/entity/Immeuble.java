package com.orange.maroc.fttheligibilite.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// Cette classe représente un immeuble (bâtiment) dans la ville
@Entity
@Table(name = "immeubles")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Immeuble {

    // ID unique de l'immeuble
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ID externe fourni par l'API Orange
    @Column(unique = true)
    private String idExterne;

    // Nom de l'immeuble (ex: AFRIQUIA 2 CODE 01.5.31.901)
    @Column(nullable = false)
    private String nom;

    // Adresse complète de l'immeuble
    @Column(nullable = false, length = 500)
    private String adresse;

    // Latitude GPS
    @Column(nullable = false)
    private Double latitude;

    // Longitude GPS
    @Column(nullable = false)
    private Double longitude;

    // Nombre d'appartements dans l'immeuble
    @Column(nullable = false)
    private Integer nombreAppartements;

    // Nombre de clients actuels
    @Column(nullable = false)
    private Integer nombreClients;

    // L'opérateur qui possède la fibre dans cet immeuble
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "operateur_id", nullable = false)
    private Operateur operateur;

    // Le quartier auquel appartient cet immeuble
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "quartier_id", nullable = false)
    private Quartier quartier;

    // Type de propriété (building, apartment, etc.)
    @Column(length = 100)
    private String typeProriete;

    // État d'éligibilité (raccordable, non raccordable, etc.)
    @Column(length = 100)
    private String eligibilite;
}