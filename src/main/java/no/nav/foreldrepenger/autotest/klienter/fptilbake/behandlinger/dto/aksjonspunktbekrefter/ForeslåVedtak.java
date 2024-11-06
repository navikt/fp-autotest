package no.nav.foreldrepenger.autotest.klienter.fptilbake.behandlinger.dto.aksjonspunktbekrefter;

import java.util.ArrayList;
import java.util.List;

import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.AksjonspunktBekreftelse;

public class ForeslåVedtak extends AksjonspunktBekreftelse {

    protected List<VedtakPerioderMedTekst> perioderMedTekst = new ArrayList<>();

    @Override
    public String aksjonspunktKode() {
        return "5004";
    }
}
