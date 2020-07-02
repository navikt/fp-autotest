package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse;

import java.time.LocalDate;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;

import no.nav.foreldrepenger.autotest.klienter.fpsak.kodeverk.dto.Kode;

@JsonAutoDetect(getterVisibility= JsonAutoDetect.Visibility.ANY, setterVisibility = JsonAutoDetect.Visibility.ANY, fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class FastsettEndretBeregningsgrunnlagAndel extends RedigerbarAndel {

    private FastsatteVerdier fastsatteVerdier;

    @JsonCreator
    public FastsettEndretBeregningsgrunnlagAndel(String andel, int andelsnr, String arbeidsgiverId,
                                                 String arbeidsforholdId, Boolean nyAndel,
                                                 Boolean lagtTilAvSaksbehandler, Kode aktivitetStatus,
                                                 LocalDate beregningsperiodeFom, LocalDate beregningsperiodeTom,
                                                 Kode arbeidsforholdType, FastsatteVerdier fastsatteVerdier) {
        super(andel, andelsnr, arbeidsgiverId, arbeidsforholdId, nyAndel, lagtTilAvSaksbehandler, aktivitetStatus,
                beregningsperiodeFom, beregningsperiodeTom, arbeidsforholdType);
        this.fastsatteVerdier = fastsatteVerdier;
    }

    public FastsettEndretBeregningsgrunnlagAndel(RedigerbarAndel redigerbarAndel, FastsatteVerdier fastsatteVerdier) {
        super(redigerbarAndel.getAndel(), redigerbarAndel.getAndelsnr(), redigerbarAndel.getArbeidsgiverId(),
                redigerbarAndel.getArbeidsforholdId(), redigerbarAndel.getNyAndel(), redigerbarAndel.getLagtTilAvSaksbehandler(),
                redigerbarAndel.getAktivitetStatus(), redigerbarAndel.getBeregningsperiodeFom(),
                redigerbarAndel.getBeregningsperiodeTom(), redigerbarAndel.getArbeidsforholdType());
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
