package com.orange.maroc.fttheligibilite.service;

import com.orange.maroc.fttheligibilite.dto.EligibiliteResponseDTO;
import com.orange.maroc.fttheligibilite.dto.OrangeResponseDTO;
import com.orange.maroc.fttheligibilite.dto.InwiResponseDTO;
import com.orange.maroc.fttheligibilite.entity.Immeuble;
import com.orange.maroc.fttheligibilite.entity.Operateur;
import com.orange.maroc.fttheligibilite.entity.Quartier;
import com.orange.maroc.fttheligibilite.repository.ImmeubleRepository;
import com.orange.maroc.fttheligibilite.repository.OperateurRepository;
import com.orange.maroc.fttheligibilite.repository.QuartierRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import lombok.extern.slf4j.Slf4j;
import java.util.Optional;

@Service
@Slf4j
public class FtthEligibiliteService {

    private static final String API_BASE_URL = "http://10.127.16.23/ftth/search/nearby";
    private static final String JWT_TOKEN = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJodHRwOi8vd3NvMi5vcmcvY2xhaW1zL3N1YnNjcmliZXIiOiJpYW0iLCJodHRwOi8vd3NvMi5vcmcvY2xhaW1zL2FwcGxpY2F0aW9ubmFtZSI6IkRlZmF1bHRBcHBsaWNhdGlvbiJ9.3GWUkQdW-rg5ikjwZERUcUKVw4kk6fSffNWjwiVkMx8";
    private static final double RAYON_INWI_KM = 0.1;

    @Autowired
    private ImmeubleRepository immeubleRepository;

    @Autowired
    private OperateurRepository operateurRepository;

    @Autowired
    private QuartierRepository quartierRepository;

    @Autowired
    private RestTemplate restTemplate;

    public EligibiliteResponseDTO verifierEligibilite(Double latitude, Double longitude) {
        log.info("Vérification éligibilité pour coordonnées GPS : {}, {}", latitude, longitude);

        try {
            log.info("Appel API Orange...");
            OrangeResponseDTO orangeResponse = appelApiOrange(latitude, longitude);

            if (orangeResponse != null && orangeResponse.getSearchHits() != null &&
                    !orangeResponse.getSearchHits().isEmpty()) {

                log.info("Résultats Orange trouvés !");
                OrangeResponseDTO.ImmeubleOrangeDTO immeubleOrange = orangeResponse.getSearchHits().get(0);
                sauvegarderImmeubleOrange(immeubleOrange);
                return creerReponseEligibleOrange(immeubleOrange);
            }

            log.info("Aucun résultat Orange, appel API Inwi...");
            OrangeResponseDTO inwiResponse = appelApiInwi(latitude, longitude);

            if (inwiResponse != null && inwiResponse.getSearchHitsExterne() != null &&
                    inwiResponse.getSearchHitsExterne().getCharacteristic() != null &&
                    !inwiResponse.getSearchHitsExterne().getCharacteristic().isEmpty()) {

                log.info("Résultats Inwi trouvés !");
                return creerReponseEligibleInwi(inwiResponse, latitude, longitude);
            }

            log.info("Aucun résultat Orange ni Inwi = NON ELIGIBLE");
            return creerReponseNonEligible();

        } catch (Exception e) {
            log.error("Erreur lors de la vérification d'éligibilité", e);
            return creerReponseErreur();
        }
    }

    private OrangeResponseDTO appelApiOrange(Double latitude, Double longitude) {
        try {
            String url = API_BASE_URL + "/" + latitude + "," + longitude;
            log.info("URL Orange : {}", url);

            HttpHeaders headers = creerHeaders();
            HttpEntity<String> entity = new HttpEntity<>(headers);

            log.info("Appel API Orange...");
            ResponseEntity<OrangeResponseDTO> response = restTemplate.exchange(
                    url, HttpMethod.GET, entity, OrangeResponseDTO.class);

            OrangeResponseDTO body = response.getBody();
            log.info("Réponse Orange reçue : SearchCount = {}", body != null ? body.getSearchCount() : "null");

            if (body != null && body.getSearchHits() != null) {
                log.info("Nombre de SearchHits Orange : {}", body.getSearchHits().size());
            }

            return body;
        } catch (Exception e) {
            log.error("Erreur lors de l'appel API Orange", e);
            e.printStackTrace();
            return null;
        }
    }

    private OrangeResponseDTO appelApiInwi(Double latitude, Double longitude) {
        try {
            String url = API_BASE_URL + "/" + latitude + "," + longitude + "?operator=inwi";
            log.info("URL Inwi : {}", url);

            HttpHeaders headers = crierHeaders();
            HttpEntity<String> entity = new HttpEntity<>(headers);

            log.info("Appel API Inwi...");
            ResponseEntity<OrangeResponseDTO> response = restTemplate.exchange(
                    url, HttpMethod.GET, entity, OrangeResponseDTO.class);

            OrangeResponseDTO body = response.getBody();
            log.info("Réponse Inwi reçue : SearchCount = {}", body != null ? body.getSearchCount() : "null");

            if (body != null && body.getSearchHitsExterne() != null) {
                log.info("Nombre de SearchHitsExterne Inwi : {}",
                        body.getSearchHitsExterne().getCharacteristic() != null ?
                                body.getSearchHitsExterne().getCharacteristic().size() : 0);
            }

            return body;
        } catch (Exception e) {
            log.error("Erreur lors de l'appel API Inwi", e);
            e.printStackTrace();
            return null;
        }
    }

    private HttpHeaders crierHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-JWT-Assertion", JWT_TOKEN);
        headers.set("X-OAPI-Application-Name", "inwi");
        headers.set("Content-Type", "application/json");
        return headers;
    }

    private HttpHeaders creerHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-JWT-Assertion", JWT_TOKEN);
        headers.set("X-OAPI-Application-Name", "inwi");
        headers.set("Content-Type", "application/json");
        return headers;
    }

    private void sauvegarderImmeubleOrange(OrangeResponseDTO.ImmeubleOrangeDTO immeubleOrange) {
        try {
            Optional<Immeuble> existant = immeubleRepository.findByIdExterne(immeubleOrange.getId());
            if (existant.isPresent()) {
                log.debug("Immeuble {} existe déjà en base", immeubleOrange.getId());
                return;
            }

            Optional<Operateur> operateur = operateurRepository.findByNom("Orange");
            if (operateur.isEmpty()) {
                log.warn("Opérateur Orange non trouvé en base");
                return;
            }

            String nomQuartier = immeubleOrange.getFullAddress().getDistrict();
            Optional<Quartier> quartier = quartierRepository.findByNom(nomQuartier);

            Quartier quartierFinal;
            if (quartier.isPresent()) {
                quartierFinal = quartier.get();
            } else {
                Quartier nouveauQuartier = new Quartier();
                nouveauQuartier.setNom(nomQuartier);
                quartierFinal = quartierRepository.save(nouveauQuartier);
            }

            Immeuble immeuble = new Immeuble();
            immeuble.setIdExterne(immeubleOrange.getId());
            immeuble.setNom(immeubleOrange.getFullAddress().getPropertyName());
            immeuble.setAdresse(immeubleOrange.getAddress());
            immeuble.setLatitude(immeubleOrange.getGmap().getLatitude());
            immeuble.setLongitude(immeubleOrange.getGmap().getLongitude());
            immeuble.setNombreAppartements(immeubleOrange.getApartments());
            immeuble.setNombreClients(immeubleOrange.getClients());
            immeuble.setOperateur(operateur.get());
            immeuble.setQuartier(quartierFinal);
            immeuble.setTypeProriete(immeubleOrange.getPropertyType());
            immeuble.setEligibilite(immeubleOrange.getEligibility());

            immeubleRepository.save(immeuble);
            log.info("Immeuble {} sauvegardé en base", immeuble.getNom());

        } catch (Exception e) {
            log.error("Erreur lors de la sauvegarde de l'immeuble", e);
        }
    }

    private EligibiliteResponseDTO creerReponseEligibleOrange(OrangeResponseDTO.ImmeubleOrangeDTO immeubleOrange) {
        EligibiliteResponseDTO response = new EligibiliteResponseDTO();
        response.setStatut("ELIGIBLE");
        response.setQuartier(immeubleOrange.getFullAddress().getDistrict());
        response.setOperateur("Orange");
        response.setNombreAppartements(immeubleOrange.getApartments());
        response.setDistanceMetres(0.0);
        response.setMessage("Vous êtes éligible à la fibre Orange FTTH. L'immeuble est compatible avec la fibre.");
        return response;
    }

    private EligibiliteResponseDTO creerReponseEligibleInwi(OrangeResponseDTO inwiResponse, Double latitude, Double longitude) {
        EligibiliteResponseDTO response = new EligibiliteResponseDTO();
        response.setStatut("ELIGIBLE");

        java.util.List<InwiResponseDTO.CharacteristicDTO> firstResult =
                inwiResponse.getSearchHitsExterne().getCharacteristic().get(0);

        String quartier = "";
        for (InwiResponseDTO.CharacteristicDTO char_dto : firstResult) {
            if ("district".equals(char_dto.getName())) {
                quartier = char_dto.getId();
            }
        }

        response.setQuartier(quartier);
        response.setOperateur("Inwi");
        response.setNombreAppartements(null);
        response.setDistanceMetres(100.0);
        response.setMessage("Vous êtes éligible à la fibre Inwi FTTH dans un rayon de 100 mètres.");
        return response;
    }

    private EligibiliteResponseDTO creerReponseNonEligible() {
        EligibiliteResponseDTO response = new EligibiliteResponseDTO();
        response.setStatut("NON_ELIGIBLE");
        response.setQuartier(null);
        response.setOperateur(null);
        response.setNombreAppartements(null);
        response.setDistanceMetres(null);
        response.setMessage("Malheureusement, vous n'êtes pas éligible à la fibre FTTH.");
        return response;
    }

    private EligibiliteResponseDTO creerReponseErreur() {
        EligibiliteResponseDTO response = new EligibiliteResponseDTO();
        response.setStatut("ERREUR");
        response.setQuartier(null);
        response.setOperateur(null);
        response.setNombreAppartements(null);
        response.setDistanceMetres(null);
        response.setMessage("Une erreur s'est produite lors de la vérification.");
        return response;
    }
}