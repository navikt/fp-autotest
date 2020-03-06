package no.nav.foreldrepenger.autotest.klienter.fptilbake.behandlinger.dto.aksjonspunktbekrefter;

import no.nav.foreldrepenger.autotest.klienter.Fagsystem;

import java.util.ArrayList;
import java.util.List;

@AksjonspunktKode(kode = "5004", fagsystem = Fagsystem.FPTILBAKE)
public class ForeslåVedtak extends AksjonspunktBehandling{

    protected List<VedtakPerioderMedTekst> perioderMedTekst = new ArrayList<>();

    public ForeslåVedtak() {
        this.kode = "5004";
    }
}
