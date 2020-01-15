package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse;

@BekreftelseKode(kode="5044")
public class VurderVilkaarForSykdomBekreftelse extends AksjonspunktBekreftelse {

    protected boolean erMorForSykVedFodsel;

    public VurderVilkaarForSykdomBekreftelse() {
        super();
    }

    //TODO Stub
    public void setErMorForSykVedFodsel(boolean morForSykVEdFodsel) {
        this.erMorForSykVedFodsel = morForSykVEdFodsel;
    }

}
