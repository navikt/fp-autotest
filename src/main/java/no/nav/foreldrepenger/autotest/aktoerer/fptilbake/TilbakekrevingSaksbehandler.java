package no.nav.foreldrepenger.autotest.aktoerer.fptilbake;

import no.nav.foreldrepenger.autotest.aktoerer.Aktoer;
import no.nav.foreldrepenger.autotest.klienter.fptilbake.behandlinger.BehandlingerKlient;
import no.nav.foreldrepenger.autotest.klienter.fptilbake.behandlinger.dto.Behandling;
import no.nav.foreldrepenger.autotest.klienter.fptilbake.behandlinger.dto.BehandlingOpprett;
import no.nav.foreldrepenger.autotest.klienter.fptilbake.behandlinger.dto.aksjonspunkt.AksjonspunktDto;
import no.nav.foreldrepenger.autotest.klienter.fptilbake.okonomi.OkonomiKlient;
import no.nav.foreldrepenger.autotest.klienter.fptilbake.okonomi.dto.Kravgrunnlag;
import no.nav.foreldrepenger.autotest.util.vent.Vent;

import java.util.List;
import java.util.UUID;

public class TilbakekrevingSaksbehandler extends Aktoer {

    public List<Behandling> behandlingList;
    public Behandling valgtBehandling;

    private BehandlingerKlient behandlingerKlient;
    private OkonomiKlient okonomiKlient;

    public TilbakekrevingSaksbehandler() {
        super();
        behandlingerKlient = new BehandlingerKlient(session);
        okonomiKlient = new OkonomiKlient(session);
    }

    // Behandlinger actions
    public void opprettTilbakekreving(Long saksnummer, UUID uuid, String ytelseType) throws Exception {
        behandlingerKlient.putTilbakekreving(new BehandlingOpprett(saksnummer, uuid, "BT-007", ytelseType));
    }

    public void hentSisteBehandling(Long saksnummer) throws Exception {
        behandlingList = behandlingerKlient.hentAlleTbkBehandlinger(saksnummer);
        valgtBehandling = null;

        if (behandlingList.isEmpty()){
            throw new RuntimeException("Finnes ingen behandlinger på saksnummer");
        }
        else if (behandlingList.size() == 1) {
            valgtBehandling = behandlingList.get(0);
        }
        else{
            valgtBehandling = behandlingList.get(behandlingList.size() -1);
        }
    }

    private void refreshBehandling() throws Exception{
        valgtBehandling = behandlingerKlient.hentTbkBehandling(valgtBehandling.id);
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

    // Aksjonspunkt actions
    private AksjonspunktDto hentAksjonspunkt(String kode) throws Exception {
        for (AksjonspunktDto aksjonspunktDto : behandlingerKlient.hentAlleAksjonspunkter(valgtBehandling.id)) {
            if (aksjonspunktDto.definisjon.kode.equals(kode)){
                return aksjonspunktDto;
            }
        }
        return null;
    }
    public boolean harAktivtAksjonspunkt(String kode) throws Exception {
        AksjonspunktDto aksjonspunktDto = hentAksjonspunkt(kode);
        if (aksjonspunktDto == null){ return false; }
        return aksjonspunktDto.erAktivt;
    }

    // Vent actions
    public void ventTilBehandlingErPåVent() throws Exception{
        if (valgtBehandling.behandlingPaaVent) {
            return;
        }
        Vent.til(() -> {
            refreshBehandling();
            return valgtBehandling.behandlingPaaVent;
        }, 60, "Behandling kom aldri på vent");
    }
    public void ventTilBehandlingHarAktivtAksjonspunkt(String aksjonspunktKode) throws Exception{
        if (harAktivtAksjonspunkt(aksjonspunktKode)){
            return;
        }
        Vent.til(() -> {
            refreshBehandling();
            return harAktivtAksjonspunkt(aksjonspunktKode);
        }, 60, "Aksjonspunkt" + aksjonspunktKode + "ble aldri oppnådd");
    }
}
