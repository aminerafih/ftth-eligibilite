package com.orange.maroc.fttheligibilite.controller;

import com.orange.maroc.fttheligibilite.dto.EligibiliteResponseDTO;
import com.orange.maroc.fttheligibilite.service.FtthEligibiliteService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

// Contrôleur REST pour gérer les endpoints d'éligibilité
@RestController
@RequestMapping("/api/v1/eligibilite")
@Slf4j
@CrossOrigin(origins = "*")
public class EligibiliteController {

    // Injection du service
    @Autowired
    private FtthEligibiliteService ftthEligibiliteService;

    // Endpoint pour vérifier l'éligibilité par coordonnées GPS
    // POST /api/v1/eligibilite/check?latitude=33.5925967&longitude=-7.5991674
    @PostMapping("/check")
    public ResponseEntity<EligibiliteResponseDTO> verifierEligibilite(
            @RequestParam(name = "latitude", required = true) Double latitude,
            @RequestParam(name = "longitude", required = true) Double longitude) {

        log.info("================================");
        log.info("NOUVELLE DEMANDE REÇUE");
        log.info("Latitude : {}", latitude);
        log.info("Longitude : {}", longitude);
        log.info("================================");

        // Valider les paramètres
        if (latitude == null || longitude == null) {
            log.warn("Paramètres invalides : latitude ou longitude manquante");
            EligibiliteResponseDTO errorResponse = new EligibiliteResponseDTO();
            errorResponse.setStatut("ERREUR");
            errorResponse.setMessage("Les paramètres latitude et longitude sont obligatoires");
            return ResponseEntity.badRequest().body(errorResponse);
        }

        // Valider que les coordonnées sont dans une plage valide
        if (latitude < -90 || latitude > 90 || longitude < -180 || longitude > 180) {
            log.warn("Coordonnées GPS invalides");
            EligibiliteResponseDTO errorResponse = new EligibiliteResponseDTO();
            errorResponse.setStatut("ERREUR");
            errorResponse.setMessage("Coordonnées GPS invalides. Latitude : -90 à 90, Longitude : -180 à 180");
            return ResponseEntity.badRequest().body(errorResponse);
        }

        try {
            // Appeler le service pour vérifier l'éligibilité
            log.info("Appel du service FtthEligibiliteService...");
            EligibiliteResponseDTO response = ftthEligibiliteService.verifierEligibilite(latitude, longitude);

            log.info("Réponse finale : Statut = {}", response.getStatut());
            log.info("================================");

            // Retourner la réponse avec le statut HTTP 200 OK
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Erreur lors de la vérification d'éligibilité", e);

            EligibiliteResponseDTO errorResponse = new EligibiliteResponseDTO();
            errorResponse.setStatut("ERREUR");
            errorResponse.setMessage("Une erreur s'est produite lors du traitement de votre demande");

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    // Endpoint de santé pour vérifier que le serveur est actif
    // GET /api/v1/eligibilite/health
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        log.info("Vérification de santé du serveur");
        return ResponseEntity.ok("Serveur de vérification d'éligibilité FTTH actif et opérationnel");
    }
}
