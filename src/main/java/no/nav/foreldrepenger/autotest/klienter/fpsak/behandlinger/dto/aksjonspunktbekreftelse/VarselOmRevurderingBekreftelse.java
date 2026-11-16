package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse;

import java.time.LocalDate;

import no.nav.foreldrepenger.autotest.domain.foreldrepenger.Venteårsak;

public class VarselOmRevurderingBekreftelse extends AksjonspunktBekreftelse {

    protected boolean sendVarsel;
    protected LocalDate frist;
    protected String ventearsak;

    public VarselOmRevurderingBekreftelse setFrist(LocalDate frist) {
        this.frist = frist;
        return this;
    }

    public VarselOmRevurderingBekreftelse bekreftSendVarsel(Venteårsak årsak) {
        sendVarsel = true;
        ventearsak = årsak.name();
        return this;
    }

    public VarselOmRevurderingBekreftelse bekreftIkkeSendVarsel() {
        sendVarsel = false;
        return this;
    }

    @Override
    public String aksjonspunktKode() {
        return "5026";
    }
}
