package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

@BekreftelseKode(kode = "5013")
@JsonAutoDetect(getterVisibility= JsonAutoDetect.Visibility.ANY, setterVisibility = JsonAutoDetect.Visibility.ANY, fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class VurderingAvForeldreansvarAndreLedd extends AksjonspunktBekreftelse {

    private Boolean erVilkarOk;
    private String avslagskode;

    public VurderingAvForeldreansvarAndreLedd() {
        super();
        // TODO Auto-generated constructor stub
    }

    public Boolean getErVilkarOk() {
        return erVilkarOk;
    }

    public String getAvslagskode() {
        return avslagskode;
    }

    public void bekreftGodkjent() {
        erVilkarOk = true;
    }

    public void bekreftAvvist(String kode) {
        erVilkarOk = false;
        avslagskode = kode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VurderingAvForeldreansvarAndreLedd that = (VurderingAvForeldreansvarAndreLedd) o;
        return Objects.equals(erVilkarOk, that.erVilkarOk) &&
                Objects.equals(avslagskode, that.avslagskode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(erVilkarOk, avslagskode);
    }

    @Override
    public String toString() {
        return "VurderingAvForeldreansvarAndreLedd{" +
                "erVilkarOk=" + erVilkarOk +
                ", avslagskode='" + avslagskode + '\'' +
                "} " + super.toString();
    }
}
