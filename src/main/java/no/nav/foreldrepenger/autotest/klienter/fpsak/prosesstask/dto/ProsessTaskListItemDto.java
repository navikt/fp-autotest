package no.nav.foreldrepenger.autotest.klienter.fpsak.prosesstask.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ProsessTaskListItemDto(int id,
                                     String taskType,
                                     String nesteKjøringEtter,
                                     String gruppe,
                                     String sekvens,
                                     String status,
                                     String sistKjørt,
                                     String sisteFeilKode,
                                     TaskParametereDto taskParametre){

}
