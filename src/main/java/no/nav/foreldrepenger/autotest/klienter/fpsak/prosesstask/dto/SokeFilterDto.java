package no.nav.foreldrepenger.autotest.klienter.fpsak.prosesstask.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record SokeFilterDto(List<ProsessTaskStatusDto> prosessTaskStatuser, LocalDateTime sisteKjoeretidspunktFraOgMed, LocalDateTime sisteKjoeretidspunktTilOgMed) {

}
