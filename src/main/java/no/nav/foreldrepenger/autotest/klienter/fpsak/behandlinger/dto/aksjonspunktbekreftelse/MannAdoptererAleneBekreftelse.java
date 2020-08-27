package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

@BekreftelseKode(kode = "5006")
@JsonAutoDetect(getterVisibility= JsonAutoDetect.Visibility.ANY, setterVisibility = JsonAutoDetect.Visibility.ANY, fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class MannAdoptererAleneBekreftelse extends AksjonspunktBekreftelse {

    private boolean mannAdoptererAlene;

    public MannAdoptererAleneBekreftelse() {
        super();
    }

    public boolean isMannAdoptererAlene() {
        return mannAdoptererAlene;
    }

    public void bekreftMannAdoptererAlene() {
        mannAdoptererAlene = true;
    }

    public void bekreftMannAdoptererIkkeAlene() {
        mannAdoptererAlene = false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MannAdoptererAleneBekreftelse that = (MannAdoptererAleneBekreftelse) o;
        return mannAdoptererAlene == that.mannAdoptererAlene;
    }

    @Override
    public int hashCode() {
        return Objects.hash(mannAdoptererAlene);
    }

    @Override
    public String toString() {
        return "MannAdoptererAleneBekreftelse{" +
                "mannAdoptererAlene=" + mannAdoptererAlene +
                '}';
    }
}
