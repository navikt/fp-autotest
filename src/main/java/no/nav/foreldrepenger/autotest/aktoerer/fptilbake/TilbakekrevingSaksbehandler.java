package no.nav.foreldrepenger.autotest.aktoerer.fptilbake;

import no.nav.foreldrepenger.autotest.aktoerer.Aktoer;
import no.nav.foreldrepenger.autotest.klienter.fptilbake.behandlinger.BehandlingerKlient;
import no.nav.foreldrepenger.autotest.klienter.fptilbake.behandlinger.dto.Behandling;
import no.nav.foreldrepenger.autotest.klienter.fptilbake.behandlinger.dto.BehandlingOpprett;
import no.nav.foreldrepenger.autotest.klienter.fptilbake.fagsak.FagsakKlient;
import no.nav.foreldrepenger.autotest.klienter.fptilbake.fagsak.dto.Fagsak;
import no.nav.foreldrepenger.autotest.klienter.fptilbake.okonomi.OkonomiKlient;
import no.nav.foreldrepenger.autotest.klienter.fptilbake.okonomi.dto.Kravgrunnlag;

import java.util.List;
import java.util.UUID;

public class TilbakekrevingSaksbehandler extends Aktoer {

    public Fagsak valgtFagsak;
    public List<Behandling> behandlingList;
    public Behandling valgtBehandling;

    private BehandlingerKlient behandlingerKlient;
    private OkonomiKlient okonomiKlient;
    private FagsakKlient fagsakKlient;

    public TilbakekrevingSaksbehandler() {
        super();
        behandlingerKlient = new BehandlingerKlient(session);
        okonomiKlient = new OkonomiKlient(session);
        fagsakKlient = new FagsakKlient(session);
    }

    // Behandlinger actions
    public void opprettTilbakekreving(Long saksnummer, UUID uuid, String ytelseType) throws Exception {
        behandlingerKlient.putTilbakekreving(new BehandlingOpprett(saksnummer, uuid, "BT-007", ytelseType));
    }

    public void hentFagsak(String saksnummer) throws Exception {
        velgFagsak(fagsakKlient.getFagsak(saksnummer));
    }
    public void hentFagsak(Long saksnummer) throws Exception {
        hentFagsak(String.valueOf(saksnummer));
    }
    public void velgFagsak(Fagsak fagsak) throws Exception {
        if (fagsak == null) {
            throw new RuntimeException("Kan ikke velge fagsak. fagsak er null");
        }
        valgtFagsak = fagsak;

        behandlingList = behandlingerKlient.alle(fagsak.saksnummer);
        valgtBehandling = null;

        if (behandlingList.size() == 1) {
            valgtBehandling = behandlingList.get(0);
        }
    }


    // Økonomi actions
    public void sendNyttKravgrunnlag(Long saksnummer, String ident, int fpsakBehandlingId, String ytelseType, int behandlingId) throws Exception {
        Kravgrunnlag kravgrunnlag = new Kravgrunnlag(saksnummer, ident, String.valueOf(fpsakBehandlingId), ytelseType, "NY");
        okonomiKlient.putGrunnlag(kravgrunnlag, behandlingId);
    }

    public void sendEndretKravgrunnlag(Long saksnummer, String ident, int fpsakBehandlingId, String ytelseType, int behandlingId) throws Exception {
        Kravgrunnlag kravgrunnlag = new Kravgrunnlag(saksnummer, ident, String.valueOf(fpsakBehandlingId), ytelseType, "ENDR");
        okonomiKlient.putGrunnlag(kravgrunnlag, behandlingId);
    }
}
