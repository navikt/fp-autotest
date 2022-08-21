package no.nav.foreldrepenger.autotest.klienter.fptilbake.behandlinger.dto.aksjonspunktbekrefter;

import java.util.List;
import java.util.UUID;

import no.nav.foreldrepenger.autotest.klienter.fptilbake.behandlinger.dto.Behandling;
import no.nav.foreldrepenger.common.domain.Saksnummer;

public class BehandledeAksjonspunkter {

    protected UUID behandlingUuid;
    protected Saksnummer saksnummer;
    protected int behandlingVersjon;
    protected List<AksjonspunktBehandling> bekreftedeAksjonspunktDtoer;

    public BehandledeAksjonspunkter(UUID behandlingUuid, Saksnummer saksnummer, int behandlingVersjon,
            List<AksjonspunktBehandling> bekreftedeAksjonspunktDtoer) {

        this.behandlingUuid = behandlingUuid;
        this.saksnummer = saksnummer;
        this.behandlingVersjon = behandlingVersjon;
        this.bekreftedeAksjonspunktDtoer = bekreftedeAksjonspunktDtoer;
    }

    public BehandledeAksjonspunkter(Behandling behandling, Saksnummer saksnummer,
            List<AksjonspunktBehandling> bekreftedeAksjonspunktDtoer) {
        this(behandling.uuid, saksnummer, behandling.versjon, bekreftedeAksjonspunktDtoer);
    }
}
