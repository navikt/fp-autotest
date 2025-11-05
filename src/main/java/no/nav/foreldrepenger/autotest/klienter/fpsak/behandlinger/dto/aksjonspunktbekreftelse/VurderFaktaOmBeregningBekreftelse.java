package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse;

import java.util.List;

import no.nav.foreldrepenger.autotest.domain.foreldrepenger.Inntektskategori;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.Behandling;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.beregning.ArbeidstakerandelUtenIMMottarYtelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.beregning.FaktaOmBeregningTilfelle;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.beregning.beregningsgrunnlag.BeregningsgrunnlagPeriodeDto;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.beregning.beregningsgrunnlag.BeregningsgrunnlagPrStatusOgAndelDto;
import no.nav.foreldrepenger.autotest.klienter.fpsak.fagsak.dto.Fagsak;
import no.nav.foreldrepenger.kontrakter.felles.typer.Orgnummer;

//TODO: Rydd opp i denne. Ganske uoversiktlig.
public class VurderFaktaOmBeregningBekreftelse extends AksjonspunktBekreftelse {

    protected FaktaOmBeregningLagreDto fakta = new FaktaOmBeregningLagreDto();

    public VurderFaktaOmBeregningBekreftelse leggTilFaktaOmBeregningTilfeller(FaktaOmBeregningTilfelle kode) {
        fakta.leggTilFaktaOmBeregningTilfeller(kode);
        return this;
    }

    public VurderFaktaOmBeregningBekreftelse fjernFaktaOmBeregningTilfeller(FaktaOmBeregningTilfelle kode) {
        fakta.fjernFaktaOmBeregningTilfeller(kode);
        return this;
    }

    public VurderFaktaOmBeregningBekreftelse leggTilVurderTidsbegrenset(boolean vurderTidsbegrenset) {
        fakta.leggTilVurderTidsbegrenset(vurderTidsbegrenset);
        return this;
    }

    public VurderFaktaOmBeregningBekreftelse leggTilMottarYtelse(
            List<ArbeidstakerandelUtenIMMottarYtelse> arbeidstakerandelUtenIMMottarYtelses) {
        fakta.leggTilMottarYtelse(null, arbeidstakerandelUtenIMMottarYtelses);
        return this;
    }

    public VurderFaktaOmBeregningBekreftelse leggTilNyIArbeidslivet(
            boolean nyIArbeidslivet) {
        fakta.leggTilNyIArbeidslivet(nyIArbeidslivet);
        return this;
    }

    public VurderFaktaOmBeregningBekreftelse leggTilVurdertLønnsendring(boolean vurdertLonnsendring) {
        fakta.leggTilVurdertLonnsendring(vurdertLonnsendring);
        return this;
    }

    public VurderFaktaOmBeregningBekreftelse leggTilMaanedsinntektUtenInntektsmelding(
            List<FastsettMaanedsinntektUtenInntektsmeldingAndel> andelListe) {
        fakta.leggTilMaanedsinntektUtenInntektsmelding(andelListe);
        fakta.leggTilFaktaOmBeregningTilfeller(
                FaktaOmBeregningTilfelle.FASTSETT_MÅNEDSLØNN_ARBEIDSTAKER_UTEN_INNTEKTSMELDING);
        return this;
    }

    public VurderFaktaOmBeregningBekreftelse leggTilMaanedsinntektFL(int maanedsinntekt) {
        fakta.leggTilMaanedsinntektFL(maanedsinntekt);
        fakta.leggTilFaktaOmBeregningTilfeller(FaktaOmBeregningTilfelle.FASTSETT_MAANEDSINNTEKT_FL);
        return this;
    }

    public VurderFaktaOmBeregningBekreftelse leggTilAndelerYtelse(double beløp, Inntektskategori inntektskategori) {
        fakta.leggTilAndelerYtelse(beløp, inntektskategori);
        return this;
    }

    public VurderFaktaOmBeregningBekreftelse settSkalHaBesteberegningForKunYtelse(boolean skalHaBesteberegning) {
        fakta.settKunYtelseBesteberegning(skalHaBesteberegning);
        fakta.leggTilFaktaOmBeregningTilfeller(FaktaOmBeregningTilfelle.VURDER_BESTEBEREGNING);
        return this;
    }

    public VurderFaktaOmBeregningBekreftelse leggTilRefusjonGyldighetVurdering(Orgnummer arbeidsgiverIdentifikator,
                                                                               boolean skalUtvideGyldighet) {
        fakta.leggTilRefusjonGyldighet(arbeidsgiverIdentifikator, skalUtvideGyldighet);
        return this;
    }

    public VurderFaktaOmBeregningBekreftelse leggTilAndelerEndretBg(BeregningsgrunnlagPeriodeDto periode,
            BeregningsgrunnlagPrStatusOgAndelDto andel, FastsatteVerdier fastsatteVerdier) {
        fakta.leggTilAndelerEndretBg(periode, andel, fastsatteVerdier);
        return this;
    }

    public VurderFaktaOmBeregningBekreftelse fordelEtterBesteBeregningForDagpenger(boolean harDagpengerIOpptjening) {
        if (harDagpengerIOpptjening) {
            leggTilFaktaOmBeregningTilfeller(FaktaOmBeregningTilfelle.FASTSETT_BESTEBEREGNING_FØDENDE_KVINNE);
        } else {
            fakta.leggTilTomBesteBeregningAndeler();
        }
        return this;
    }

    public VurderFaktaOmBeregningBekreftelse leggTilBesteBeregningAndeler(double beløp, Inntektskategori inntektskategori) {
        fakta.leggTilBesteBeregningAndeler(beløp, inntektskategori);
        return this;
    }

    public VurderFaktaOmBeregningBekreftelse leggTilMottarYtelseFrilans(boolean frilansMottarYtelse) {
        fakta.leggTilMottarYtelse(frilansMottarYtelse, List.of());
        return this;
    }

    @Override
    public String aksjonspunktKode() {
        return "5058";
    }

    @Override
    public void oppdaterMedDataFraBehandling(Fagsak fagsak, Behandling behandling) {
        for (var faktaOmBeregningTilfeller : behandling.getBeregningsgrunnlag().getFaktaOmBeregning().getFaktaOmBeregningTilfeller()) {
            fakta.leggTilFaktaOmBeregningTilfeller(faktaOmBeregningTilfeller);
        }
    }

}
