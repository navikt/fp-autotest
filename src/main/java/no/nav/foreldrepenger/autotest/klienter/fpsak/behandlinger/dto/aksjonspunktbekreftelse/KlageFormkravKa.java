package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse;

import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.Behandling;
import no.nav.foreldrepenger.autotest.klienter.fpsak.fagsak.dto.Fagsak;

@BekreftelseKode(kode="5083")
public class KlageFormkravKa extends KlageFormkravBekreftelse {

    public KlageFormkravKa() {
        super();
    }

    @Override
    public void setFagsakOgBehandling(Fagsak fagsak, Behandling behandling) {
        super.setFagsakOgBehandling(fagsak, behandling);
        this.setPåklagdVedtak("" + behandling.id);
    }
}
