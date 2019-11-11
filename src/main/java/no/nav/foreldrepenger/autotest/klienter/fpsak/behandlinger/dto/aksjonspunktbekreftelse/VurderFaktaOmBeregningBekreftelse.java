package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse;

import java.util.List;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.Behandling;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.beregning.ArbeidstakerandelUtenIMMottarYtelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.beregning.FaktaOmBeregningTilfelle;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.beregning.beregningsgrunnlag.BeregningsgrunnlagPeriodeDto;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.beregning.beregningsgrunnlag.BeregningsgrunnlagPrStatusOgAndelDto;
import no.nav.foreldrepenger.autotest.klienter.fpsak.fagsak.dto.Fagsak;
import no.nav.foreldrepenger.autotest.klienter.fpsak.kodeverk.dto.Kode;

@BekreftelseKode(kode="5058")
public class VurderFaktaOmBeregningBekreftelse extends AksjonspunktBekreftelse {

    protected FaktaOmBeregningLagreDto fakta = new FaktaOmBeregningLagreDto();

    public VurderFaktaOmBeregningBekreftelse(Fagsak fagsak, Behandling behandling) {
        super(fagsak, behandling);
    }

    public VurderFaktaOmBeregningBekreftelse leggTilFaktaOmBeregningTilfeller(String kode) {
        fakta.leggTilFaktaOmBeregningTilfeller(kode);
        return this;
    }

    public VurderFaktaOmBeregningBekreftelse leggTilVurderTidsbegrenset(List<VurderteArbeidsforholdDto> tidsbegrensetAndeler){
        fakta.leggTilVurderTidsbegrenset(tidsbegrensetAndeler);
        return this;
    }

    public VurderFaktaOmBeregningBekreftelse leggTilMottarYtelse(List<ArbeidstakerandelUtenIMMottarYtelse> arbeidstakerandelUtenIMMottarYtelses){
        fakta.leggTilMottarYtelse(false, arbeidstakerandelUtenIMMottarYtelses);
        return this;
    }

    public VurderFaktaOmBeregningBekreftelse leggTilMottarYtelseFrilans(boolean frilansMottarYtelse){
        fakta.leggTilMottarYtelse(frilansMottarYtelse, List.of());
        return this;
    }

    public VurderFaktaOmBeregningBekreftelse leggTilMaanedsinntektUtenInntektsmelding(List<FastsettMaanedsinntektUtenInntektsmeldingAndel> andelListe){
        fakta.leggTilMaanedsinntektUtenInntektsmelding(andelListe);
        return this;
    }

    public VurderFaktaOmBeregningBekreftelse leggTilMaanedsinntektFL(int maanedsinntekt) {
        fakta.leggTilMaanedsinntektFL(maanedsinntekt);
        return this;
    }

    public VurderFaktaOmBeregningBekreftelse leggTilAndelerYtelse(double beløp, Kode inntektskategori) {
        fakta.leggTilAndelerYtelse(beløp, inntektskategori);
        return this;
    }

    public VurderFaktaOmBeregningBekreftelse settSkalHaBesteberegningForKunYtelse(boolean skalHaBesteberegning) {
        fakta.settKunYtelseBesteberegning(skalHaBesteberegning);
        return this;
    }



    public VurderFaktaOmBeregningBekreftelse leggTilAndelerEndretBg(BeregningsgrunnlagPeriodeDto periode, BeregningsgrunnlagPrStatusOgAndelDto andel, FastsatteVerdier fastsatteVerdier) {
        fakta.leggTilAndelerEndretBg(periode, andel, fastsatteVerdier);
        return this;
    }
    public void behandleFrilansMottar(int maanedsinntekt) {
        fakta.leggTilMaanedsinntektFL(maanedsinntekt);
        fakta.leggTilFaktaOmBeregningTilfeller(FaktaOmBeregningTilfelle.VURDER_MOTTAR_YTELSE.kode);
        fakta.leggTilFaktaOmBeregningTilfeller(FaktaOmBeregningTilfelle.FASTSETT_MAANEDSINNTEKT_FL.kode);
        fakta.leggTilMottarYtelse(true, List.of());
    }
}
