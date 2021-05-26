package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse;

import java.util.List;
import java.util.UUID;

import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.Behandling;
import no.nav.foreldrepenger.autotest.klienter.fpsak.fagsak.dto.Fagsak;

public class OverstyrAksjonspunkter {

    protected UUID behandlingUuid;
    protected String saksnummer;
    protected int behandlingVersjon;
    protected List<AksjonspunktBekreftelse> overstyrteAksjonspunktDtoer;

    public OverstyrAksjonspunkter(Fagsak fagsak, Behandling behandling,
            List<AksjonspunktBekreftelse> aksjonspunktBekreftelser) {
        this(behandling.uuid, fagsak.saksnummer().toString(), behandling.versjon, aksjonspunktBekreftelser);
    }

    public OverstyrAksjonspunkter(UUID behandlingUuid, String saksnummer, int behandlingVersjon,
                                  List<AksjonspunktBekreftelse> bekreftedeAksjonspunktDtoer) {
        super();
        this.behandlingUuid = behandlingUuid;
        this.saksnummer = saksnummer;
        this.behandlingVersjon = behandlingVersjon;
        this.overstyrteAksjonspunktDtoer = bekreftedeAksjonspunktDtoer;
    }
}
