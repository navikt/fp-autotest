package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse;

import java.util.List;
import java.util.UUID;

public record BekreftedeAksjonspunkter(UUID behandlingUuid,
                                       int behandlingVersjon,
                                       List<AksjonspunktBekreftelse> bekreftedeAksjonspunktDtoer) {

}
