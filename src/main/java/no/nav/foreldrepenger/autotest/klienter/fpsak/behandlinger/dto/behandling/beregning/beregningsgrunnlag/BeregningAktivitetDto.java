package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.beregning.beregningsgrunnlag;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import no.nav.foreldrepenger.autotest.domain.foreldrepenger.OpptjeningAktivitetType;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BeregningAktivitetDto {

    protected String arbeidsgiverNavn;
    protected String arbeidsgiverId;
    protected LocalDate fom;
    protected LocalDate tom;
    protected String arbeidsforholdId;
    protected OpptjeningAktivitetType arbeidsforholdType;
    protected AktørId aktørId;
    protected Boolean skalBrukes;

    public String getArbeidsgiverNavn() {
        return arbeidsgiverNavn;
    }

    public String getArbeidsgiverId() {
        return arbeidsgiverId;
    }

    public LocalDate getFom() {
        return fom;
    }

    public LocalDate getTom() {
        return tom;
    }

    public String getArbeidsforholdId() {
        return arbeidsforholdId;
    }

    public OpptjeningAktivitetType getArbeidsforholdType() {
        return arbeidsforholdType;
    }

    public AktørId getAktørId() {
        return aktørId;
    }

    public Boolean getSkalBrukes() {
        return skalBrukes;
    }
}
