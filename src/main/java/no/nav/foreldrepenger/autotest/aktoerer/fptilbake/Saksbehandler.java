package no.nav.foreldrepenger.autotest.aktoerer.fptilbake;

import no.nav.foreldrepenger.autotest.aktoerer.Aktoer;
import no.nav.foreldrepenger.autotest.klienter.fptilbake.behandlinger.BehandlingerKlient;
import no.nav.foreldrepenger.autotest.klienter.fptilbake.behandlinger.dto.BehandlingOpprett;

import java.io.IOException;
import java.util.UUID;

public class Saksbehandler extends Aktoer {

    private BehandlingerKlient behandlingerKlient;

    public Saksbehandler() {
        super();
        behandlingerKlient = new BehandlingerKlient(session);
    }

    public Saksbehandler(Rolle rolle) throws IOException {
        this();
        erLoggetInnMedRolle(rolle);
    }

    public void opprettTilbakekreving(Long saksnummer, UUID uuid) throws Exception {
        behandlingerKlient.putTilbakekreving(new BehandlingOpprett(saksnummer, uuid, "BT-007", "FP"));
    }
}
