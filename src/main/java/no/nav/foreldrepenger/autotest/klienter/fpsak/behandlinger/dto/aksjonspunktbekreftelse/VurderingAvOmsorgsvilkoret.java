package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;

import no.nav.foreldrepenger.autotest.klienter.fpsak.kodeverk.dto.Kode;

@BekreftelseKode(kode = "5011")
@JsonAutoDetect(getterVisibility= JsonAutoDetect.Visibility.ANY, setterVisibility = JsonAutoDetect.Visibility.ANY, fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class VurderingAvOmsorgsvilkoret extends AksjonspunktBekreftelse {

    private String avslagskode;
    private boolean erVilkarOk;

    public VurderingAvOmsorgsvilkoret() {
        super();
    }

    @JsonCreator
    public VurderingAvOmsorgsvilkoret(String avslagskode, boolean erVilkarOk) {
        this.avslagskode = avslagskode;
        this.erVilkarOk = erVilkarOk;
    }

    public String getAvslagskode() {
        return avslagskode;
    }

    public boolean isErVilkarOk() {
        return erVilkarOk;
    }

    public VurderingAvOmsorgsvilkoret bekreftGodkjent() {
        erVilkarOk = true;
        return this;
    }

    public VurderingAvOmsorgsvilkoret bekreftAvvist(Kode kode) {
        erVilkarOk = false;
        avslagskode = kode.kode;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VurderingAvOmsorgsvilkoret that = (VurderingAvOmsorgsvilkoret) o;
        return erVilkarOk == that.erVilkarOk &&
                Objects.equals(avslagskode, that.avslagskode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(avslagskode, erVilkarOk);
    }

    @Override
    public String toString() {
        return "VurderingAvOmsorgsvilkoret{" +
                "avslagskode='" + avslagskode + '\'' +
                ", erVilkarOk=" + erVilkarOk +
                "} " + super.toString();
    }
}
