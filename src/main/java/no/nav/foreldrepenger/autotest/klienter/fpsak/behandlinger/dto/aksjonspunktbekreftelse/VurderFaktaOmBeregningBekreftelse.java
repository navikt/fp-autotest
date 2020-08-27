package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse;

import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;

import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.Behandling;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.beregning.ArbeidstakerandelUtenIMMottarYtelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.beregning.FaktaOmBeregningTilfelle;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.beregning.beregningsgrunnlag.BeregningsgrunnlagPeriodeDto;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.beregning.beregningsgrunnlag.BeregningsgrunnlagPrStatusOgAndelDto;
import no.nav.foreldrepenger.autotest.klienter.fpsak.fagsak.dto.Fagsak;
import no.nav.foreldrepenger.autotest.klienter.fpsak.kodeverk.dto.Kode;

//TODO: Rydd opp i denne. Ganske uoversiktlig.
@BekreftelseKode(kode = "5058")
@JsonAutoDetect(getterVisibility= JsonAutoDetect.Visibility.ANY, setterVisibility = JsonAutoDetect.Visibility.ANY, fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class VurderFaktaOmBeregningBekreftelse extends AksjonspunktBekreftelse {

    private FaktaOmBeregningLagreDto fakta = new FaktaOmBeregningLagreDto();

    public VurderFaktaOmBeregningBekreftelse() {
        super();
    }

    public VurderFaktaOmBeregningBekreftelse(@JsonProperty("fakta") FaktaOmBeregningLagreDto fakta) {
        this.fakta = fakta;
    }

    public FaktaOmBeregningLagreDto getFakta() {
        return fakta;
    }

    public VurderFaktaOmBeregningBekreftelse leggTilFaktaOmBeregningTilfeller(String kode) {
        fakta.leggTilFaktaOmBeregningTilfeller(kode);
        return this;
    }

    public VurderFaktaOmBeregningBekreftelse fjernFaktaOmBeregningTilfeller(String kode) {
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

    public VurderFaktaOmBeregningBekreftelse leggTilVurdertLønnsendring(boolean vurdertLonnsendring) {
        fakta.leggTilVurdertLonnsendring(vurdertLonnsendring);
        return this;
    }

    public VurderFaktaOmBeregningBekreftelse leggTilMaanedsinntektUtenInntektsmelding(
            List<FastsettMaanedsinntektUtenInntektsmeldingAndel> andelListe) {
        fakta.leggTilMaanedsinntektUtenInntektsmelding(andelListe);
        fakta.leggTilFaktaOmBeregningTilfeller(
                FaktaOmBeregningTilfelle.FASTSETT_MAANEDSLONN_ARBEIDSTAKER_UTEN_INNTEKTSMELDING.kode);
        return this;
    }

    public VurderFaktaOmBeregningBekreftelse leggTilMaanedsinntektFL(int maanedsinntekt) {
        fakta.leggTilMaanedsinntektFL(maanedsinntekt);
        fakta.leggTilFaktaOmBeregningTilfeller(FaktaOmBeregningTilfelle.FASTSETT_MAANEDSINNTEKT_FL.kode);
        return this;
    }

    public VurderFaktaOmBeregningBekreftelse leggTilAndelerYtelse(double beløp, Kode inntektskategori) {
        fakta.leggTilAndelerYtelse(beløp, inntektskategori);
        return this;
    }

    public VurderFaktaOmBeregningBekreftelse settSkalHaBesteberegningForKunYtelse(boolean skalHaBesteberegning) {
        fakta.settKunYtelseBesteberegning(skalHaBesteberegning);
        fakta.leggTilFaktaOmBeregningTilfeller(FaktaOmBeregningTilfelle.VURDER_BESTEBEREGNING.kode);
        return this;
    }

    public VurderFaktaOmBeregningBekreftelse leggTilRefusjonGyldighetVurdering(String orgnummer,
            boolean skalUtvideGyldighet) {
        fakta.leggTilRefusjonGyldighet(orgnummer, skalUtvideGyldighet);
        return this;
    }

    public VurderFaktaOmBeregningBekreftelse leggTilAndelerEndretBg(BeregningsgrunnlagPeriodeDto periode,
            BeregningsgrunnlagPrStatusOgAndelDto andel, FastsatteVerdier fastsatteVerdier) {
        fakta.leggTilAndelerEndretBg(periode, andel, fastsatteVerdier);
        return this;
    }

    public VurderFaktaOmBeregningBekreftelse fordelEtterBesteBeregningForDagpenger(boolean harDagpengerIOpptjening) {
        if (harDagpengerIOpptjening) {
            leggTilFaktaOmBeregningTilfeller(FaktaOmBeregningTilfelle.FASTSETT_BESTEBEREGNING_FØDENDE_KVINNE.kode);
        } else {
            fakta.leggTilTomBesteBeregningAndeler();
        }
        return this;
    }

    public VurderFaktaOmBeregningBekreftelse leggTilBesteBeregningAndeler(double beløp, Kode inntektskategori) {
        fakta.leggTilBesteBeregningAndeler(beløp, inntektskategori);
        return this;
    }

    public VurderFaktaOmBeregningBekreftelse leggTilMottarYtelseFrilans(boolean frilansMottarYtelse) {
        fakta.leggTilMottarYtelse(frilansMottarYtelse, List.of());
        return this;
    }

    public VurderFaktaOmBeregningBekreftelse leggTilmånedsinntektFLMottarStøtte(int maanedsinntekt) {
        fakta.leggTilMaanedsinntektFL(maanedsinntekt);
        fakta.leggTilFaktaOmBeregningTilfeller(FaktaOmBeregningTilfelle.FASTSETT_MAANEDSINNTEKT_FL.kode);
        return this;
    }

    @Override
    public void oppdaterMedDataFraBehandling(Fagsak fagsak, Behandling behandling) {
        for (FaktaOmBeregningTilfelle faktaOmBeregningTilfeller : behandling.getBeregningsgrunnlag()
                .getFaktaOmBeregning().getFaktaOmBeregningTilfeller()) {
            fakta.leggTilFaktaOmBeregningTilfeller(faktaOmBeregningTilfeller.kode);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VurderFaktaOmBeregningBekreftelse that = (VurderFaktaOmBeregningBekreftelse) o;
        return Objects.equals(fakta, that.fakta);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fakta);
    }

    @Override
    public String toString() {
        return "VurderFaktaOmBeregningBekreftelse{" +
                "fakta=" + fakta +
                '}';
    }
}
