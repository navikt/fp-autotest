package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.beregning.ArbeidstakerandelUtenIMMottarYtelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.beregning.MottarYtelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.beregning.beregningsgrunnlag.BeregningsgrunnlagPeriodeDto;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.beregning.beregningsgrunnlag.BeregningsgrunnlagPrStatusOgAndelDto;
import no.nav.foreldrepenger.autotest.klienter.fpsak.kodeverk.dto.Kode;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonAutoDetect(getterVisibility= JsonAutoDetect.Visibility.ANY, setterVisibility = JsonAutoDetect.Visibility.ANY, fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class FaktaOmBeregningLagreDto {

    private FastsettMaanedsinntektFL fastsettMaanedsinntektFL;
    private FastsettMaanedsinntektUtenInntektsmelding fastsattUtenInntektsmelding;
    private List<String> faktaOmBeregningTilfeller = new ArrayList<>();
    private MottarYtelse mottarYtelse;
    private FastsettEndretBeregningsgrunnlag fastsettEndringBeregningsgrunnlag;
    private BesteberegningFødendeKvinneDto besteberegningAndeler;
    private VurderTidsbegrensetArbeidsforholdDto vurderTidsbegrensetArbeidsforhold;
    private FastsettBgKunYtelseDto kunYtelseFordeling;
    private VurderLønnsendringDto vurdertLonnsendring;
    private List<RefusjonskravPrArbeidsgiverVurderingDto> refusjonskravGyldighet = new ArrayList<>();

    public FaktaOmBeregningLagreDto() {
    }

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
            kunYtelseFordeling = new FastsettBgKunYtelseDto();
        }
        kunYtelseFordeling.leggTilYtelseAndeler(new FastsettBrukersAndel(beløp, inntektskategori.kode));
        return this;
    }

    public FaktaOmBeregningLagreDto leggTilRefusjonGyldighet(String arbeidsgiverId, boolean skalUtvideGyldighet) {
        refusjonskravGyldighet.add(new RefusjonskravPrArbeidsgiverVurderingDto(arbeidsgiverId, skalUtvideGyldighet));
        return this;
    }

    public FaktaOmBeregningLagreDto settKunYtelseBesteberegning(boolean skalHaBesteberegning) {
        if (kunYtelseFordeling == null) {
            kunYtelseFordeling = new FastsettBgKunYtelseDto();
        }
        kunYtelseFordeling.setSkalBrukeBesteberegning(skalHaBesteberegning);
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FaktaOmBeregningLagreDto that = (FaktaOmBeregningLagreDto) o;
        return Objects.equals(fastsettMaanedsinntektFL, that.fastsettMaanedsinntektFL) &&
                Objects.equals(fastsattUtenInntektsmelding, that.fastsattUtenInntektsmelding) &&
                Objects.equals(faktaOmBeregningTilfeller, that.faktaOmBeregningTilfeller) &&
                Objects.equals(mottarYtelse, that.mottarYtelse) &&
                Objects.equals(fastsettEndringBeregningsgrunnlag, that.fastsettEndringBeregningsgrunnlag) &&
                Objects.equals(besteberegningAndeler, that.besteberegningAndeler) &&
                Objects.equals(vurderTidsbegrensetArbeidsforhold, that.vurderTidsbegrensetArbeidsforhold) &&
                Objects.equals(kunYtelseFordeling, that.kunYtelseFordeling) &&
                Objects.equals(vurdertLonnsendring, that.vurdertLonnsendring) &&
                Objects.equals(refusjonskravGyldighet, that.refusjonskravGyldighet);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fastsettMaanedsinntektFL, fastsattUtenInntektsmelding, faktaOmBeregningTilfeller,
                mottarYtelse, fastsettEndringBeregningsgrunnlag, besteberegningAndeler, vurderTidsbegrensetArbeidsforhold,
                kunYtelseFordeling, vurdertLonnsendring, refusjonskravGyldighet);
    }

    @Override
    public String toString() {
        return "FaktaOmBeregningLagreDto{" +
                "fastsettMaanedsinntektFL=" + fastsettMaanedsinntektFL +
                ", fastsattUtenInntektsmelding=" + fastsattUtenInntektsmelding +
                ", faktaOmBeregningTilfeller=" + faktaOmBeregningTilfeller +
                ", mottarYtelse=" + mottarYtelse +
                ", fastsettEndringBeregningsgrunnlag=" + fastsettEndringBeregningsgrunnlag +
                ", besteberegningAndeler=" + besteberegningAndeler +
                ", vurderTidsbegrensetArbeidsforhold=" + vurderTidsbegrensetArbeidsforhold +
                ", kunYtelseFordeling=" + kunYtelseFordeling +
                ", vurdertLonnsendring=" + vurdertLonnsendring +
                ", refusjonskravGyldighet=" + refusjonskravGyldighet +
                '}';
    }
}
