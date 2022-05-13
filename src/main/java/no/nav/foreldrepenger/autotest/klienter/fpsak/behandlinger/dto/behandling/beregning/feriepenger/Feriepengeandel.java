package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.beregning.feriepenger;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import no.nav.foreldrepenger.autotest.domain.foreldrepenger.AktivitetStatus;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Feriepengeandel {

    protected AktivitetStatus aktivitetStatus;
    protected Integer opptjeningsår;
    protected BigDecimal årsbeløp;
    protected boolean erBrukerMottaker;
    protected String arbeidsgiverId;
    protected String arbeidsforholdId;

    public String getArbeidsgiverId() {
        return arbeidsgiverId;
    }

    public Integer getOpptjeningsår() {
        return opptjeningsår;
    }

    public BigDecimal getÅrsbeløp() {
        return årsbeløp;
    }


    public AktivitetStatus getAktivitetStatus() {
        return aktivitetStatus;
    }

    public String getArbeidsforholdId() {
        return arbeidsforholdId;
    }

    public boolean getErBrukerMottaker() {
        return erBrukerMottaker;
    }

}
