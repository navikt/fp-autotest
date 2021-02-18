package no.nav.foreldrepenger.autotest.foreldrepenger.engangsstonad;

import static no.nav.foreldrepenger.autotest.erketyper.SøknadEngangstønadErketyper.lagEngangstønadFødsel;

import java.time.LocalDate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import io.qameta.allure.Description;
import no.nav.foreldrepenger.autotest.aktoerer.Aktoer.Rolle;
import no.nav.foreldrepenger.autotest.base.FpsakTestBase;
import no.nav.foreldrepenger.autotest.dokumentgenerator.foreldrepengesoknad.SøkersRolle;
import no.nav.foreldrepenger.autotest.dokumentgenerator.foreldrepengesoknad.builders.EngangstønadBuilder;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.ForeslåVedtakBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.VurderingAvInnsynBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.historikk.dto.HistorikkInnslag;
import no.nav.foreldrepenger.autotest.util.AllureHelper;
import no.nav.foreldrepenger.vtp.kontrakter.TestscenarioDto;
import no.nav.foreldrepenger.vtp.testmodell.dokument.modell.koder.DokumenttypeId;

@Execution(ExecutionMode.CONCURRENT)
@Tag("fpsak")
@Tag("engangsstonad")
public class Innsyn extends FpsakTestBase {

    @Test
    @DisplayName("Behandle innsyn for mor - godkjent")
    @Description("Behandle innsyn for mor - godkjent happy case")
    public void behandleInnsynMorGodkjent() {
        TestscenarioDto testscenario = opprettTestscenario("50");
        EngangstønadBuilder søknad = lagEngangstønadFødsel(
                testscenario.personopplysninger().søkerAktørIdent(),
                SøkersRolle.MOR,
                testscenario.personopplysninger().fødselsdato());

        fordel.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        long saksnummer = fordel.sendInnSøknad(søknad.build(), testscenario,
                DokumenttypeId.SØKNAD_ENGANGSSTØNAD_FØDSEL);

        saksbehandler.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        saksbehandler.hentFagsak(saksnummer);
        saksbehandler.ventTilAvsluttetBehandling();
        verifiserLikhet(saksbehandler.valgtBehandling.hentBehandlingsresultat(), "INNVILGET");

        saksbehandler.oprettBehandlingInnsyn(null);
        saksbehandler.ventPåOgVelgDokumentInnsynBehandling();

        VurderingAvInnsynBekreftelse vurderingAvInnsynBekreftelse = saksbehandler
                .hentAksjonspunktbekreftelse(VurderingAvInnsynBekreftelse.class);
        vurderingAvInnsynBekreftelse.setMottattDato(LocalDate.now())
                .setMottattDato(LocalDate.now())
                .setInnsynResultatType(saksbehandler.kodeverk.InnsynResultatType.getKode("INNV"))
                .skalSetteSakPåVent(false)
                .setBegrunnelse("Test");
        saksbehandler.bekreftAksjonspunkt(vurderingAvInnsynBekreftelse);

        saksbehandler.bekreftAksjonspunktMedDefaultVerdier(ForeslåVedtakBekreftelse.class);

        saksbehandler.ventTilAvsluttetBehandling();
        AllureHelper.debugLoggBehandlingsliste(saksbehandler.behandlinger);
        AllureHelper.debugLoggHistorikkinnslag(saksbehandler.getHistorikkInnslag());
        verifiserLikhet(saksbehandler.valgtBehandling.hentBehandlingsresultat(), "INNSYN_INNVILGET");
        verifiser(saksbehandler.harHistorikkinnslagForBehandling(HistorikkInnslag.BREV_BESTILT),
                "Brev er ikke bestilt etter innsyn er godkjent");
    }

    @Test
    @DisplayName("Behandle innsyn for mor - avvist")
    @Description("Behandle innsyn for mor - avvist ved vurdering")
    public void behandleInnsynMorAvvist() {
        TestscenarioDto testscenario = opprettTestscenario("50");
        EngangstønadBuilder søknad = lagEngangstønadFødsel(
                testscenario.personopplysninger().søkerAktørIdent(),
                SøkersRolle.MOR,
                testscenario.personopplysninger().fødselsdato());

        fordel.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        long saksnummer = fordel.sendInnSøknad(søknad.build(), testscenario,
                DokumenttypeId.SØKNAD_ENGANGSSTØNAD_FØDSEL);

        saksbehandler.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        saksbehandler.hentFagsak(saksnummer);
        saksbehandler.ventTilAvsluttetBehandling();
        verifiserLikhet(saksbehandler.valgtBehandling.hentBehandlingsresultat(), "INNVILGET");

        saksbehandler.oprettBehandlingInnsyn(null);
        saksbehandler.ventPåOgVelgDokumentInnsynBehandling();

        VurderingAvInnsynBekreftelse vurderingAvInnsynBekreftelse = saksbehandler
                .hentAksjonspunktbekreftelse(VurderingAvInnsynBekreftelse.class);
        vurderingAvInnsynBekreftelse.setMottattDato(LocalDate.now())
                .setInnsynResultatType(saksbehandler.kodeverk.InnsynResultatType.getKode("AVVIST"))
                .setBegrunnelse("Test");
        saksbehandler.bekreftAksjonspunkt(vurderingAvInnsynBekreftelse);

        saksbehandler.bekreftAksjonspunktMedDefaultVerdier(ForeslåVedtakBekreftelse.class);

        saksbehandler.ventTilAvsluttetBehandling();
        verifiserLikhet(saksbehandler.valgtBehandling.hentBehandlingsresultat(), "INNSYN_AVVIST",
                "Behandlingstatus");
        verifiser(saksbehandler.harHistorikkinnslagForBehandling(HistorikkInnslag.BREV_BESTILT),
                "Brev er ikke bestilt etter innsyn er godkjent");
    }
}
