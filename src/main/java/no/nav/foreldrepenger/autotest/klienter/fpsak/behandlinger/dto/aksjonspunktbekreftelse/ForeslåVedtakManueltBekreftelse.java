package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse;

import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.AksjonspunktKoder;

public class ForeslåVedtakManueltBekreftelse extends AksjonspunktBekreftelse {

    @Override
    public String aksjonspunktKode() {
        return AksjonspunktKoder.FORESLÅ_VEDTAK_MANUELT;
    }
}
