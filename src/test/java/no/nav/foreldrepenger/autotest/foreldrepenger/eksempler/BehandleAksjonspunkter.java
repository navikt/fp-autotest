package no.nav.foreldrepenger.autotest.foreldrepenger.eksempler;

import no.nav.foreldrepenger.autotest.aktoerer.Aktoer.Rolle;
import no.nav.foreldrepenger.autotest.base.FpsakTestBase;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.avklarfakta.AvklarFaktaTerminBekreftelse;
import no.nav.foreldrepenger.vtp.dokumentgenerator.foreldrepengesoknad.SøkersRolle;
import no.nav.foreldrepenger.vtp.dokumentgenerator.foreldrepengesoknad.builders.SøknadBuilder;
import no.nav.foreldrepenger.vtp.dokumentgenerator.foreldrepengesoknad.erketyper.SøknadErketyper;
import no.nav.foreldrepenger.vtp.kontrakter.TestscenarioDto;
import no.nav.foreldrepenger.vtp.testmodell.dokument.modell.koder.DokumenttypeId;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

@Tag("eksempel")
public class BehandleAksjonspunkter extends FpsakTestBase {

    @Test
    @Disabled
    public void godkjenneTermindato() throws Exception {
        //Opprett scenario og søknad
        TestscenarioDto testscenario = opprettTestscenarioFraVTPTemplate("50");
        SøknadBuilder søknad = SøknadErketyper.engangstønadsøknadTerminErketype(
                testscenario.getPersonopplysninger().getSøkerAktørIdent(),
                SøkersRolle.MOR,
                1,
                LocalDate.now().plusWeeks(3));

        //Send inn søknad
        fordel.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        long saksnummer = fordel.sendInnSøknad(søknad.build(), testscenario, DokumenttypeId.FOEDSELSSOKNAD_ENGANGSSTONAD);

        //Behandle sak
        saksbehandler.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        saksbehandler.hentFagsak(saksnummer);
        AvklarFaktaTerminBekreftelse bekreftelse = saksbehandler.hentAksjonspunktbekreftelse(AvklarFaktaTerminBekreftelse.class);
        bekreftelse.setTermindato(LocalDate.now().plusWeeks(1));
        bekreftelse.setAntallBarn(1);
        saksbehandler.bekreftAksjonspunkt(bekreftelse);

        verifiserLikhet(saksbehandler.valgtBehandling.hentBehandlingsresultat(), "INNVILGET", "Behandlingsresultat");
    }

}
