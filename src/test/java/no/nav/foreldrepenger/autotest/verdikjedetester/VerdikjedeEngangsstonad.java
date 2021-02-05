package no.nav.foreldrepenger.autotest.verdikjedetester;

import java.time.LocalDate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import io.qameta.allure.Description;
import no.nav.foreldrepenger.autotest.aktoerer.Aktoer;
import no.nav.foreldrepenger.autotest.base.ForeldrepengerTestBase;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.avklarfakta.AvklarFaktaTerminBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.avklarfakta.AvklarLovligOppholdBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.historikk.dto.HistorikkInnslag;
import no.nav.foreldrepenger.autotest.søknad.erketyper.SøknadEngangsstønadErketyper;
import no.nav.foreldrepenger.autotest.søknad.modell.BrukerRolle;
import no.nav.foreldrepenger.autotest.søknad.modell.Fødselsnummer;
import no.nav.foreldrepenger.autotest.søknad.modell.felles.annenforelder.NorskForelder;

@Tag("verdikjede")
public class VerdikjedeEngangsstonad extends ForeldrepengerTestBase {

    @Test
    @DisplayName("1: Mor er tredjelandsborger og søker engangsstønad")
    @Description("Mor er tredjelandsborger med statsborgerskap i USA og har ikke registrert medlemsskap i norsk folketrygd.")
    public void MorTredjelandsborgerSøkerEngangsStønadTest() {
        var testscenario = opprettTestscenario("505");
        var termindato = LocalDate.now().plusWeeks(3);
        var søknad = SøknadEngangsstønadErketyper.lagEngangstønadTermin(BrukerRolle.MOR, termindato)
                .medAnnenForelder(new NorskForelder(new Fødselsnummer(testscenario.personopplysninger().annenpartIdent()), ""));
        var saksnummer = selvbetjening.sendInnSøknad(testscenario.personopplysninger().søkerIdent(), søknad.build());

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
