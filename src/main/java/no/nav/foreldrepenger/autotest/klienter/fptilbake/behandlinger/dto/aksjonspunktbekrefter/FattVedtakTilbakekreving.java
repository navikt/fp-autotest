package no.nav.foreldrepenger.autotest.klienter.fptilbake.behandlinger.dto.aksjonspunktbekrefter;

import java.util.ArrayList;
import java.util.List;

import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.AksjonspunktBekreftelse;

public class FattVedtakTilbakekreving extends AksjonspunktBekreftelse {

    protected List<FattVedtakDetaljerDto> aksjonspunktGodkjenningDtos = new ArrayList<>();

    public void godkjennAksjonspunkt(int kode) {
        aksjonspunktGodkjenningDtos.add(new FattVedtakDetaljerDto(kode, true));
    }

    @Override
    public boolean erBeslutterAksjonspunkt() {
        return true;
    }

    @Override
    public String aksjonspunktKode() {
        return "5005";
    }
}
