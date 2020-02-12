package no.nav.foreldrepenger.autotest.aktoerer.fptilbake;

import no.nav.foreldrepenger.autotest.aktoerer.Aktoer;
import no.nav.foreldrepenger.autotest.klienter.fptilbake.behandlinger.BehandlingerKlient;
import no.nav.foreldrepenger.autotest.klienter.fptilbake.behandlinger.dto.BehandlingOpprett;
import no.nav.foreldrepenger.autotest.klienter.fptilbake.okonomi.OkonomiKlient;
import no.nav.foreldrepenger.autotest.klienter.fptilbake.okonomi.dto.Kravgrunnlag;

import java.util.UUID;

public class TilbakekrevingSaksbehandler extends Aktoer {

    private BehandlingerKlient behandlingerKlient;
    private OkonomiKlient okonomiKlient;

    public TilbakekrevingSaksbehandler() {
        super();
        behandlingerKlient = new BehandlingerKlient(session);
        okonomiKlient = new OkonomiKlient(session);
    }

    public void opprettTilbakekreving(Long saksnummer, UUID uuid, String ytelseType) throws Exception {
        behandlingerKlient.putTilbakekreving(new BehandlingOpprett(saksnummer, uuid, "BT-007", ytelseType));
    }

    public void sendNyttKravgrunnlag(Long saksnummer, String ident, int behandlingId, String ytelseType) throws Exception {
        Kravgrunnlag kravgrunnlag = new Kravgrunnlag(saksnummer, ident, behandlingId, ytelseType, "NY");
        kravgrunnlag.leggTilPeriode();
        okonomiKlient.putGrunnlag(kravgrunnlag);
    }

    public void sendEndretKravgrunnlag(Long saksnummer, String ident, int behandlingId, String ytelseType) throws Exception {
        okonomiKlient.putGrunnlag(new Kravgrunnlag(saksnummer, ident, behandlingId, ytelseType, "ENDR"));
    }
}
