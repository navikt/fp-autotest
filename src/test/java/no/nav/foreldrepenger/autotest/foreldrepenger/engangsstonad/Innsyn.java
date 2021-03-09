package no.nav.foreldrepenger.autotest.foreldrepenger.engangsstonad;

import static no.nav.foreldrepenger.autotest.erketyper.SøknadEngangstønadErketyper.lagEngangstønadFødsel;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import io.qameta.allure.Description;
import no.nav.foreldrepenger.autotest.base.FpsakTestBase;
import no.nav.foreldrepenger.autotest.dokumentgenerator.foreldrepengesoknad.SøkersRolle;
import no.nav.foreldrepenger.autotest.dokumentgenerator.foreldrepengesoknad.builders.EngangstønadBuilder;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.BehandlingResultatType;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.InnsynResultatType;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.ForeslåVedtakBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.VurderingAvInnsynBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.historikk.dto.HistorikkinnslagType;
import no.nav.foreldrepenger.autotest.util.AllureHelper;
import no.nav.foreldrepenger.vtp.kontrakter.TestscenarioDto;
import no.nav.foreldrepenger.vtp.testmodell.dokument.modell.koder.DokumenttypeId;

@Execution(ExecutionMode.CONCURRENT)
@Tag("fpsak")
@Tag("engangsstonad")
class Innsyn extends FpsakTestBase {

    @Test
    @DisplayName("Behandle innsyn for mor - godkjent")
    @Description("Behandle innsyn for mor - godkjent happy case")
    void behandleInnsynMorGodkjent() {
        TestscenarioDto testscenario = opprettTestscenario("50");
        EngangstønadBuilder søknad = lagEngangstønadFødsel(
                testscenario.personopplysninger().søkerAktørIdent(),
                SøkersRolle.MOR,
                testscenario.personopplysninger().fødselsdato());

        long saksnummer = fordel.sendInnSøknad(søknad.build(), testscenario,
                DokumenttypeId.SØKNAD_ENGANGSSTØNAD_FØDSEL);

        saksbehandler.hentFagsak(saksnummer);
        saksbehandler.ventTilAvsluttetBehandling();
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
        TestscenarioDto testscenario = opprettTestscenario("50");
        EngangstønadBuilder søknad = lagEngangstønadFødsel(
                testscenario.personopplysninger().søkerAktørIdent(),
                SøkersRolle.MOR,
                testscenario.personopplysninger().fødselsdato());

        long saksnummer = fordel.sendInnSøknad(søknad.build(), testscenario,
                DokumenttypeId.SØKNAD_ENGANGSSTØNAD_FØDSEL);

        saksbehandler.hentFagsak(saksnummer);
        saksbehandler.ventTilAvsluttetBehandling();
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

        saksbehandler.ventTilAvsluttetBehandling();
        assertThat(saksbehandler.valgtBehandling.hentBehandlingsresultat())
                .as("Behandlingsresultat")
                .isEqualTo(BehandlingResultatType.INNSYN_AVVIST);
        assertThat(saksbehandler.harHistorikkinnslagForBehandling(HistorikkinnslagType.BREV_BESTILT))
                .as("Historikkinnslag (Brev er ikke bestilt etter innsyn er godkjent)")
                .isTrue();
    }
}
