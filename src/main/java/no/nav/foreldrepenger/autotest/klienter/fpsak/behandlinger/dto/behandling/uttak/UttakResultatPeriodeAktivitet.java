package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.uttak;

import java.io.Serializable;
import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import no.nav.foreldrepenger.kontrakter.fpsoknad.foreldrepenger.uttaksplan.KontoType;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UttakResultatPeriodeAktivitet implements Serializable {

    protected UttakKontoType stønadskontoType = null;
    protected BigDecimal trekkdagerDesimaler = null;
    protected BigDecimal prosentArbeid = null;
    protected BigDecimal utbetalingsgrad = null;
    protected String uttakArbeidType = null;
    protected String arbeidsgiverReferanse;

    protected BigDecimal trekkdager;
    protected String arbeidsforholdId;
    protected boolean gradering;

    public BigDecimal getUtbetalingsgrad() {
        return utbetalingsgrad;
    }

    public void setUtbetalingsgrad(BigDecimal utbetalingsgrad) {
        this.utbetalingsgrad = utbetalingsgrad;
    }

    public BigDecimal getTrekkdagerDesimaler() {
        return trekkdagerDesimaler;
    }

    public void setTrekkdagerDesimaler(BigDecimal trekkdagerDesimaler) {
        this.trekkdagerDesimaler = trekkdagerDesimaler;
    }

    public KontoType getKontoType() {
        if (stønadskontoType == null) {
            return null;
        }
        return stønadskontoType.tilKontoType();
    }

    public void setStønadskontoType(UttakKontoType stønadskontoType) {
        this.stønadskontoType = stønadskontoType;
    }

    @JsonIgnore
    public void setKontotype(KontoType kontotype) {
        this.stønadskontoType = UttakKontoType.tilUttakKontoType(kontotype);
    }

    public String getArbeidsgiverReferanse() {
        return arbeidsgiverReferanse;
    }

    public String getUttakArbeidType() {
        return uttakArbeidType;
    }

    public BigDecimal getProsentArbeid() {
        return prosentArbeid;
    }
}
