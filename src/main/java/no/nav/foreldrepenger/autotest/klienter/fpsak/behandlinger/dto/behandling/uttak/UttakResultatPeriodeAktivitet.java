package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.uttak;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import no.nav.foreldrepenger.autotest.domain.foreldrepenger.Stønadskonto;
import no.nav.foreldrepenger.autotest.klienter.fpsak.kodeverk.dto.Kode;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonAutoDetect(getterVisibility= JsonAutoDetect.Visibility.ANY, setterVisibility = JsonAutoDetect.Visibility.ANY, fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class UttakResultatPeriodeAktivitet implements Serializable {

    private Stønadskonto stønadskontoType;
    private BigDecimal trekkdagerDesimaler;
    private BigDecimal prosentArbeid;
    private BigDecimal utbetalingsgrad;
    private Kode uttakArbeidType;
    private Arbeidsgiver arbeidsgiver;
    private BigDecimal trekkdager;
    private String arbeidsforholdId;
    private boolean gradering;

    public UttakResultatPeriodeAktivitet(Stønadskonto stønadskontoType, BigDecimal trekkdagerDesimaler,
                                         BigDecimal prosentArbeid, BigDecimal utbetalingsgrad, Kode uttakArbeidType,
                                         Arbeidsgiver arbeidsgiver, BigDecimal trekkdager, String arbeidsforholdId,
                                         boolean gradering) {
        this.stønadskontoType = stønadskontoType;
        this.trekkdagerDesimaler = trekkdagerDesimaler;
        this.prosentArbeid = prosentArbeid;
        this.utbetalingsgrad = utbetalingsgrad;
        this.uttakArbeidType = uttakArbeidType;
        this.arbeidsgiver = arbeidsgiver;
        this.trekkdager = trekkdager;
        this.arbeidsforholdId = arbeidsforholdId;
        this.gradering = gradering;
    }



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

    public Stønadskonto getStønadskontoType() {
        return stønadskontoType;
    }

    public void setStønadskontoType(Stønadskonto stønadskontoType) {
        this.stønadskontoType = stønadskontoType;
    }

    public BigDecimal getTrekkdager() {
        return trekkdager;
    }

    public Arbeidsgiver getArbeidsgiver() {
        return arbeidsgiver;
    }

    public Kode getUttakArbeidType() {
        return uttakArbeidType;
    }

    public BigDecimal getProsentArbeid() {
        return prosentArbeid;
    }

    public String getArbeidsforholdId() {
        return arbeidsforholdId;
    }

    public boolean isGradering() {
        return gradering;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UttakResultatPeriodeAktivitet aktivitet = (UttakResultatPeriodeAktivitet) o;
        return gradering == aktivitet.gradering &&
                stønadskontoType == aktivitet.stønadskontoType &&
                Objects.equals(trekkdagerDesimaler, aktivitet.trekkdagerDesimaler) &&
                Objects.equals(prosentArbeid, aktivitet.prosentArbeid) &&
                Objects.equals(utbetalingsgrad, aktivitet.utbetalingsgrad) &&
                Objects.equals(uttakArbeidType, aktivitet.uttakArbeidType) &&
                Objects.equals(arbeidsgiver, aktivitet.arbeidsgiver) &&
                Objects.equals(trekkdager, aktivitet.trekkdager) &&
                Objects.equals(arbeidsforholdId, aktivitet.arbeidsforholdId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(stønadskontoType, trekkdagerDesimaler, prosentArbeid, utbetalingsgrad, uttakArbeidType, arbeidsgiver, trekkdager, arbeidsforholdId, gradering);
    }

    @Override
    public String toString() {
        return "UttakResultatPeriodeAktivitet{" +
                "stønadskontoType=" + stønadskontoType +
                ", trekkdagerDesimaler=" + trekkdagerDesimaler +
                ", prosentArbeid=" + prosentArbeid +
                ", utbetalingsgrad=" + utbetalingsgrad +
                ", uttakArbeidType=" + uttakArbeidType +
                ", arbeidsgiver=" + arbeidsgiver +
                ", trekkdager=" + trekkdager +
                ", arbeidsforholdId='" + arbeidsforholdId + '\'' +
                ", gradering=" + gradering +
                '}';
    }
}
