package com.orange.maroc.fttheligibilite.repository;

import com.orange.maroc.fttheligibilite.entity.Operateur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface OperateurRepository extends JpaRepository<Operateur, Long> {
    Optional<Operateur> findByNom(String nom);
    Optional<Operateur> findByPriorite(Integer priorite);
}