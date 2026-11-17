package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse;

import java.util.List;
import java.util.UUID;

import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.Behandling;

public class OverstyrAksjonspunkter {

    protected UUID behandlingUuid;
    protected int behandlingVersjon;
    protected List<AksjonspunktBekreftelse> overstyrteAksjonspunktDtoer;

    public OverstyrAksjonspunkter(Behandling behandling,
            List<AksjonspunktBekreftelse> aksjonspunktBekreftelser) {
        this(behandling.uuid, behandling.versjon, aksjonspunktBekreftelser);
    }

    public OverstyrAksjonspunkter(UUID behandlingUuid, int behandlingVersjon,
                                  List<AksjonspunktBekreftelse> bekreftedeAksjonspunktDtoer) {
        super();
        this.behandlingUuid = behandlingUuid;
        this.behandlingVersjon = behandlingVersjon;
        this.overstyrteAksjonspunktDtoer = bekreftedeAksjonspunktDtoer;
    }
}
