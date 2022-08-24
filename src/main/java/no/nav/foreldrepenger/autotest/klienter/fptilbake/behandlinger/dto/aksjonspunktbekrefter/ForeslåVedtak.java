package no.nav.foreldrepenger.autotest.klienter.fptilbake.behandlinger.dto.aksjonspunktbekrefter;

import java.util.ArrayList;
import java.util.List;

import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.AksjonspunktBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.BekreftelseKode;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.Fagsystem;

@BekreftelseKode(kode = "5004", fagsystem = Fagsystem.FPTILBAKE)
public class ForeslåVedtak extends AksjonspunktBekreftelse {

    protected List<VedtakPerioderMedTekst> perioderMedTekst = new ArrayList<>();

}
