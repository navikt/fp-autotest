package no.nav.foreldrepenger.autotest.foreldrepenger.engangsstonad;

import io.qameta.allure.Description;
import no.nav.foreldrepenger.autotest.aktoerer.Aktoer.Rolle;
import no.nav.foreldrepenger.autotest.base.FpsakTestBase;
import no.nav.foreldrepenger.autotest.dokumentgenerator.foreldrepengesoknad.SøkersRolle;
import no.nav.foreldrepenger.autotest.dokumentgenerator.foreldrepengesoknad.builders.EngangstønadBuilder;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.AksjonspunktBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.ForesloVedtakBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.VurderingAvInnsynBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.avklarfakta.AvklarFaktaTillegsopplysningerBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.historikk.dto.HistorikkInnslag;
import no.nav.foreldrepenger.autotest.util.AllureHelper;
import no.nav.foreldrepenger.vtp.kontrakter.TestscenarioDto;
import no.nav.foreldrepenger.vtp.testmodell.dokument.modell.koder.DokumenttypeId;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import java.time.LocalDate;

import static no.nav.foreldrepenger.autotest.erketyper.SøknadEngangstønadErketyper.lagEngangstønadFødsel;
import static no.nav.foreldrepenger.autotest.erketyper.SøknadEngangstønadErketyper.lagEngangstønadTermin;

@Execution(ExecutionMode.CONCURRENT)
@Tag("fpsak")
@Tag("engangsstonad")
public class Innsyn extends FpsakTestBase {

    @Test
    @DisplayName("Behandle innsyn for mor - godkjent")
    @Description("Behandle innsyn for mor - godkjent happy case")
    public void behandleInnsynMorGodkjent() throws Exception {
        TestscenarioDto testscenario = opprettTestscenarioFraVTPTemplate("50");
        EngangstønadBuilder søknad = lagEngangstønadFødsel(
                testscenario.getPersonopplysninger().getSøkerAktørIdent(),
                SøkersRolle.MOR,
                testscenario.getPersonopplysninger().getFødselsdato());

        fordel.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        long saksnummer = fordel.sendInnSøknad(søknad.build(), testscenario, DokumenttypeId.FOEDSELSSOKNAD_ENGANGSSTONAD);

        saksbehandler.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        saksbehandler.hentFagsak(saksnummer);
        saksbehandler.bekreftAksjonspunktMedDefaultVerdier(AvklarFaktaTillegsopplysningerBekreftelse.class);

        saksbehandler.oprettBehandlingInnsyn(null);
        saksbehandler.velgDokumentInnsynBehandling();

        AksjonspunktBekreftelse aksjonspunktBekreftelse = saksbehandler.hentAksjonspunktbekreftelse(VurderingAvInnsynBekreftelse.class)
                .setMottattDato(LocalDate.now())
                .setInnsynResultatType(saksbehandler.kodeverk.InnsynResultatType.getKode("INNV"))
                .skalSetteSakPåVent(false)
                .setBegrunnelse("Test");
        saksbehandler.bekreftAksjonspunkt(aksjonspunktBekreftelse);

        saksbehandler.bekreftAksjonspunktMedDefaultVerdier(ForesloVedtakBekreftelse.class);

        saksbehandler.ventTilBehandlingsstatus("AVSLU");
        AllureHelper.debugLoggBehandlingsliste(saksbehandler.behandlinger);
        AllureHelper.debugLoggHistorikkinnslag(saksbehandler.getHistorikkInnslag());
        verifiserLikhet(saksbehandler.valgtBehandling.behandlingsresultat.toString(), "INNSYN_INNVILGET", "Behandlingstatus");
        verifiser(saksbehandler.harHistorikkinnslag(HistorikkInnslag.BREV_BESTILT), "Brev er ikke bestilt etter innsyn er godkjent");
        //TODO: Fjernet vent på brev sendt - bytte med annen assertion
    }

    @Test
    @DisplayName("Behandle innsyn for mor - avvist")
    @Description("Behandle innsyn for mor - avvist ved vurdering")
    public void behandleInnsynMorAvvist() throws Exception {
        TestscenarioDto testscenario = opprettTestscenarioFraVTPTemplate("50");
        EngangstønadBuilder søknad = lagEngangstønadFødsel(
                testscenario.getPersonopplysninger().getSøkerAktørIdent(),
                SøkersRolle.MOR,
                testscenario.getPersonopplysninger().getFødselsdato());

        fordel.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        long saksnummer = fordel.sendInnSøknad(søknad.build(), testscenario, DokumenttypeId.FOEDSELSSOKNAD_ENGANGSSTONAD);

        saksbehandler.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        saksbehandler.hentFagsak(saksnummer);
        saksbehandler.bekreftAksjonspunktMedDefaultVerdier(AvklarFaktaTillegsopplysningerBekreftelse.class);

        saksbehandler.oprettBehandlingInnsyn(null);
        saksbehandler.velgDokumentInnsynBehandling();

        VurderingAvInnsynBekreftelse vurderingAvInnsynBekreftelse = saksbehandler.hentAksjonspunktbekreftelse(VurderingAvInnsynBekreftelse.class);
        vurderingAvInnsynBekreftelse.setMottattDato(LocalDate.now())
                .setInnsynResultatType(saksbehandler.kodeverk.InnsynResultatType.getKode("AVVIST"))
                .setBegrunnelse("Test");
        saksbehandler.bekreftAksjonspunkt(vurderingAvInnsynBekreftelse);

        saksbehandler.bekreftAksjonspunktMedDefaultVerdier(ForesloVedtakBekreftelse.class);

        saksbehandler.ventTilBehandlingsstatus("AVSLU");
        saksbehandler.ventTilHistorikkinnslag(HistorikkInnslag.BREV_BESTILT);
        verifiserLikhet(saksbehandler.valgtBehandling.behandlingsresultat.toString(), "INNSYN_AVVIST", "Behandlingstatus");
        verifiser(saksbehandler.harHistorikkinnslag(HistorikkInnslag.BREV_BESTILT), "Brev er ikke bestilt etter innsyn er godkjent");
        //TODO: Fjernet vent på brev sendt - bytte med annen assertion
    }
    @Disabled //Disabled til Kafka støtte for brev er i VTP
    @Test
    @DisplayName("Behandle innsyn for far - avvist")
    @Description("Behandle innsyn for far - avvist ved vurdering")
    public void behandleInnsynFarAvvist() throws Exception {
        TestscenarioDto testscenario = opprettTestscenarioFraVTPTemplate("61");
        EngangstønadBuilder søknad = lagEngangstønadTermin(
                testscenario.getPersonopplysninger().getSøkerAktørIdent(),
                SøkersRolle.FAR,
                LocalDate.now().plusWeeks(3));

        fordel.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        long saksnummer = fordel.sendInnSøknad(søknad.build(), testscenario, DokumenttypeId.FOEDSELSSOKNAD_ENGANGSSTONAD);

        saksbehandler.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        saksbehandler.hentFagsak(saksnummer);
        saksbehandler.bekreftAksjonspunktMedDefaultVerdier(AvklarFaktaTillegsopplysningerBekreftelse.class);

        saksbehandler.oprettBehandlingInnsyn(null);
        saksbehandler.velgDokumentInnsynBehandling();

        saksbehandler.hentAksjonspunktbekreftelse(VurderingAvInnsynBekreftelse.class)
                .setMottattDato(LocalDate.now())
                .setInnsynResultatType(saksbehandler.kodeverk.InnsynResultatType.getKode("AVVIST"))
                .setBegrunnelse("Test");
        saksbehandler.bekreftAksjonspunktMedDefaultVerdier(VurderingAvInnsynBekreftelse.class);

        saksbehandler.bekreftAksjonspunktMedDefaultVerdier(ForesloVedtakBekreftelse.class);

        saksbehandler.ventTilBehandlingsstatus("AVSLU");
        verifiserLikhet(saksbehandler.valgtBehandling.behandlingsresultat.toString(), "INNSYN_AVVIST", "Behandlingstatus");
        verifiser(saksbehandler.harHistorikkinnslag(HistorikkInnslag.BREV_BESTILT), "Brev er ikke bestilt etter innsyn er godkjent");
        //TODO: Fjernet vent på brev sendt - bytte med annen assertion
    }
}
