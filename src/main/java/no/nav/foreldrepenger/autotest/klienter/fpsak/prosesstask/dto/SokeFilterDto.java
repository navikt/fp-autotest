package no.nav.foreldrepenger.autotest.klienter.fpsak.prosesstask.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonAutoDetect(getterVisibility= JsonAutoDetect.Visibility.ANY, setterVisibility = JsonAutoDetect.Visibility.ANY, fieldVisibility = JsonAutoDetect.Visibility.ANY)
public record SokeFilterDto(List<ProsessTaskStatusDto> prosessTaskStatuser, LocalDateTime sisteKjoeretidspunktFraOgMed, LocalDateTime sisteKjoeretidspunktTilOgMed) {

}
