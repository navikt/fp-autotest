package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse;

import java.util.ArrayList;
import java.util.List;

public class BesteberegningFødendeKvinneDto {

    protected List<BesteberegningFødendeKvinneAndelDto> besteberegningAndelListe = new ArrayList<>();

    protected DagpengeAndelLagtTilBesteberegningDto nyDagpengeAndel;

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
}
