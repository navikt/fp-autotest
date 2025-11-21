package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.papirsoknad;

import java.time.LocalDate;

import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.AksjonspunktBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.papirsøknad.FordelingDto;

public class PapirSøknadEndringForeldrepengerBekreftelse extends AksjonspunktBekreftelse {

    // FamilieHendelseType
    protected String tema = "FODSL";

    // ForeldreType // burde vært RelasjonsRolleType?
    protected String søker = "MOR";

    // FagsakYtelseType
    protected String søknadstype = "FP";

    protected LocalDate mottattDato = LocalDate.now();

    protected FordelingDto tidsromPermisjon = new FordelingDto();

    protected boolean annenForelderInformert;

    public void setFordeling(FordelingDto tidsromPermisjon) {
        this.tidsromPermisjon = tidsromPermisjon;
    }

    public void setAnnenForelderInformert(boolean annenForelderInformert) {
        this.annenForelderInformert = annenForelderInformert;
    }

    @Override
    public String aksjonspunktKode() {
        return "5057";
    }
}
