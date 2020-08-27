package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse;

import java.util.Objects;

public abstract class VedtaksbrevOverstyringDto extends AksjonspunktBekreftelse {

    private String overskrift;
    private String fritekstBrev;
    private Boolean skalBrukeOverstyrendeFritekstBrev;

    public VedtaksbrevOverstyringDto() {
        super();
    }

    public VedtaksbrevOverstyringDto(String begrunnelse, String overskrift, String fritekstBrev,
                                     Boolean skalBrukeOverstyrendeFritekstBrev) {
        this.setBegrunnelse(begrunnelse);
        this.overskrift = overskrift;
        this.fritekstBrev = fritekstBrev;
        this.skalBrukeOverstyrendeFritekstBrev = skalBrukeOverstyrendeFritekstBrev;
    }

    public String getOverskrift() {
        return overskrift;
    }

    public String getFritekstBrev() {
        return fritekstBrev;
    }

    public Boolean getSkalBrukeOverstyrendeFritekstBrev() {
        return skalBrukeOverstyrendeFritekstBrev;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VedtaksbrevOverstyringDto that = (VedtaksbrevOverstyringDto) o;
        return Objects.equals(overskrift, that.overskrift) &&
                Objects.equals(fritekstBrev, that.fritekstBrev) &&
                Objects.equals(skalBrukeOverstyrendeFritekstBrev, that.skalBrukeOverstyrendeFritekstBrev);
    }

    @Override
    public int hashCode() {
        return Objects.hash(overskrift, fritekstBrev, skalBrukeOverstyrendeFritekstBrev);
    }

    @Override
    public String toString() {
        return "ForeslåVedtakBekreftelse{" +
                "overskrift='" + overskrift + '\'' +
                ", fritekstBrev='" + fritekstBrev + '\'' +
                ", skalBrukeOverstyrendeFritekstBrev=" + skalBrukeOverstyrendeFritekstBrev +
                "} " + super.toString();
    }
}
