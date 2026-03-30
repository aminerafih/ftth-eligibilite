package com.orange.maroc.fttheligibilite.repository;

import com.orange.maroc.fttheligibilite.entity.Immeuble;
import com.orange.maroc.fttheligibilite.entity.Operateur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ImmeubleRepository extends JpaRepository<Immeuble, Long> {
    Optional<Immeuble> findByIdExterne(String idExterne);
    List<Immeuble> findByOperateur(Operateur operateur);
    List<Immeuble> findByLatitudeAndLongitude(Double latitude, Double longitude);

    @Query(value = "SELECT * FROM immeubles WHERE " +
            "(6371 * acos(cos(radians(:latitude)) * cos(radians(latitude)) * " +
            "cos(radians(longitude) - radians(:longitude)) + " +
            "sin(radians(:latitude)) * sin(radians(latitude)))) <= :rayon",
            nativeQuery = true)
    List<Immeuble> findImmeublesDansRayon(@Param("latitude") Double latitude,
                                          @Param("longitude") Double longitude,
                                          @Param("rayon") Double rayon);
}