package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.beregning.feriepenger;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import no.nav.foreldrepenger.autotest.klienter.fpsak.kodeverk.dto.Kode;

import java.math.BigDecimal;
import java.time.LocalDate;

@JsonIgnoreProperties(ignoreUnknown = true)
public class FeriepengegrunnlagAndel {
    protected Kode aktivitetStatus;
    protected String arbeidsgiverId;
    protected String arbeidsforholdId;
    protected Integer opptjeningsår;
    protected BigDecimal årsbeløp;
    protected Boolean erBrukerMottaker;
    protected LocalDate ytelseperiodeFom;
    protected LocalDate ytelseperiodeTom;

    public FeriepengegrunnlagAndel() {
    }

    public Kode getAktivitetStatus() {
        return aktivitetStatus;
    }

    public String getArbeidsgiverId() {
        return arbeidsgiverId;
    }

    public String getArbeidsforholdId() {
        return arbeidsforholdId;
    }

    public Integer getOpptjeningsår() {
        return opptjeningsår;
    }

    public BigDecimal getÅrsbeløp() {
        return årsbeløp;
    }

    public Boolean getErBrukerMottaker() {
        return erBrukerMottaker;
    }

    public LocalDate getYtelseperiodeFom() {
        return ytelseperiodeFom;
    }

    public LocalDate getYtelseperiodeTom() {
        return ytelseperiodeTom;
    }
}
