package no.nav.foreldrepenger.autotest.aktoerer.saksbehandler.fpsak;

import java.util.ArrayList;
import java.util.List;

import io.qameta.allure.Allure;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.AksjonspunktBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.OverstyrAksjonspunkter;
import no.nav.foreldrepenger.autotest.klienter.vtp.sikkerhet.azure.SaksbehandlerRolle;

public class Overstyrer extends Saksbehandler {

    public Overstyrer() {
        super(SaksbehandlerRolle.OVERSTYRER);
    }

    /*
     * Oversyring
     */
    // TODO: Legg på sjekk om behandling er ferdigbehandlet eller ei
    public void overstyr(AksjonspunktBekreftelse bekreftelse) {
        List<AksjonspunktBekreftelse> bekreftelser = new ArrayList<>();
        bekreftelser.add(bekreftelse);
        overstyr(bekreftelser);
    }

    public void overstyr(List<AksjonspunktBekreftelse> bekreftelser) {
        Allure.step("Overstyrer aksjonspunkter", () -> {
            OverstyrAksjonspunkter aksjonspunkter = new OverstyrAksjonspunkter(valgtFagsak, valgtBehandling, bekreftelser);
            behandlingerKlient.overstyr(aksjonspunkter);
            refreshBehandling();
        });
    }
}
