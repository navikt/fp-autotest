package no.nav.foreldrepenger.autotest.foreldrepengerUtvidet.engangsstonad;

import static no.nav.foreldrepenger.autotest.søknad.erketyper.SøknadEngangsstønadErketyper.lagEngangstønadFødsel;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import io.qameta.allure.Description;
import no.nav.foreldrepenger.autotest.base.FpsakTestBase;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.BehandlingResultatType;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.InnsynResultatType;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.ForeslåVedtakBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.VurderingAvInnsynBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.historikk.dto.HistorikkinnslagType;
import no.nav.foreldrepenger.autotest.søknad.modell.BrukerRolle;
import no.nav.foreldrepenger.autotest.util.AllureHelper;
import no.nav.foreldrepenger.autotest.util.testscenario.modell.Familie;

@Tag("fpsak")
@Tag("engangsstonad")
class Innsyn extends FpsakTestBase {

    @Test
    @DisplayName("Behandle innsyn for mor - godkjent")
    @Description("Behandle innsyn for mor - godkjent happy case")
    void behandleInnsynMorGodkjent() {
        var familie = new Familie("50");
        var mor = familie.mor();
        var søknad = lagEngangstønadFødsel(BrukerRolle.MOR, familie.barn().fødselsdato());
        var saksnummer = mor.søk(søknad.build());

        saksbehandler.hentFagsak(saksnummer);
        saksbehandler.ventTilAvsluttetBehandling();
        assertThat(saksbehandler.valgtBehandling.hentBehandlingsresultat())
                .as("Behandlingsresultat")
                .isEqualTo(BehandlingResultatType.INNVILGET);

        saksbehandler.oprettBehandlingInnsyn(null);
        saksbehandler.ventPåOgVelgDokumentInnsynBehandling();

        var vurderingAvInnsynBekreftelse = saksbehandler
                .hentAksjonspunktbekreftelse(VurderingAvInnsynBekreftelse.class)
                .setMottattDato(LocalDate.now())
                .setInnsynResultatType(InnsynResultatType.INNVILGET)
                .skalSetteSakPåVent(false)
                .setBegrunnelse("Test");
        saksbehandler.bekreftAksjonspunkt(vurderingAvInnsynBekreftelse);

        saksbehandler.bekreftAksjonspunktMedDefaultVerdier(ForeslåVedtakBekreftelse.class);

        saksbehandler.ventTilAvsluttetBehandling();
        AllureHelper.debugLoggBehandlingsliste(saksbehandler.behandlinger);
        AllureHelper.debugLoggHistorikkinnslag(saksbehandler.getHistorikkInnslag());
        assertThat(saksbehandler.valgtBehandling.hentBehandlingsresultat())
                .as("Behandlingsresultat")
                .isEqualTo(BehandlingResultatType.INNSYN_INNVILGET);
        assertThat(saksbehandler.harHistorikkinnslagForBehandling(HistorikkinnslagType.BREV_BESTILT))
                .as("Historikkinnslag")
                .isTrue();
    }

    @Test
    @DisplayName("Behandle innsyn for mor - avvist")
    @Description("Behandle innsyn for mor - avvist ved vurdering")
    void behandleInnsynMorAvvist() {
        var familie = new Familie("50");
        var mor = familie.mor();
        var søknad = lagEngangstønadFødsel(BrukerRolle.MOR, familie.barn().fødselsdato());
        var saksnummer = mor.søk(søknad.build());

        saksbehandler.hentFagsak(saksnummer);
        saksbehandler.ventTilAvsluttetBehandling();
        assertThat(saksbehandler.valgtBehandling.hentBehandlingsresultat())
                .as("Behandlingsresultat")
                .isEqualTo(BehandlingResultatType.INNVILGET);

        saksbehandler.oprettBehandlingInnsyn(null);
        saksbehandler.ventPåOgVelgDokumentInnsynBehandling();

        var vurderingAvInnsynBekreftelse = saksbehandler
                .hentAksjonspunktbekreftelse(VurderingAvInnsynBekreftelse.class)
                .setMottattDato(LocalDate.now())
                .setInnsynResultatType(InnsynResultatType.AVVIST)
                .setBegrunnelse("Test");
        saksbehandler.bekreftAksjonspunkt(vurderingAvInnsynBekreftelse);

        saksbehandler.bekreftAksjonspunktMedDefaultVerdier(ForeslåVedtakBekreftelse.class);

        saksbehandler.ventTilAvsluttetBehandling();
        assertThat(saksbehandler.valgtBehandling.hentBehandlingsresultat())
                .as("Behandlingsresultat")
                .isEqualTo(BehandlingResultatType.INNSYN_AVVIST);
        assertThat(saksbehandler.harHistorikkinnslagForBehandling(HistorikkinnslagType.BREV_BESTILT))
                .as("Historikkinnslag (Brev er ikke bestilt etter innsyn er godkjent)")
                .isTrue();
    }
}
