package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse;

import java.time.LocalDate;

public class VurderSoknadsfristForeldrepengerBekreftelse extends AksjonspunktBekreftelse {

    protected Boolean harGyldigGrunn;
    protected LocalDate ansesMottattDato;

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
    public String aksjonspunktKode() {
        return "5043";
    }
}
