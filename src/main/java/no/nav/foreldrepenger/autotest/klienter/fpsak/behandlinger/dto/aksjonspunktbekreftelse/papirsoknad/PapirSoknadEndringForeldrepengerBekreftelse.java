package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.papirsoknad;

import java.time.LocalDate;

import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.AksjonspunktBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.BekreftelseKode;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.Behandling;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.papirsøknad.FordelingDto;
import no.nav.foreldrepenger.autotest.klienter.fpsak.fagsak.dto.Fagsak;

@BekreftelseKode(kode="5057")
public class PapirSoknadEndringForeldrepengerBekreftelse extends AksjonspunktBekreftelse {

    protected String tema = "IKKE_RELEVANT";

    protected String soker = "IKKE_RELEVANT";

    protected String soknadstype = "ENDRING_FP";

    protected LocalDate mottattDato = LocalDate.now();

    protected FordelingDto tidsromPermisjon = new FordelingDto();

    public PapirSoknadEndringForeldrepengerBekreftelse(Fagsak fagsak, Behandling behandling) {
        super(fagsak, behandling);
    }

    public void setFordeling(FordelingDto tidsromPermisjon) {
        this.tidsromPermisjon = tidsromPermisjon;
    }
}
