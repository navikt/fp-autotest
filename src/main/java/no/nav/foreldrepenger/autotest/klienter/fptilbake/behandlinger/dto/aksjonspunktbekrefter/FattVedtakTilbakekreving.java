package no.nav.foreldrepenger.autotest.klienter.fptilbake.behandlinger.dto.aksjonspunktbekrefter;

import no.nav.foreldrepenger.autotest.klienter.Fagsystem;

import java.util.ArrayList;
import java.util.List;

@AksjonspunktKode(kode = "5005", fagsystem = Fagsystem.FPTILBAKE)
public class FattVedtakTilbakekreving extends AksjonspunktBehandling{

    protected String begrunnelse = null;
    protected List<FattVedtakDetaljerDto> aksjonspunktGodkjenningDtos = new ArrayList<>();

    public FattVedtakTilbakekreving() {
        this.kode = "5005";
    }

    public void godkjennAksjonspunkt(int kode) {
        aksjonspunktGodkjenningDtos.add(new FattVedtakDetaljerDto(kode, true));
    }
}
