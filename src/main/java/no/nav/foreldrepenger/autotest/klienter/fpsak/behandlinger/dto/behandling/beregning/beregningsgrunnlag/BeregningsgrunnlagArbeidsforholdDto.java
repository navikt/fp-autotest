package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.beregning.beregningsgrunnlag;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import no.nav.foreldrepenger.autotest.domain.foreldrepenger.OpptjeningAktivitetType;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BeregningsgrunnlagArbeidsforholdDto {
    protected String arbeidsgiverNavn;
    protected String arbeidsgiverIdent;
    protected String startdato;
    protected String opphoersdato;
    protected String arbeidsforholdId;
    protected OpptjeningAktivitetType arbeidsforholdType;
    protected AktørId aktørId;
    protected double refusjonPrAar;

    public String getArbeidsgiverNavn() {
        return arbeidsgiverNavn;
    }

    public String getArbeidsgiverIdent() {
        return arbeidsgiverIdent;
    }

    public String getStartdato() {
        return startdato;
    }

    public String getOpphoersdato() {
        return opphoersdato;
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

    public double getRefusjonPrAar() {
        return refusjonPrAar;
    }
}
