package com.orange.maroc.fttheligibilite;

import com.orange.maroc.fttheligibilite.entity.Operateur;
import com.orange.maroc.fttheligibilite.repository.OperateurRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import java.util.Optional;

@Component
@Slf4j
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private OperateurRepository operateurRepository;

    @Override
    public void run(String... args) throws Exception {
        log.info("Initialisation des données...");

        creerOperateurSiNexistePas("Orange", 1, "Opérateur Orange Maroc - Fibre FTTH");
        creerOperateurSiNexistePas("Inwi", 2, "Opérateur Inwi - Fibre FTTH");
        creerOperateurSiNexistePas("IAM", 3, "Opérateur Maroc Telecom (IAM) - Fibre FTTH");

        log.info("Initialisation des données terminée");
    }

    private void creerOperateurSiNexistePas(String nom, Integer priorite, String description) {
        Optional<Operateur> existant = operateurRepository.findByNom(nom);

        if (existant.isPresent()) {
            log.info("Opérateur {} existe déjà", nom);
            return;
        }

        Operateur operateur = new Operateur();
        operateur.setNom(nom);
        operateur.setPriorite(priorite);
        operateur.setDescription(description);

        operateurRepository.save(operateur);
        log.info("Opérateur {} créé avec succès", nom);
    }
}