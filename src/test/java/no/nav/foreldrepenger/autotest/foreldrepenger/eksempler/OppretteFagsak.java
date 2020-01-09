package no.nav.foreldrepenger.autotest.foreldrepenger.eksempler;

import no.nav.foreldrepenger.autotest.aktoerer.Aktoer.Rolle;
import no.nav.foreldrepenger.autotest.base.FpsakTestBase;
import no.nav.foreldrepenger.autotest.dokumentgenerator.foreldrepengesoknad.SøkersRolle;
import no.nav.foreldrepenger.autotest.dokumentgenerator.foreldrepengesoknad.builders.SøknadBuilder;
import no.nav.foreldrepenger.autotest.dokumentgenerator.foreldrepengesoknad.builders.ytelse.ForeldrepengerYtelseBuilder;
import no.nav.foreldrepenger.autotest.dokumentgenerator.foreldrepengesoknad.erketyper.FordelingErketyper;
import no.nav.foreldrepenger.autotest.dokumentgenerator.foreldrepengesoknad.erketyper.SoekersRelasjonErketyper;
import no.nav.foreldrepenger.vtp.kontrakter.TestscenarioDto;
import no.nav.foreldrepenger.vtp.testmodell.dokument.modell.koder.DokumenttypeId;
import no.nav.vedtak.felles.xml.soeknad.felles.v3.SoekersRelasjonTilBarnet;
import no.nav.vedtak.felles.xml.soeknad.foreldrepenger.v3.Foreldrepenger;
import no.nav.vedtak.felles.xml.soeknad.uttak.v3.Fordeling;
import org.junit.jupiter.api.Tag;

import java.time.LocalDate;

@Tag("eksempel")
public class OppretteFagsak extends FpsakTestBase {


    public void oppretteTerminsøknad() throws Exception {
        //Opprett scenario og søknad
        TestscenarioDto testscenario = opprettTestscenarioFraVTPTemplate("50");
        SoekersRelasjonTilBarnet relasjonTilBarnet = SoekersRelasjonErketyper.termin(1, testscenario.getPersonopplysninger().getFødselsdato());
        Fordeling fordeling = FordelingErketyper.fordelingHappyCase(testscenario.getPersonopplysninger().getFødselsdato(), SøkersRolle.MOR);
        Foreldrepenger foreldrepenger = new ForeldrepengerYtelseBuilder(relasjonTilBarnet, fordeling).build();
        SøknadBuilder søknad = new SøknadBuilder(foreldrepenger, testscenario.getPersonopplysninger().getSøkerAktørIdent(), SøkersRolle.MOR);
        //Send inn søknad
        fordel.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        long saksnummer = fordel.sendInnSøknad(søknad.build(), testscenario, DokumenttypeId.FOEDSELSSOKNAD_ENGANGSSTONAD);

        //Behandle sak
        saksbehandler.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        saksbehandler.hentFagsak(saksnummer);

        saksbehandler.settBehandlingPåVent(LocalDate.now(), "AVV_DOK");
        verifiser(saksbehandler.valgtBehandling.erSattPåVent(), "Behandlingen er ikke satt på vent");

        saksbehandler.gjenopptaBehandling();
        verifiser(!saksbehandler.valgtBehandling.erSattPåVent(), "Behandlingen er satt på vent");

        saksbehandler.henleggBehandling(saksbehandler.kodeverk.BehandlingResultatType.getKode("HENLAGT_SØKNAD_TRUKKET"));

        saksbehandler.ventTilBehandlingsstatus("AVSLU");
    }
}
