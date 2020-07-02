package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

@BekreftelseKode(kode = "5005")
@JsonAutoDetect(getterVisibility= JsonAutoDetect.Visibility.ANY, setterVisibility = JsonAutoDetect.Visibility.ANY, fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class VurderEktefellesBarnBekreftelse extends AksjonspunktBekreftelse {

    private Boolean ektefellesBarn;

    public VurderEktefellesBarnBekreftelse() {
        super();
        // TODO Auto-generated constructor stub
    }

    public Boolean getEktefellesBarn() {
        return ektefellesBarn;
    }

    public VurderEktefellesBarnBekreftelse bekreftBarnErEktefellesBarn() {
        ektefellesBarn = true;
        return this;
    }

    public VurderEktefellesBarnBekreftelse bekreftBarnErIkkeEktefellesBarn() {
        ektefellesBarn = false;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VurderEktefellesBarnBekreftelse that = (VurderEktefellesBarnBekreftelse) o;
        return Objects.equals(ektefellesBarn, that.ektefellesBarn);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ektefellesBarn);
    }

    @Override
    public String toString() {
        return "VurderEktefellesBarnBekreftelse{" +
                "ektefellesBarn=" + ektefellesBarn +
                "} " + super.toString();
    }
}
