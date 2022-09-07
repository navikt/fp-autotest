package no.nav.foreldrepenger.autotest.fpsak.engangsstonad;

import static no.nav.foreldrepenger.autotest.aktoerer.innsender.InnsenderType.SEND_DOKUMENTER_UTEN_SELVBETJENING;
import static no.nav.foreldrepenger.autotest.dokumentgenerator.foreldrepengesoknad.json.erketyper.SøknadEngangsstønadErketyper.lagEngangstønadFødsel;
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
import no.nav.foreldrepenger.autotest.klienter.fpsak.historikk.dto.HistorikkInnslag;
import no.nav.foreldrepenger.autotest.klienter.fpsak.historikk.dto.HistorikkinnslagType;
import no.nav.foreldrepenger.autotest.util.AllureHelper;
import no.nav.foreldrepenger.autotest.util.testscenario.modell.Familie;
import no.nav.foreldrepenger.common.domain.BrukerRolle;

@Tag("fpsak")
@Tag("engangsstonad")
class Innsyn extends FpsakTestBase {

    @Test
    @DisplayName("Behandle innsyn for mor - godkjent")
    @Description("Behandle innsyn for mor - godkjent happy case")
    void behandleInnsynMorGodkjent() {
        var familie = new Familie("50", SEND_DOKUMENTER_UTEN_SELVBETJENING);
        var mor = familie.mor();
        var fødselsdato = familie.barn().fødselsdato();
        var søknad = lagEngangstønadFødsel(BrukerRolle.MOR,
                fødselsdato);
        var saksnummer = mor.søk(søknad.build());

        saksbehandler.hentFagsak(saksnummer);
        saksbehandler.ventTilAvsluttetBehandlingOgFagsakLøpendeEllerAvsluttet();
        assertThat(saksbehandler.valgtBehandling.hentBehandlingsresultat())
                .as("Behandlingsresultat")
                .isEqualTo(BehandlingResultatType.INNVILGET);

        saksbehandler.oprettBehandlingInnsyn(null);
        saksbehandler.ventPåOgVelgDokumentInnsynBehandling();

        VurderingAvInnsynBekreftelse vurderingAvInnsynBekreftelse = saksbehandler
                .hentAksjonspunktbekreftelse(VurderingAvInnsynBekreftelse.class);
        vurderingAvInnsynBekreftelse.setMottattDato(LocalDate.now())
                .setMottattDato(LocalDate.now())
                .setInnsynResultatType(InnsynResultatType.INNVILGET)
                .skalSetteSakPåVent(false)
                .setBegrunnelse("Test");
        saksbehandler.bekreftAksjonspunkt(vurderingAvInnsynBekreftelse);

        saksbehandler.bekreftAksjonspunktMedDefaultVerdier(ForeslåVedtakBekreftelse.class);

        saksbehandler.ventTilAvsluttetBehandlingOgFagsakLøpendeEllerAvsluttet();
        AllureHelper.debugLoggBehandlingsliste(saksbehandler.behandlinger);
        AllureHelper.debugLoggHistorikkinnslag(saksbehandler.hentHistorikkinnslagPåBehandling());
        assertThat(saksbehandler.valgtBehandling.hentBehandlingsresultat())
                .as("Behandlingsresultat")
                .isEqualTo(BehandlingResultatType.INNSYN_INNVILGET);
        assertThat(saksbehandler.hentHistorikkinnslagPåBehandling())
                .as("Historikkinnslag")
                .extracting(HistorikkInnslag::type)
                .contains(HistorikkinnslagType.BREV_BESTILT);
    }

    @Test
    @DisplayName("Behandle innsyn for mor - avvist")
    @Description("Behandle innsyn for mor - avvist ved vurdering")
    void behandleInnsynMorAvvist() {
        var familie = new Familie("50", SEND_DOKUMENTER_UTEN_SELVBETJENING);
        var mor = familie.mor();
        var fødselsdato = familie.barn().fødselsdato();
        var søknad = lagEngangstønadFødsel(BrukerRolle.MOR,
                fødselsdato);
        var saksnummer = mor.søk(søknad.build());

        saksbehandler.hentFagsak(saksnummer);
        saksbehandler.ventTilAvsluttetBehandlingOgFagsakLøpendeEllerAvsluttet();
        assertThat(saksbehandler.valgtBehandling.hentBehandlingsresultat())
                .as("Behandlingsresultat")
                .isEqualTo(BehandlingResultatType.INNVILGET);

        saksbehandler.oprettBehandlingInnsyn(null);
        saksbehandler.ventPåOgVelgDokumentInnsynBehandling();

        VurderingAvInnsynBekreftelse vurderingAvInnsynBekreftelse = saksbehandler
                .hentAksjonspunktbekreftelse(VurderingAvInnsynBekreftelse.class);
        vurderingAvInnsynBekreftelse.setMottattDato(LocalDate.now())
                .setInnsynResultatType(InnsynResultatType.AVVIST)
                .setBegrunnelse("Test");
        saksbehandler.bekreftAksjonspunkt(vurderingAvInnsynBekreftelse);

        saksbehandler.bekreftAksjonspunktMedDefaultVerdier(ForeslåVedtakBekreftelse.class);

        saksbehandler.ventTilAvsluttetBehandlingOgFagsakLøpendeEllerAvsluttet();
        assertThat(saksbehandler.valgtBehandling.hentBehandlingsresultat())
                .as("Behandlingsresultat")
                .isEqualTo(BehandlingResultatType.INNSYN_AVVIST);
        assertThat(saksbehandler.hentHistorikkinnslagPåBehandling())
                .as("Historikkinnslag (Brev er ikke bestilt etter innsyn er godkjent)")
                .extracting(HistorikkInnslag::type)
                .contains(HistorikkinnslagType.BREV_BESTILT);
    }
}
