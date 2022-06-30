package no.nav.foreldrepenger.autotest.klienter.fptilbake.behandlinger.dto.aksjonspunktbekrefter;

import java.util.List;

import no.nav.foreldrepenger.autotest.klienter.fptilbake.behandlinger.dto.Behandling;
import no.nav.foreldrepenger.common.domain.Saksnummer;

public class BehandledeAksjonspunkter {

    protected int behandlingId;
    protected Saksnummer saksnummer;
    protected int behandlingVersjon;
    protected List<AksjonspunktBehandling> bekreftedeAksjonspunktDtoer;

    public BehandledeAksjonspunkter(int behandlingId, Saksnummer saksnummer, int behandlingVersjon,
            List<AksjonspunktBehandling> bekreftedeAksjonspunktDtoer) {

        this.behandlingId = behandlingId;
        this.saksnummer = saksnummer;
        this.behandlingVersjon = behandlingVersjon;
        this.bekreftedeAksjonspunktDtoer = bekreftedeAksjonspunktDtoer;
    }

    public BehandledeAksjonspunkter(Behandling behandling, Saksnummer saksnummer,
            List<AksjonspunktBehandling> bekreftedeAksjonspunktDtoer) {
        this(behandling.id, saksnummer, behandling.versjon, bekreftedeAksjonspunktDtoer);
    }
}
