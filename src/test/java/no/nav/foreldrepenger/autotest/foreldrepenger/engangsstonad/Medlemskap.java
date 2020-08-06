package no.nav.foreldrepenger.autotest.foreldrepenger.engangsstonad;

import static no.nav.foreldrepenger.autotest.erketyper.SøknadEngangstønadErketyper.lagEngangstønadFødsel;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.qameta.allure.Description;
import no.nav.foreldrepenger.autotest.aktoerer.Aktoer.Rolle;
import no.nav.foreldrepenger.autotest.base.FpsakTestBase;
import no.nav.foreldrepenger.autotest.dokumentgenerator.foreldrepengesoknad.SøkersRolle;
import no.nav.foreldrepenger.autotest.dokumentgenerator.foreldrepengesoknad.builders.EngangstønadBuilder;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.FatterVedtakBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.ForeslåVedtakBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.avklarfakta.AvklarBrukerHarGyldigPeriodeBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.avklarfakta.AvklarFaktaPersonstatusBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.overstyr.OverstyrMedlemskapsvilkaaret;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.AksjonspunktKoder;
import no.nav.foreldrepenger.vtp.kontrakter.TestscenarioDto;
import no.nav.foreldrepenger.vtp.testmodell.dokument.modell.koder.DokumenttypeId;

@Execution(ExecutionMode.CONCURRENT)
@Tag("fpsak")
@Tag("engangsstonad")
public class Medlemskap extends FpsakTestBase {

    private static final Logger logger = LoggerFactory.getLogger(Medlemskap.class);

    @Test
    @DisplayName("Mor søker fødsel er utvandret")
    @Description("Mor søker fødsel og er utvandret. Skal føre til aksjonspunkt angående medlemskap - avslått")
    public void morSøkerFødselErUtvandret() {
        TestscenarioDto testscenario = opprettTestscenario("51");

        EngangstønadBuilder søknad = lagEngangstønadFødsel(
                testscenario.getPersonopplysninger().getSøkerAktørIdent(),
                SøkersRolle.MOR,
                testscenario.getPersonopplysninger().getFødselsdato());

        fordel.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        long saksnummer = fordel.sendInnSøknad(søknad.build(), testscenario,
                DokumenttypeId.FOEDSELSSOKNAD_ENGANGSSTONAD);
        logger.info("Opprettet sak med saksnummer: {}", saksnummer);

        saksbehandler.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        saksbehandler.hentFagsak(saksnummer);
        var ab = saksbehandler.hentAksjonspunktbekreftelse(AvklarBrukerHarGyldigPeriodeBekreftelse.class)
                .setVurdering(hentKodeverk().MedlemskapManuellVurderingType.getKode("MEDLEM"),
                        saksbehandler.valgtBehandling.getMedlem().getMedlemskapPerioder());
        saksbehandler.bekreftAksjonspunkt(ab);

        overstyrer.erLoggetInnMedRolle(Rolle.OVERSTYRER);
        overstyrer.hentFagsak(saksnummer);

        OverstyrMedlemskapsvilkaaret overstyr = new OverstyrMedlemskapsvilkaaret();
        overstyr.avvis(overstyrer.kodeverk.Avslagsårsak.get("FP_VK_2").getKode("1020" /* Søker er ikke medlem" */));
        overstyr.setBegrunnelse("avvist");
        overstyrer.overstyr(overstyr);

        verifiserLikhet(overstyrer.valgtBehandling.behandlingsresultat.toString(), "AVSLÅTT", "Behandlingstatus");
        overstyrer.bekreftAksjonspunktMedDefaultVerdier(ForeslåVedtakBekreftelse.class);

        beslutter.erLoggetInnMedRolle(Rolle.BESLUTTER);
        beslutter.hentFagsak(saksnummer);

        var fatterVedtakBekreftelse = beslutter.hentAksjonspunktbekreftelse(FatterVedtakBekreftelse.class)
                .godkjennAksjonspunkt(beslutter.hentAksjonspunkt(AksjonspunktKoder.OVERSTYRING_AV_MEDLEMSKAPSVILKÅRET));
        beslutter.bekreftAksjonspunkt(fatterVedtakBekreftelse);

        verifiserLikhet(beslutter.valgtBehandling.behandlingsresultat.toString(), "AVSLÅTT", "Behandlingstatus");
    }

    @Test
    @DisplayName("Mor søker med personstatus uregistrert")
    @Description("Mor søker med personstatus uregistrert, får askjonspunkt så hennlegges")
    public void morSøkerFødselUregistrert() {
        TestscenarioDto testscenario = opprettTestscenario("120");
        EngangstønadBuilder søknad = lagEngangstønadFødsel(
                testscenario.getPersonopplysninger().getSøkerAktørIdent(),
                SøkersRolle.MOR,
                testscenario.getPersonopplysninger().getFødselsdato());
        fordel.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        long saksnummer = fordel.sendInnSøknad(søknad.build(), testscenario,
                DokumenttypeId.FOEDSELSSOKNAD_ENGANGSSTONAD);

        saksbehandler.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        saksbehandler.hentFagsak(saksnummer);
        var avklarFaktaPersonstatusBekreftelse = saksbehandler
                .hentAksjonspunktbekreftelse(AvklarFaktaPersonstatusBekreftelse.class)
                .bekreftHenleggBehandling();
        saksbehandler.bekreftAksjonspunkt(avklarFaktaPersonstatusBekreftelse);

        verifiser(saksbehandler.valgtBehandling.erHenlagt(),
                "Behandlingen ble ikke henlagt etter bekreftet ugyldig status");
    }

    @Test
    @DisplayName("Mor søker med utenlandsk adresse og ingen registert inntekt")
    @Description("Mor søker med utelandsk adresse og ingen registret inntekt")
    public void morSøkerFødselUtenlandsadresse() {
        TestscenarioDto testscenario = opprettTestscenario("121");
        EngangstønadBuilder søknad = lagEngangstønadFødsel(
                testscenario.getPersonopplysninger().getSøkerAktørIdent(),
                SøkersRolle.MOR,
                testscenario.getPersonopplysninger().getFødselsdato());
        fordel.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        long saksnummer = fordel.sendInnSøknad(søknad.build(), testscenario,
                DokumenttypeId.FOEDSELSSOKNAD_ENGANGSSTONAD);

        saksbehandler.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        saksbehandler.hentFagsak(saksnummer);
        saksbehandler.ventTilAvsluttetBehandling();
        verifiserLikhet(saksbehandler.valgtBehandling.hentBehandlingsresultat(), "INNVILGET", "Behandlingstatus");
    }
}
