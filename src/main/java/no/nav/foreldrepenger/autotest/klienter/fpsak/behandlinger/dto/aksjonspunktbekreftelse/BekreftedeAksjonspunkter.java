package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse;

import java.util.List;
import java.util.UUID;

import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.Behandling;

public class BekreftedeAksjonspunkter {

    protected UUID behandlingUuid;
    protected int behandlingVersjon;
    protected List<AksjonspunktBekreftelse> bekreftedeAksjonspunktDtoer;

    public BekreftedeAksjonspunkter(Behandling behandling,
                                    List<AksjonspunktBekreftelse> aksjonspunktBekreftelser) {
        this(behandling.uuid, behandling.versjon, aksjonspunktBekreftelser);
    }

    public BekreftedeAksjonspunkter(UUID behandlingUuid,
                                    int behandlingVersjon,
                                    List<AksjonspunktBekreftelse> bekreftedeAksjonspunktDtoer) {
        this.behandlingUuid = behandlingUuid;
        this.behandlingVersjon = behandlingVersjon;
        this.bekreftedeAksjonspunktDtoer = bekreftedeAksjonspunktDtoer;
    }
}
