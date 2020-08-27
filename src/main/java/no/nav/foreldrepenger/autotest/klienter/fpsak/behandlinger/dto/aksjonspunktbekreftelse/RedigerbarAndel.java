package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse;

import java.time.LocalDate;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

import no.nav.foreldrepenger.autotest.klienter.fpsak.kodeverk.dto.Kode;

@JsonAutoDetect(getterVisibility= JsonAutoDetect.Visibility.ANY, setterVisibility = JsonAutoDetect.Visibility.ANY, fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class RedigerbarAndel {

    private String andel;
    private Long andelsnr;
    private String arbeidsgiverId;
    private String arbeidsforholdId;
    private Boolean nyAndel;
    private Boolean lagtTilAvSaksbehandler;
    private Kode aktivitetStatus;
    private LocalDate beregningsperiodeFom;
    private LocalDate beregningsperiodeTom;
    private Kode arbeidsforholdType;


    public RedigerbarAndel(String andel, Long andelsnr, String arbeidsgiverId, String arbeidsforholdId, Boolean nyAndel,
                           Boolean lagtTilAvSaksbehandler, Kode aktivitetStatus, LocalDate beregningsperiodeFom,
                           LocalDate beregningsperiodeTom, Kode arbeidsforholdType) {
        this.andel = andel;
        this.andelsnr = andelsnr;
        this.arbeidsgiverId = arbeidsgiverId;
        this.arbeidsforholdId = arbeidsforholdId;
        this.nyAndel = nyAndel;
        this.lagtTilAvSaksbehandler = lagtTilAvSaksbehandler;
        this.aktivitetStatus = aktivitetStatus;
        this.beregningsperiodeFom = beregningsperiodeFom;
        this.beregningsperiodeTom = beregningsperiodeTom;
        this.arbeidsforholdType = arbeidsforholdType;
    }

    public String getAndel() {
        return andel;
    }

    public void setAndel(String andel) {
        this.andel = andel;
    }

    public Long getAndelsnr() {
        return andelsnr;
    }

    public void setAndelsnr(Long andelsnr) {
        this.andelsnr = andelsnr;
    }

    public String getArbeidsgiverId() {
        return arbeidsgiverId;
    }

    public void setArbeidsgiverId(String arbeidsgiverId) {
        this.arbeidsgiverId = arbeidsgiverId;
    }

    public String getArbeidsforholdId() {
        return arbeidsforholdId;
    }

    public void setArbeidsforholdId(String arbeidsforholdId) {
        this.arbeidsforholdId = arbeidsforholdId;
    }

    public Boolean getNyAndel() {
        return nyAndel;
    }

    public void setNyAndel(Boolean nyAndel) {
        this.nyAndel = nyAndel;
    }

    public Boolean getLagtTilAvSaksbehandler() {
        return lagtTilAvSaksbehandler;
    }

    public void setLagtTilAvSaksbehandler(Boolean lagtTilAvSaksbehandler) {
        this.lagtTilAvSaksbehandler = lagtTilAvSaksbehandler;
    }

    public Kode getAktivitetStatus() {
        return aktivitetStatus;
    }

    public void setAktivitetStatus(Kode aktivitetStatus) {
        this.aktivitetStatus = aktivitetStatus;
    }

    public LocalDate getBeregningsperiodeFom() {
        return beregningsperiodeFom;
    }

    public void setBeregningsperiodeFom(LocalDate beregningsperiodeFom) {
        this.beregningsperiodeFom = beregningsperiodeFom;
    }

    public LocalDate getBeregningsperiodeTom() {
        return beregningsperiodeTom;
    }

    public void setBeregningsperiodeTom(LocalDate beregningsperiodeTom) {
        this.beregningsperiodeTom = beregningsperiodeTom;
    }

    public Kode getArbeidsforholdType() {
        return arbeidsforholdType;
    }

    public void setArbeidsforholdType(Kode arbeidsforholdType) {
        this.arbeidsforholdType = arbeidsforholdType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RedigerbarAndel that = (RedigerbarAndel) o;
        return andelsnr == that.andelsnr &&
                Objects.equals(andel, that.andel) &&
                Objects.equals(arbeidsgiverId, that.arbeidsgiverId) &&
                Objects.equals(arbeidsforholdId, that.arbeidsforholdId) &&
                Objects.equals(nyAndel, that.nyAndel) &&
                Objects.equals(lagtTilAvSaksbehandler, that.lagtTilAvSaksbehandler) &&
                Objects.equals(aktivitetStatus, that.aktivitetStatus) &&
                Objects.equals(beregningsperiodeFom, that.beregningsperiodeFom) &&
                Objects.equals(beregningsperiodeTom, that.beregningsperiodeTom) &&
                Objects.equals(arbeidsforholdType, that.arbeidsforholdType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(andel, andelsnr, arbeidsgiverId, arbeidsforholdId, nyAndel, lagtTilAvSaksbehandler, aktivitetStatus, beregningsperiodeFom, beregningsperiodeTom, arbeidsforholdType);
    }

    @Override
    public String toString() {
        return "RedigerbarAndel{" +
                "andel='" + andel + '\'' +
                ", andelsnr=" + andelsnr +
                ", arbeidsgiverId='" + arbeidsgiverId + '\'' +
                ", arbeidsforholdId='" + arbeidsforholdId + '\'' +
                ", nyAndel=" + nyAndel +
                ", lagtTilAvSaksbehandler=" + lagtTilAvSaksbehandler +
                ", aktivitetStatus=" + aktivitetStatus +
                ", beregningsperiodeFom=" + beregningsperiodeFom +
                ", beregningsperiodeTom=" + beregningsperiodeTom +
                ", arbeidsforholdType=" + arbeidsforholdType +
                '}';
    }
}
