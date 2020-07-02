package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

import no.nav.foreldrepenger.autotest.klienter.fpsak.kodeverk.dto.Kode;

@BekreftelseKode(kode = "5015")
@JsonAutoDetect(getterVisibility= JsonAutoDetect.Visibility.ANY, setterVisibility = JsonAutoDetect.Visibility.ANY, fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class ForeslåVedtakBekreftelse extends AksjonspunktBekreftelse {

    // TODO se om dette stemmer enda
    // protected int antallBarn;
    // protected int beregningResultat;
    private Kode avslagCode;
    private String fritekstBrev;
    private Boolean skalBrukeOverstyrendeFritekstBrev;
    private Boolean isVedtakSubmission;

    public ForeslåVedtakBekreftelse() {
        super();
    }

    public ForeslåVedtakBekreftelse(Kode avslagCode, String fritekstBrev, Boolean skalBrukeOverstyrendeFritekstBrev,
                                    Boolean isVedtakSubmission) {
        this.avslagCode = avslagCode;
        this.fritekstBrev = fritekstBrev;
        this.skalBrukeOverstyrendeFritekstBrev = skalBrukeOverstyrendeFritekstBrev;
        this.isVedtakSubmission = isVedtakSubmission;
    }

    public Kode getAvslagCode() {
        return avslagCode;
    }

    public String getFritekstBrev() {
        return fritekstBrev;
    }

    public Boolean getSkalBrukeOverstyrendeFritekstBrev() {
        return skalBrukeOverstyrendeFritekstBrev;
    }

    public Boolean getIsVedtakSubmission() {
        return isVedtakSubmission;
    }

    public ForeslåVedtakBekreftelse setAvslagCode(Kode avslagCode) {
        this.avslagCode = avslagCode;
        return this;
    }

    public ForeslåVedtakBekreftelse setIsVedtakSubmission(Boolean verdi) {
        this.isVedtakSubmission = verdi;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ForeslåVedtakBekreftelse that = (ForeslåVedtakBekreftelse) o;
        return Objects.equals(avslagCode, that.avslagCode) &&
                Objects.equals(fritekstBrev, that.fritekstBrev) &&
                Objects.equals(skalBrukeOverstyrendeFritekstBrev, that.skalBrukeOverstyrendeFritekstBrev) &&
                Objects.equals(isVedtakSubmission, that.isVedtakSubmission);
    }

    @Override
    public int hashCode() {
        return Objects.hash(avslagCode, fritekstBrev, skalBrukeOverstyrendeFritekstBrev, isVedtakSubmission);
    }

    @Override
    public String toString() {
        return "ForeslåVedtakBekreftelse{" +
                "avslagCode=" + avslagCode +
                ", fritekstBrev='" + fritekstBrev + '\'' +
                ", skalBrukeOverstyrendeFritekstBrev=" + skalBrukeOverstyrendeFritekstBrev +
                ", isVedtakSubmission=" + isVedtakSubmission +
                '}';
    }
}
