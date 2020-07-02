package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;

@JsonAutoDetect(getterVisibility= JsonAutoDetect.Visibility.ANY, setterVisibility = JsonAutoDetect.Visibility.ANY, fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class FastsettBgKunYtelseDto {

    private List<FastsettBrukersAndel> andeler = new ArrayList<>();
    private Boolean skalBrukeBesteberegning;

    public FastsettBgKunYtelseDto() {
        // TODO Auto-generated constructor stub
    }

    @JsonCreator
    public FastsettBgKunYtelseDto(List<FastsettBrukersAndel> andeler, Boolean skalBrukeBesteberegning) {
        this.andeler = andeler;
        this.skalBrukeBesteberegning = skalBrukeBesteberegning;
    }

    public List<FastsettBrukersAndel> getAndeler() {
        return andeler;
    }

    public Boolean getSkalBrukeBesteberegning() {
        return skalBrukeBesteberegning;
    }

    public void setSkalBrukeBesteberegning(Boolean skalBrukeBesteberegning) {
        this.skalBrukeBesteberegning = skalBrukeBesteberegning;
    }

    public void leggTilYtelseAndeler(FastsettBrukersAndel andel) {
        andel.setAndelsnr(andeler.size() + 1);
        andeler.add(andel);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FastsettBgKunYtelseDto that = (FastsettBgKunYtelseDto) o;
        return Objects.equals(andeler, that.andeler) &&
                Objects.equals(skalBrukeBesteberegning, that.skalBrukeBesteberegning);
    }

    @Override
    public int hashCode() {
        return Objects.hash(andeler, skalBrukeBesteberegning);
    }

    @Override
    public String toString() {
        return "YtelseForedeling{" +
                "andeler=" + andeler +
                ", skalBrukeBesteberegning=" + skalBrukeBesteberegning +
                '}';
    }
}
