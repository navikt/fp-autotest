package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse;

import java.time.LocalDate;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

import no.nav.foreldrepenger.autotest.klienter.fpsak.kodeverk.dto.Kode;

@JsonAutoDetect(getterVisibility= JsonAutoDetect.Visibility.ANY, setterVisibility = JsonAutoDetect.Visibility.ANY, fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class RedigerbarAndel {

    private Long andelsnr;
    private String arbeidsgiverId;
    private String arbeidsforholdId;
    private Boolean nyAndel;
    private Kode aktivitetStatus;
    private Kode arbeidsforholdType;
    private Boolean lagtTilAvSaksbehandler;
    private LocalDate beregningsperiodeFom;
    private LocalDate beregningsperiodeTom;

    public RedigerbarAndel(Long andelsnr, String arbeidsgiverId, String arbeidsforholdId, Boolean nyAndel,
                           Kode aktivitetStatus, Kode arbeidsforholdType, Boolean lagtTilAvSaksbehandler,
                           LocalDate beregningsperiodeFom, LocalDate beregningsperiodeTom) {
        this.andelsnr = andelsnr;
        this.arbeidsgiverId = arbeidsgiverId;
        this.arbeidsforholdId = arbeidsforholdId;
        this.nyAndel = nyAndel;
        this.aktivitetStatus = aktivitetStatus;
        this.arbeidsforholdType = arbeidsforholdType;
        this.lagtTilAvSaksbehandler = lagtTilAvSaksbehandler;
        this.beregningsperiodeFom = beregningsperiodeFom;
        this.beregningsperiodeTom = beregningsperiodeTom;
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

    public Kode getAktivitetStatus() {
        return aktivitetStatus;
    }

    public void setAktivitetStatus(Kode aktivitetStatus) {
        this.aktivitetStatus = aktivitetStatus;
    }

    public Kode getArbeidsforholdType() {
        return arbeidsforholdType;
    }

    public void setArbeidsforholdType(Kode arbeidsforholdType) {
        this.arbeidsforholdType = arbeidsforholdType;
    }

    public Boolean getLagtTilAvSaksbehandler() {
        return lagtTilAvSaksbehandler;
    }

    public void setLagtTilAvSaksbehandler(Boolean lagtTilAvSaksbehandler) {
        this.lagtTilAvSaksbehandler = lagtTilAvSaksbehandler;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RedigerbarAndel that = (RedigerbarAndel) o;
        return Objects.equals(andelsnr, that.andelsnr) &&
                Objects.equals(arbeidsgiverId, that.arbeidsgiverId) &&
                Objects.equals(arbeidsforholdId, that.arbeidsforholdId) &&
                Objects.equals(nyAndel, that.nyAndel) &&
                Objects.equals(aktivitetStatus, that.aktivitetStatus) &&
                Objects.equals(arbeidsforholdType, that.arbeidsforholdType) &&
                Objects.equals(lagtTilAvSaksbehandler, that.lagtTilAvSaksbehandler) &&
                Objects.equals(beregningsperiodeFom, that.beregningsperiodeFom) &&
                Objects.equals(beregningsperiodeTom, that.beregningsperiodeTom);
    }

    @Override
    public int hashCode() {
        return Objects.hash(andelsnr, arbeidsgiverId, arbeidsforholdId, nyAndel, aktivitetStatus, arbeidsforholdType, lagtTilAvSaksbehandler, beregningsperiodeFom, beregningsperiodeTom);
    }

    @Override
    public String toString() {
        return "RedigerbarAndel{" +
                "andelsnr=" + andelsnr +
                ", arbeidsgiverId='" + arbeidsgiverId + '\'' +
                ", arbeidsforholdId='" + arbeidsforholdId + '\'' +
                ", nyAndel=" + nyAndel +
                ", aktivitetStatus=" + aktivitetStatus +
                ", arbeidsforholdType=" + arbeidsforholdType +
                ", lagtTilAvSaksbehandler=" + lagtTilAvSaksbehandler +
                ", beregningsperiodeFom=" + beregningsperiodeFom +
                ", beregningsperiodeTom=" + beregningsperiodeTom +
                '}';
    }
}
