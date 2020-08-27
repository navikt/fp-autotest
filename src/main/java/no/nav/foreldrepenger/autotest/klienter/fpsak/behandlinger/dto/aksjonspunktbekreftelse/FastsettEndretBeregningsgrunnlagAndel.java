package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse;

import java.time.LocalDate;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;

import no.nav.foreldrepenger.autotest.klienter.fpsak.kodeverk.dto.Kode;

@JsonAutoDetect(getterVisibility= JsonAutoDetect.Visibility.ANY, setterVisibility = JsonAutoDetect.Visibility.ANY, fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class FastsettEndretBeregningsgrunnlagAndel extends RedigerbarAndel {

    private FastsatteVerdier fastsatteVerdier;

    public FastsettEndretBeregningsgrunnlagAndel(RedigerbarAndel redigerbarAndel, FastsatteVerdier fastsatteVerdier) {
        this(redigerbarAndel.getAndelsnr(), redigerbarAndel.getArbeidsgiverId(),
                redigerbarAndel.getArbeidsforholdId(), redigerbarAndel.getNyAndel(), redigerbarAndel.getAktivitetStatus(),
                redigerbarAndel.getArbeidsforholdType(), redigerbarAndel.getLagtTilAvSaksbehandler(),
                redigerbarAndel.getBeregningsperiodeFom(),redigerbarAndel.getBeregningsperiodeTom(), fastsatteVerdier);
    }

    @JsonCreator
    public FastsettEndretBeregningsgrunnlagAndel(Long andelsnr, String arbeidsgiverId, String arbeidsforholdId,
                                                 Boolean nyAndel, Kode aktivitetStatus, Kode arbeidsforholdType,
                                                 Boolean lagtTilAvSaksbehandler, LocalDate beregningsperiodeFom,
                                                 LocalDate beregningsperiodeTom, FastsatteVerdier fastsatteVerdier) {
        super(andelsnr, arbeidsgiverId, arbeidsforholdId, nyAndel, aktivitetStatus, arbeidsforholdType,
                lagtTilAvSaksbehandler, beregningsperiodeFom, beregningsperiodeTom);
        this.fastsatteVerdier = fastsatteVerdier;
    }

    public FastsatteVerdier getFastsatteVerdier() {
        return fastsatteVerdier;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        FastsettEndretBeregningsgrunnlagAndel that = (FastsettEndretBeregningsgrunnlagAndel) o;
        return Objects.equals(fastsatteVerdier, that.fastsatteVerdier);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), fastsatteVerdier);
    }

    @Override
    public String toString() {
        return "FastsettEndretBeregningsgrunnlagAndel{" +
                "fastsatteVerdier=" + fastsatteVerdier +
                "} " + super.toString();
    }
}
