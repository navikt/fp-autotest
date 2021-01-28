package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.beregning.ArbeidstakerandelUtenIMMottarYtelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.beregning.MottarYtelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.beregning.beregningsgrunnlag.BeregningsgrunnlagPeriodeDto;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.beregning.beregningsgrunnlag.BeregningsgrunnlagPrStatusOgAndelDto;
import no.nav.foreldrepenger.autotest.klienter.fpsak.kodeverk.dto.Kode;

@JsonIgnoreProperties(ignoreUnknown = true)
public class FaktaOmBeregningLagreDto {

    protected FastsettMaanedsinntektFL fastsettMaanedsinntektFL;
    protected FastsettMaanedsinntektUtenInntektsmelding fastsattUtenInntektsmelding;
    protected List<String> faktaOmBeregningTilfeller = new ArrayList<>();
    protected MottarYtelse mottarYtelse;
    protected FastsettEndretBeregningsgrunnlag fastsettEndringBeregningsgrunnlag;
    protected BesteberegningFødendeKvinneDto besteberegningAndeler;
    protected VurderTidsbegrensetArbeidsforholdDto vurderTidsbegrensetArbeidsforhold;
    protected YtelseForedeling kunYtelseFordeling;
    protected VurderLønnsendringDto vurdertLonnsendring;
    protected List<RefusjonskravPrArbeidsgiverVurderingDto> refusjonskravGyldighet = new ArrayList<>();

    public FaktaOmBeregningLagreDto leggTilFaktaOmBeregningTilfeller(String kode) {
        this.faktaOmBeregningTilfeller.add(kode);
        return this;
    }

    public FaktaOmBeregningLagreDto fjernFaktaOmBeregningTilfeller(String kode) {
        this.faktaOmBeregningTilfeller.remove(kode);
        return this;
    }

    public FaktaOmBeregningLagreDto leggTilVurderTidsbegrenset(Boolean tidsbegrenset) {
        List<VurderteArbeidsforholdDto> tidsbegrensetAndeler = Collections
                .singletonList(new VurderteArbeidsforholdDto(2L, tidsbegrenset, false));
        this.vurderTidsbegrensetArbeidsforhold = new VurderTidsbegrensetArbeidsforholdDto(tidsbegrensetAndeler);
        return this;
    }

    public FaktaOmBeregningLagreDto leggTilMottarYtelse(Boolean frilansMottarYtelse,
            List<ArbeidstakerandelUtenIMMottarYtelse> arbeidstakerandelUtenIMMottarYtelses) {
        if (this.mottarYtelse != null) {
            arbeidstakerandelUtenIMMottarYtelses.forEach(this.mottarYtelse::leggTilArbeidstakerandelUtenIMMottarYtelse);
        } else {
            this.mottarYtelse = new MottarYtelse(frilansMottarYtelse, arbeidstakerandelUtenIMMottarYtelses);
        }
        return this;
    }

    public FaktaOmBeregningLagreDto leggTilTomBesteBeregningAndeler() {
        if (besteberegningAndeler == null) {
            besteberegningAndeler = new BesteberegningFødendeKvinneDto();
        }
        return this;
    }

    public FaktaOmBeregningLagreDto leggTilBesteBeregningAndeler(double beløp, Kode inntektskategori) {
        if (besteberegningAndeler == null) {
            besteberegningAndeler = new BesteberegningFødendeKvinneDto();
        }
        besteberegningAndeler
                .leggTilBesteberegningAndel(new BesteberegningFødendeKvinneAndelDto(beløp, inntektskategori.kode));
        return this;
    }

    public FaktaOmBeregningLagreDto leggTilMaanedsinntektUtenInntektsmelding(
            List<FastsettMaanedsinntektUtenInntektsmeldingAndel> andelListe) {
        this.fastsattUtenInntektsmelding = new FastsettMaanedsinntektUtenInntektsmelding(andelListe);
        return this;
    }

    public FaktaOmBeregningLagreDto leggTilMaanedsinntektFL(int maanedsinntekt) {
        fastsettMaanedsinntektFL = new FastsettMaanedsinntektFL(maanedsinntekt);
        return this;
    }

    public FaktaOmBeregningLagreDto leggTilAndelerYtelse(double beløp, Kode inntektskategori) {
        if (kunYtelseFordeling == null) {
            kunYtelseFordeling = new YtelseForedeling();
        }
        kunYtelseFordeling.leggTilYtelseAndeler(new YtelseAndeler(beløp, inntektskategori.kode));
        return this;
    }

    public FaktaOmBeregningLagreDto leggTilRefusjonGyldighet(String arbeidsgiverId, boolean skalUtvideGyldighet) {
        refusjonskravGyldighet.add(new RefusjonskravPrArbeidsgiverVurderingDto(arbeidsgiverId, skalUtvideGyldighet));
        return this;
    }

    public FaktaOmBeregningLagreDto settKunYtelseBesteberegning(boolean skalHaBesteberegning) {
        if (kunYtelseFordeling == null) {
            kunYtelseFordeling = new YtelseForedeling();
        }
        kunYtelseFordeling.skalBrukeBesteberegning = skalHaBesteberegning;
        return this;
    }

    public FaktaOmBeregningLagreDto leggTilAndelerEndretBg(BeregningsgrunnlagPeriodeDto periode,
            BeregningsgrunnlagPrStatusOgAndelDto andel, FastsatteVerdier fastsatteVerdier) {
        if (fastsettEndringBeregningsgrunnlag == null) {
            fastsettEndringBeregningsgrunnlag = new FastsettEndretBeregningsgrunnlag();
        }
        fastsettEndringBeregningsgrunnlag.leggTilAndelTilPeriode(periode, andel, fastsatteVerdier);
        return this;
    }

    public FaktaOmBeregningLagreDto leggTilVurdertLonnsendring(Boolean vurdertLønnsendring) {
        if (vurdertLonnsendring == null) {
            vurdertLonnsendring = new VurderLønnsendringDto();
        }
        vurdertLonnsendring.setErLønnsendringIBeregningsperioden(vurdertLønnsendring);
        return this;
    }

    public class YtelseForedeling {

        public List<YtelseAndeler> andeler = new ArrayList<>();
        public Boolean skalBrukeBesteberegning;

        public YtelseForedeling() {
            super();
        }

        public void leggTilYtelseAndeler(YtelseAndeler andel) {
            andel.setAndelsnr(andeler.size() + 1);
            andeler.add(andel);
        }

    }

    public static class YtelseAndeler {
        public int andelsnr;
        public double fastsattBeløp;
        public String inntektskategori;
        public boolean lagtTilAvSaksbehandler;
        public boolean nyAndel;

        public YtelseAndeler() {
            super();
        }

        public YtelseAndeler(double fastsattBeløp, String inntektskategori) {
            super();
            this.fastsattBeløp = fastsattBeløp;
            this.inntektskategori = inntektskategori;
        }

        public void setAndelsnr(int andelsnr) {
            this.andelsnr = andelsnr;
        }

    }

}
