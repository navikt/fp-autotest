package no.nav.foreldrepenger.autotest.verdikjedetester;

import static no.nav.foreldrepenger.autotest.erketyper.SøknadEngangstønadErketyper.lagEngangstønadTermin;

import java.time.LocalDate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import io.qameta.allure.Description;
import no.nav.foreldrepenger.autotest.aktoerer.Aktoer;
import no.nav.foreldrepenger.autotest.base.ForeldrepengerTestBase;
import no.nav.foreldrepenger.autotest.dokumentgenerator.foreldrepengesoknad.SøkersRolle;
import no.nav.foreldrepenger.autotest.dokumentgenerator.foreldrepengesoknad.builders.EngangstønadBuilder;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.avklarfakta.AvklarFaktaTerminBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.avklarfakta.AvklarLovligOppholdBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.historikk.dto.HistorikkInnslag;
import no.nav.foreldrepenger.vtp.testmodell.dokument.modell.koder.DokumenttypeId;

@Execution(ExecutionMode.CONCURRENT)
@Tag("verdikjede")
public class VerdikjedeEngangsstonad extends ForeldrepengerTestBase {

    @Test
    @DisplayName("1: Mor er tredjelandsborger og søker engangsstønad")
    @Description("Mor er tredjelandsborger med statsborgerskap i USA og har ikke registrert medlemsskap i norsk folketrygd.")
    public void MorTredjelandsborgerSøkerEngangsStønadTest() {
        var testscenario = opprettTestscenario("505");
        var søkerAktørId = testscenario.getPersonopplysninger().getSøkerAktørIdent();
        var termindato = LocalDate.now().plusWeeks(3);
        EngangstønadBuilder søknad = lagEngangstønadTermin(
                søkerAktørId,
                SøkersRolle.MOR,
                termindato);
        fordel.erLoggetInnMedRolle(Aktoer.Rolle.SAKSBEHANDLER);
        long saksnummer = fordel.sendInnSøknad(
                søknad.build(),
                testscenario,
                DokumenttypeId.FOEDSELSSOKNAD_ENGANGSSTONAD);

        saksbehandler.erLoggetInnMedRolle(Aktoer.Rolle.SAKSBEHANDLER);
        saksbehandler.hentFagsak(saksnummer);
        AvklarFaktaTerminBekreftelse avklarFaktaTerminBekreftelse = saksbehandler
                .hentAksjonspunktbekreftelse(AvklarFaktaTerminBekreftelse.class);
        avklarFaktaTerminBekreftelse.setBegrunnelse("Informasjon er hentet fra søknadden og godkjennes av autotest.");
        saksbehandler.bekreftAksjonspunkt(avklarFaktaTerminBekreftelse);

        AvklarLovligOppholdBekreftelse avklarLovligOppholdBekreftelse = saksbehandler
                .hentAksjonspunktbekreftelse(AvklarLovligOppholdBekreftelse.class);
        avklarLovligOppholdBekreftelse.bekreftBrukerHarLovligOpphold();
        saksbehandler.bekreftAksjonspunkt(avklarLovligOppholdBekreftelse);

        foreslårOgFatterVedtakVenterTilAvsluttetBehandlingOgSjekkerOmBrevErSendt(saksnummer, false);

        verifiserLikhet(beslutter.valgtBehandling.hentBehandlingsresultat(), "INNVILGET");
        verifiser(saksbehandler.valgtBehandling.getBeregningResultatEngangsstonad().getBeregnetTilkjentYtelse() > 0,
                "Forventer at det utbetales mer enn 0 kr.");

        var dokumentId = saksbehandler.hentDokumentIdFraHistorikkinnslag(HistorikkInnslag.Type.BREV_SENT);
        var pdf = fordel.hentJournalførtDokument(dokumentId, "ARKIV");
        verifiser(is_pdf(pdf), "Sjekker om byte array er av typen PDF");

    }
}
