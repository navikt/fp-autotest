package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse;

import java.time.LocalDate;

import no.nav.foreldrepenger.autotest.klienter.fpsak.kodeverk.dto.Kode;

@BekreftelseKode(kode="5026")
public class VarselOmRevurderingBekreftelse extends AksjonspunktBekreftelse {

    protected String begrunnelseForVarsel;
    protected String fritekst;
    protected String sendVarsel;
    protected LocalDate frist;
    protected String ventearsak;

    public VarselOmRevurderingBekreftelse() {
        super();
    }

    public VarselOmRevurderingBekreftelse setFrist(LocalDate frist) {
        this.frist = frist;
        return this;
    }

    public VarselOmRevurderingBekreftelse bekreftSendVarsel(Kode årsak, String fritekst) {
        sendVarsel = "" + true;
        this.fritekst = fritekst;
        ventearsak = årsak.kode;
        return this;
    }

    public VarselOmRevurderingBekreftelse bekreftIkkeSendVarsel() {
        sendVarsel = "" + false;
        return this;
    }

}
