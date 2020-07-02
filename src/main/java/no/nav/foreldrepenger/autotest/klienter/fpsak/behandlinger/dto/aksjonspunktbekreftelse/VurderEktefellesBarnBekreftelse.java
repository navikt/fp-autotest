package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse;

@BekreftelseKode(kode = "5005")
public class VurderEktefellesBarnBekreftelse extends AksjonspunktBekreftelse {

    protected Boolean ektefellesBarn;

    public VurderEktefellesBarnBekreftelse() {
        super();
        // TODO Auto-generated constructor stub
    }

    public VurderEktefellesBarnBekreftelse bekreftBarnErEktefellesBarn() {
        ektefellesBarn = true;
        return this;
    }

    public VurderEktefellesBarnBekreftelse bekreftBarnErIkkeEktefellesBarn() {
        ektefellesBarn = false;
        return this;
    }
}
