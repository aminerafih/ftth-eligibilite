package com.orange.maroc.fttheligibilite.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class OrangeResponseDTO {
    private Integer searchCount;
    private List<ImmeubleOrangeDTO> searchHits;
    private InwiResponseDTO searchHitsExterne;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ImmeubleOrangeDTO {
        private String id;
        private String propertyType;
        private String eligibility;
        private GmapDTO gmap;
        private FullAddressDTO fullAddress;
        private String address;
        private Integer apartments;
        private Integer clients;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class GmapDTO {
        private Double latitude;
        private Double longitude;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class FullAddressDTO {
        private String city;
        private String district;
        private String street;
        private String projectName;
        private String propertyName;
    }
}