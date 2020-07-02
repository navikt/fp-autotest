package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse;

import java.time.LocalDate;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

@BekreftelseKode(kode = "5043")
@JsonAutoDetect(getterVisibility= JsonAutoDetect.Visibility.ANY, setterVisibility = JsonAutoDetect.Visibility.ANY, fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class VurderSoknadsfristForeldrepengerBekreftelse extends AksjonspunktBekreftelse {

    private Boolean harGyldigGrunn;
    private LocalDate ansesMottattDato;

    public VurderSoknadsfristForeldrepengerBekreftelse() {
        super();
    }

    public Boolean getHarGyldigGrunn() {
        return harGyldigGrunn;
    }

    public LocalDate getAnsesMottattDato() {
        return ansesMottattDato;
    }

    public VurderSoknadsfristForeldrepengerBekreftelse bekreftHarGyldigGrunn(LocalDate ansesMottattDato) {
        this.harGyldigGrunn = true;
        this.ansesMottattDato = ansesMottattDato;
        this.begrunnelse = "Test";
        return this;
    }

    public VurderSoknadsfristForeldrepengerBekreftelse bekreftHarIkkeGyldigGrunn() {
        harGyldigGrunn = false;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VurderSoknadsfristForeldrepengerBekreftelse that = (VurderSoknadsfristForeldrepengerBekreftelse) o;
        return Objects.equals(harGyldigGrunn, that.harGyldigGrunn) &&
                Objects.equals(ansesMottattDato, that.ansesMottattDato);
    }

    @Override
    public int hashCode() {
        return Objects.hash(harGyldigGrunn, ansesMottattDato);
    }

    @Override
    public String toString() {
        return "VurderSoknadsfristForeldrepengerBekreftelse{" +
                "harGyldigGrunn=" + harGyldigGrunn +
                ", ansesMottattDato=" + ansesMottattDato +
                "} " + super.toString();
    }
}
