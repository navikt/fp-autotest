package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.papirsoknad;

import java.time.LocalDate;

import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.AksjonspunktBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.BekreftelseKode;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.Behandling;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.papirsøknad.FordelingDto;
import no.nav.foreldrepenger.autotest.klienter.fpsak.fagsak.dto.Fagsak;

@BekreftelseKode(kode="5057")
public class PapirSoknadEndringForeldrepengerBekreftelse extends AksjonspunktBekreftelse {

    // FamilieHendelseType
    protected String tema = "FODSL";

    // ForeldreType  // burde vært RelasjonsRolleType?
    protected String soker = "MOR";

    // FagsakYtelseType
    protected String soknadstype = "FP";

    protected LocalDate mottattDato = LocalDate.now();

    protected FordelingDto tidsromPermisjon = new FordelingDto();

    public void setFordeling(FordelingDto tidsromPermisjon) {
        this.tidsromPermisjon = tidsromPermisjon;
    }

    @Override
    public void setFagsakOgBehandling(Fagsak fagsak, Behandling behandling) {
        super.setFagsakOgBehandling(fagsak, behandling);
    }
}
