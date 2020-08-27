package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;

import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.beregning.beregningsgrunnlag.BeregningsgrunnlagPrStatusOgAndelDto;

@JsonAutoDetect(getterVisibility= JsonAutoDetect.Visibility.ANY, setterVisibility = JsonAutoDetect.Visibility.ANY, fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class FastsettEndretBeregningsgrunnlagPeriode {

    private List<FastsettEndretBeregningsgrunnlagAndel> andeler = new ArrayList<>();
    private LocalDate fom;
    private LocalDate tom;

    @JsonCreator
    public FastsettEndretBeregningsgrunnlagPeriode(List<FastsettEndretBeregningsgrunnlagAndel> andeler, LocalDate fom,
            LocalDate tom) {
        this.andeler = andeler;
        this.fom = fom;
        this.tom = tom;
    }

    public FastsettEndretBeregningsgrunnlagPeriode(BeregningsgrunnlagPrStatusOgAndelDto andel,
            FastsatteVerdier fastsatteVerdier,
            LocalDate fom, LocalDate tom) {
        leggTilAndel(andel, fastsatteVerdier);
        this.fom = fom;
        this.tom = tom;
    }

    void leggTilAndel(BeregningsgrunnlagPrStatusOgAndelDto andel, FastsatteVerdier fastsatteVerdier) {
        if (andeler.stream().anyMatch(a -> a.getAndelsnr() == andel.getAndelsnr())) {
            RedigerbarAndel andelInfo = new RedigerbarAndel(andel.getAndelsnr(),
                    andel.getArbeidsforhold() == null ? null : andel.getArbeidsforhold().getArbeidsgiverId(),
                    andel.getArbeidsforhold().getArbeidsforholdId(),
                    true,
                    andel.getAktivitetStatus(),
                    andel.getArbeidsforhold() == null ? null : andel.getArbeidsforhold().getArbeidsforholdType(),
                    true,
                    andel.getBeregningsperiodeFom(),
                    andel.getBeregningsperiodeTom());
            andeler.add(new FastsettEndretBeregningsgrunnlagAndel(andelInfo, fastsatteVerdier));
        } else {
            RedigerbarAndel andelInfo = new RedigerbarAndel(andel.getAndelsnr(),
                    andel.getArbeidsforhold().getArbeidsgiverId(),
                    andel.getArbeidsforhold().getArbeidsforholdId(),
                    false,
                    andel.getAktivitetStatus(),
                    andel.getArbeidsforhold() == null ? null : andel.getArbeidsforhold().getArbeidsforholdType(),
                    false,
                    andel.getBeregningsperiodeFom(),
                    andel.getBeregningsperiodeTom());
            andeler.add(new FastsettEndretBeregningsgrunnlagAndel(andelInfo, fastsatteVerdier));
        }
    }

    public List<FastsettEndretBeregningsgrunnlagAndel> getAndeler() {
        return andeler;
    }

    public void setAndeler(List<FastsettEndretBeregningsgrunnlagAndel> andeler) {
        this.andeler = andeler;
    }

    public LocalDate getFom() {
        return fom;
    }

    public void setFom(LocalDate fom) {
        this.fom = fom;
    }

    public LocalDate getTom() {
        return tom;
    }

    public void setTom(LocalDate tom) {
        this.tom = tom;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FastsettEndretBeregningsgrunnlagPeriode that = (FastsettEndretBeregningsgrunnlagPeriode) o;
        return Objects.equals(andeler, that.andeler) &&
                Objects.equals(fom, that.fom) &&
                Objects.equals(tom, that.tom);
    }

    @Override
    public int hashCode() {
        return Objects.hash(andeler, fom, tom);
    }

    @Override
    public String toString() {
        return "FastsettEndretBeregningsgrunnlagPeriode{" +
                "andeler=" + andeler +
                ", fom=" + fom +
                ", tom=" + tom +
                '}';
    }
}
