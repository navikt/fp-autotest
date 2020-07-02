package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;

@JsonAutoDetect(getterVisibility= JsonAutoDetect.Visibility.ANY, setterVisibility = JsonAutoDetect.Visibility.ANY, fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class BesteberegningFødendeKvinneDto {

    private List<BesteberegningFødendeKvinneAndelDto> besteberegningAndelListe = new ArrayList<>();
    private DagpengeAndelLagtTilBesteberegningDto nyDagpengeAndel;

    public BesteberegningFødendeKvinneDto() {
    }

    @JsonCreator
    public BesteberegningFødendeKvinneDto(List<BesteberegningFødendeKvinneAndelDto> besteberegningAndelListe,
                                          DagpengeAndelLagtTilBesteberegningDto nyDagpengeAndel) {
        this.besteberegningAndelListe = besteberegningAndelListe;
        this.nyDagpengeAndel = nyDagpengeAndel;
    }

    public void leggTilBesteberegningAndel(BesteberegningFødendeKvinneAndelDto andel) {
        andel.setAndelsnr(besteberegningAndelListe.size() + 1);
        besteberegningAndelListe.add(andel);
    }

    public void setBesteberegningAndelListe(List<BesteberegningFødendeKvinneAndelDto> besteberegningAndelListe) {
        this.besteberegningAndelListe = besteberegningAndelListe;
    }

    public void setNyDagpengeAndel(DagpengeAndelLagtTilBesteberegningDto nyDagpengeAndel) {
        this.nyDagpengeAndel = nyDagpengeAndel;
    }

    public List<BesteberegningFødendeKvinneAndelDto> getBesteberegningAndelListe() {
        return besteberegningAndelListe;
    }

    public DagpengeAndelLagtTilBesteberegningDto getNyDagpengeAndel() {
        return nyDagpengeAndel;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BesteberegningFødendeKvinneDto that = (BesteberegningFødendeKvinneDto) o;
        return Objects.equals(besteberegningAndelListe, that.besteberegningAndelListe) &&
                Objects.equals(nyDagpengeAndel, that.nyDagpengeAndel);
    }

    @Override
    public int hashCode() {
        return Objects.hash(besteberegningAndelListe, nyDagpengeAndel);
    }

    @Override
    public String toString() {
        return "BesteberegningFødendeKvinneDto{" +
                "besteberegningAndelListe=" + besteberegningAndelListe +
                ", nyDagpengeAndel=" + nyDagpengeAndel +
                '}';
    }
}
