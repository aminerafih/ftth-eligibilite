package com.orange.maroc.fttheligibilite.repository;

import com.orange.maroc.fttheligibilite.entity.Quartier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface QuartierRepository extends JpaRepository<Quartier, Long> {
    Optional<Quartier> findByNom(String nom);
    Optional<Quartier> findByCode(String code);
}