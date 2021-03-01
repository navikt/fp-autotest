package no.nav.foreldrepenger.autotest.verdikjedetester;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import io.qameta.allure.Description;
import no.nav.foreldrepenger.autotest.base.ForeldrepengerTestBase;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.BehandlingResultatType;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.avklarfakta.AvklarFaktaTerminBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.avklarfakta.AvklarLovligOppholdBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.historikk.dto.HistorikkinnslagType;
import no.nav.foreldrepenger.autotest.søknad.erketyper.SøknadEngangsstønadErketyper;
import no.nav.foreldrepenger.autotest.søknad.modell.BrukerRolle;
import no.nav.foreldrepenger.autotest.søknad.modell.Fødselsnummer;
import no.nav.foreldrepenger.autotest.søknad.modell.felles.annenforelder.NorskForelder;
import no.nav.foreldrepenger.autotest.util.testscenario.modell.Familie;

@Execution(ExecutionMode.CONCURRENT)
@Tag("verdikjede")
class VerdikjedeEngangsstonad extends ForeldrepengerTestBase {

    @Test
    @DisplayName("1: Mor er tredjelandsborger og søker engangsstønad")
    @Description("Mor er tredjelandsborger med statsborgerskap i USA og har ikke registrert medlemsskap i norsk folketrygd.")
    void MorTredjelandsborgerSøkerEngangsStønadTest() {
        var familie = new Familie("505");
        var termindato = LocalDate.now().plusWeeks(3);
        var søknad = SøknadEngangsstønadErketyper.lagEngangstønadTermin(BrukerRolle.MOR, termindato)
                .medAnnenForelder(lagNorskAnnenforeldre(familie.far().fødselsnummer()));
        var saksnummer = familie.mor().søk(søknad.build());

        saksbehandler.hentFagsak(saksnummer);
        var avklarFaktaTerminBekreftelse = saksbehandler
                .hentAksjonspunktbekreftelse(AvklarFaktaTerminBekreftelse.class);
        avklarFaktaTerminBekreftelse.setBegrunnelse("Informasjon er hentet fra søknadden og godkjennes av autotest.");
        saksbehandler.bekreftAksjonspunkt(avklarFaktaTerminBekreftelse);

        var avklarLovligOppholdBekreftelse = saksbehandler
                .hentAksjonspunktbekreftelse(AvklarLovligOppholdBekreftelse.class);
        avklarLovligOppholdBekreftelse.bekreftBrukerHarLovligOpphold();
        saksbehandler.bekreftAksjonspunkt(avklarLovligOppholdBekreftelse);

        foreslårOgFatterVedtakVenterTilAvsluttetBehandlingOgSjekkerOmBrevErSendt(saksnummer, false);

        assertThat(saksbehandler.valgtBehandling.hentBehandlingsresultat())
                .as("Behandlingsresultat")
                .isEqualTo(BehandlingResultatType.INNVILGET);
        assertThat(saksbehandler.valgtBehandling.getBeregningResultatEngangsstonad().getBeregnetTilkjentYtelse())
                .as("Beregnet tilkjent ytelse")
                .isPositive();

        var dokumentId = saksbehandler.hentDokumentIdFraHistorikkinnslag(HistorikkinnslagType.BREV_SENT);
        var pdf = fordel.hentJournalførtDokument(dokumentId, "ARKIV");
        assertThat(is_pdf(pdf))
                .as("Sjekker om byte array er av typen PDF")
                .isTrue();
    }

    private NorskForelder lagNorskAnnenforeldre(String indent) {
        return new NorskForelder(new Fødselsnummer(indent), "");
    }
}
