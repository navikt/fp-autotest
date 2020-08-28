package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

@BekreftelseKode(kode = "5095")
@JsonAutoDetect(getterVisibility= JsonAutoDetect.Visibility.ANY, setterVisibility = JsonAutoDetect.Visibility.ANY, fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class VurderFaresignalerDto extends AksjonspunktBekreftelse{

    private Boolean harInnvirketBehandlingen;

    public VurderFaresignalerDto() {
        super();
    }

    public Boolean getHarInnvirketBehandlingen() {
        return harInnvirketBehandlingen;
    }

    public void setHarInnvirketBehandlingen(Boolean harInnvirketBehandlingen) {
        this.harInnvirketBehandlingen = harInnvirketBehandlingen;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VurderFaresignalerDto that = (VurderFaresignalerDto) o;
        return Objects.equals(harInnvirketBehandlingen, that.harInnvirketBehandlingen);
    }

    @Override
    public int hashCode() {
        return Objects.hash(harInnvirketBehandlingen);
    }

    @Override
    public String toString() {
        return "VurderFaresignalerDto{" +
                "harInnvirketBehandlingen=" + harInnvirketBehandlingen +
                "} " + super.toString();
    }
}
