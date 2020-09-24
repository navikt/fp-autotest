package no.nav.foreldrepenger.autotest.klienter.fpsak.prosesstask.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonAutoDetect(getterVisibility= JsonAutoDetect.Visibility.ANY, setterVisibility = JsonAutoDetect.Visibility.ANY, fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class SokeFilterDto {
    private List<ProsessTaskStatusDto> prosessTaskStatuser = new ArrayList<>();
    private LocalDateTime sisteKjoeretidspunktFraOgMed;
    private LocalDateTime sisteKjoeretidspunktTilOgMed;

    public SokeFilterDto() {

    }

    @JsonCreator
    public SokeFilterDto(List<ProsessTaskStatusDto> prosessTaskStatuser, LocalDateTime sisteKjoeretidspunktFraOgMed,
                         LocalDateTime sisteKjoeretidspunktTilOgMed) {
        this.prosessTaskStatuser = prosessTaskStatuser;
        this.sisteKjoeretidspunktFraOgMed = sisteKjoeretidspunktFraOgMed;
        this.sisteKjoeretidspunktTilOgMed = sisteKjoeretidspunktTilOgMed;
    }

    public List<ProsessTaskStatusDto> getProsessTaskStatuser() {
        return prosessTaskStatuser;
    }

    public LocalDateTime getSisteKjoeretidspunktFraOgMed() {
        return sisteKjoeretidspunktFraOgMed;
    }

    public LocalDateTime getSisteKjoeretidspunktTilOgMed() {
        return sisteKjoeretidspunktTilOgMed;
    }

    public SokeFilterDto setSisteKjoeretidspunktFraOgMed(LocalDateTime tid) {
        this.sisteKjoeretidspunktFraOgMed = tid;
        return this;
    }

    public SokeFilterDto setSisteKjoeretidspunktTilOgMed(LocalDateTime tid) {
        this.sisteKjoeretidspunktTilOgMed = tid;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SokeFilterDto that = (SokeFilterDto) o;
        return Objects.equals(prosessTaskStatuser, that.prosessTaskStatuser) &&
                Objects.equals(sisteKjoeretidspunktFraOgMed, that.sisteKjoeretidspunktFraOgMed) &&
                Objects.equals(sisteKjoeretidspunktTilOgMed, that.sisteKjoeretidspunktTilOgMed);
    }

    @Override
    public int hashCode() {
        return Objects.hash(prosessTaskStatuser, sisteKjoeretidspunktFraOgMed, sisteKjoeretidspunktTilOgMed);
    }

    @Override
    public String toString() {
        return "SokeFilterDto{" +
                "prosessTaskStatuser=" + prosessTaskStatuser +
                ", sisteKjoeretidspunktFraOgMed=" + sisteKjoeretidspunktFraOgMed +
                ", sisteKjoeretidspunktTilOgMed=" + sisteKjoeretidspunktTilOgMed +
                '}';
    }
}
