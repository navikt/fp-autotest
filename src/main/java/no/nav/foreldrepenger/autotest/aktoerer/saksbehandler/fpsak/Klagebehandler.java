package no.nav.foreldrepenger.autotest.aktoerer.saksbehandler.fpsak;

import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.KlageVurderingResultatAksjonspunktMellomlagringDto;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.AksjonspunktKoder;
import no.nav.foreldrepenger.autotest.klienter.vtp.sikkerhet.azure.SaksbehandlerRolle;

public class Klagebehandler extends Saksbehandler {

    public Klagebehandler() {
        super(SaksbehandlerRolle.KLAGEBEHANDLER);
    }

    public void mellomlagreKlage() {
        behandlingerKlient.mellomlagre(
                new KlageVurderingResultatAksjonspunktMellomlagringDto(valgtBehandling,
                        hentAksjonspunkt(AksjonspunktKoder.MANUELL_VURDERING_AV_KLAGE_NFP)));
        refreshBehandling();
    }
}
