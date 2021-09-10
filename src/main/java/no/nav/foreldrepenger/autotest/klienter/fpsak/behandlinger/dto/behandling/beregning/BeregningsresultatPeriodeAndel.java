package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.beregning;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import no.nav.foreldrepenger.autotest.dokumentgenerator.foreldrepengesoknad.json.modell.felles.Orgnummer;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.AktivitetStatus;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.OpptjeningAktivitetType;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BeregningsresultatPeriodeAndel {

    protected Orgnummer arbeidsgiverReferanse;
    protected Integer refusjon;
    protected Integer tilSoker;
    protected Uttak uttak;
    protected BigDecimal utbetalingsgrad;
    protected LocalDate sisteUtbetalingsdato;
    protected AktivitetStatus aktivitetStatus;
    protected String arbeidsforholdId;
    protected OpptjeningAktivitetType arbeidsforholdType;


    public Orgnummer getArbeidsgiverReferanse() {
        return arbeidsgiverReferanse;
    }

    public Integer getRefusjon() {
        return refusjon;
    }

    public Integer getTilSoker() {
        return tilSoker;
    }

    public Uttak getUttak() {
        return uttak;
    }

    public BigDecimal getUtbetalingsgrad() {
        return utbetalingsgrad;
    }

    public LocalDate getSisteUtbetalingsdato() {
        return sisteUtbetalingsdato;
    }

    public AktivitetStatus getAktivitetStatus() {
        return aktivitetStatus;
    }

    public String getArbeidsforholdId() {
        return arbeidsforholdId;
    }

    public OpptjeningAktivitetType getArbeidsforholdType() {
        return arbeidsforholdType;
    }

}
