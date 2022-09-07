package no.nav.foreldrepenger.autotest.aktoerer.saksbehandler.fpsak;

import no.nav.foreldrepenger.autotest.aktoerer.Aktoer;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.KlageVurderingResultatAksjonspunktMellomlagringDto;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.AksjonspunktKoder;

public class Klagebehandler extends Saksbehandler {

    public Klagebehandler() {
        super(Aktoer.Rolle.KLAGEBEHANDLER);
    }

    public void mellomlagreKlage() {
        behandlingerKlient.mellomlagre(
                new KlageVurderingResultatAksjonspunktMellomlagringDto(valgtBehandling,
                        hentAksjonspunkt(AksjonspunktKoder.MANUELL_VURDERING_AV_KLAGE_NFP)));
        refreshBehandling();
    }
}
