package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

@BekreftelseKode(kode = "5044")
@JsonAutoDetect(getterVisibility= JsonAutoDetect.Visibility.ANY, setterVisibility = JsonAutoDetect.Visibility.ANY, fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class VurderVilkaarForSykdomBekreftelse extends AksjonspunktBekreftelse {

    private boolean erMorForSykVedFodsel;

    public VurderVilkaarForSykdomBekreftelse() {
        super();
    }

    public boolean isErMorForSykVedFodsel() {
        return erMorForSykVedFodsel;
    }

    public void setErMorForSykVedFodsel(boolean morForSykVEdFodsel) {
        this.erMorForSykVedFodsel = morForSykVEdFodsel;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VurderVilkaarForSykdomBekreftelse that = (VurderVilkaarForSykdomBekreftelse) o;
        return erMorForSykVedFodsel == that.erMorForSykVedFodsel;
    }

    @Override
    public int hashCode() {
        return Objects.hash(erMorForSykVedFodsel);
    }

    @Override
    public String toString() {
        return "VurderVilkaarForSykdomBekreftelse{" +
                "erMorForSykVedFodsel=" + erMorForSykVedFodsel +
                "} " + super.toString();
    }
}
