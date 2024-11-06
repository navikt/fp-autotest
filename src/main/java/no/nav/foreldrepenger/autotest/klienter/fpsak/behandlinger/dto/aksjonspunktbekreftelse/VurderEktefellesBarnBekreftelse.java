package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse;

public class VurderEktefellesBarnBekreftelse extends AksjonspunktBekreftelse {

    protected Boolean ektefellesBarn;

    public VurderEktefellesBarnBekreftelse() {
        super();
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
    public String aksjonspunktKode() {
        return "5005";
    }
}
