package com.orange.maroc.fttheligibilite.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EligibiliteResponseDTO {
    private String statut;
    private String quartier;
    private String operateur;
    private Integer nombreAppartements;
    private Double distanceMetres;
    private String message;
}