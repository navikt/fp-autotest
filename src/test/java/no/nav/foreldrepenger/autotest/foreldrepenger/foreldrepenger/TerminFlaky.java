package no.nav.foreldrepenger.autotest.foreldrepenger.foreldrepenger;

import io.qameta.allure.Description;
import no.nav.foreldrepenger.autotest.aktoerer.Aktoer;
import no.nav.foreldrepenger.autotest.base.ForeldrepengerTestBase;
import no.nav.foreldrepenger.autotest.klienter.fpsak.historikk.dto.HistorikkInnslag;
import no.nav.foreldrepenger.autotest.dokumentgenerator.foreldrepengesoknad.SøkersRolle;
import no.nav.foreldrepenger.autotest.dokumentgenerator.foreldrepengesoknad.builders.SøknadBuilder;
import no.nav.foreldrepenger.autotest.dokumentgenerator.inntektsmelding.builders.InntektsmeldingBuilder;
import no.nav.foreldrepenger.vtp.kontrakter.TestscenarioDto;
import no.nav.foreldrepenger.vtp.testmodell.dokument.modell.koder.DokumenttypeId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static no.nav.foreldrepenger.autotest.util.AllureHelper.debugLoggBehandling;
import static no.nav.foreldrepenger.autotest.util.AllureHelper.debugLoggHistorikkinnslag;
import static no.nav.foreldrepenger.autotest.dokumentgenerator.foreldrepengesoknad.erketyper.SøknadErketyper.foreldrepengesøknadTerminErketype;

@Tag("flaky")
public class TerminFlaky extends ForeldrepengerTestBase {

    @Test
    @DisplayName("Mor søker med ett arbeidsforhold")
    @Description("Mor søkner med ett arbeidsforhold. Forventer at vedtak blir fattet og brev blir sendt")
    public void MorSøkerMedEttArbeidsforhold() throws Exception {
        TestscenarioDto testscenario = opprettTestscenarioFraVTPTemplate("55");
        LocalDate termindato = LocalDate.now().plusWeeks(3);
        LocalDate startDatoForeldrepenger = termindato.minusWeeks(3);
        String aktørID = testscenario.getPersonopplysninger().getSøkerAktørIdent();
        SøknadBuilder søknad = foreldrepengesøknadTerminErketype(aktørID, SøkersRolle.MOR, 1, termindato);

        fordel.erLoggetInnMedRolle(Aktoer.Rolle.SAKSBEHANDLER);
        long saksnummer = fordel.sendInnSøknad(søknad.build(), testscenario, DokumenttypeId.FOEDSELSSOKNAD_FORELDREPENGER);
        List<InntektsmeldingBuilder> inntektsmeldinger = makeInntektsmeldingFromTestscenario(testscenario, startDatoForeldrepenger);
        fordel.sendInnInntektsmeldinger(inntektsmeldinger, testscenario, saksnummer);

        saksbehandler.erLoggetInnMedRolle(Aktoer.Rolle.SAKSBEHANDLER);

        saksbehandler.hentFagsak(saksnummer);
        saksbehandler.velgFørstegangsbehandling();
        debugLoggBehandling(saksbehandler.valgtBehandling);
        debugLoggHistorikkinnslag(saksbehandler.getHistorikkInnslag());
        saksbehandler.ventTilHistorikkinnslag(HistorikkInnslag.VEDTAK_FATTET);
        saksbehandler.ventTilHistorikkinnslag(HistorikkInnslag.BREV_BESTILT);
        //TODO (OL): Byttet verifisering på historikkinnslag til asynk venting. Feilet med at behandlingen ikke hadde historikkinnslag
        //verifiser(saksbehandler.harHistorikkinnslag("Vedtak fattet"), "behandling har ikke historikkinslag 'Vedtak fattet'");
        //verifiser(saksbehandler.harHistorikkinnslag(HistorikkInnslag.BREV_SENT), "behandling har ikke historikkinslag 'Brev sendt'");
    }

}
