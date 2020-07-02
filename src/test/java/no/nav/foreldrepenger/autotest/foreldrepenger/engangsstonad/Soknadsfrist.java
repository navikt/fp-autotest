package no.nav.foreldrepenger.autotest.foreldrepenger.engangsstonad;

import static no.nav.foreldrepenger.autotest.erketyper.SøknadEngangstønadErketyper.lagEngangstønadFødsel;

import java.time.LocalDate;

import org.junit.jupiter.api.Disabled;
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
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.FatterVedtakBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.ForeslåVedtakBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.VurderManglendeFodselBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.VurderSoknadsfristBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.AksjonspunktKoder;
import no.nav.foreldrepenger.autotest.klienter.fpsak.kodeverk.dto.Kode;
import no.nav.foreldrepenger.vtp.kontrakter.TestscenarioDto;
import no.nav.foreldrepenger.vtp.testmodell.dokument.modell.koder.DokumenttypeId;

@Execution(ExecutionMode.CONCURRENT)
@Tag("foreldrepenger")
public class Soknadsfrist extends FpsakTestBase {

    @Test
    @Disabled
    @DisplayName("Mor søker for sent men får godkjent")
    @Description("Mor søker for sent men får godkjent alikevel")
    public void behandleFødselEngangstønadSøknadsfristGodkjent() {
        TestscenarioDto testscenario = opprettTestscenarioFraVTPTemplate("52");
        String aktørID = testscenario.getPersonopplysninger().getSøkerAktørIdent();
        LocalDate fødselsdato = LocalDate.now().minusMonths(7);
        EngangstønadBuilder søknad = lagEngangstønadFødsel(aktørID, SøkersRolle.MOR, fødselsdato);

        fordel.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        long saksnummer = fordel.sendInnSøknad(søknad.build(), testscenario,
                DokumenttypeId.FOEDSELSSOKNAD_ENGANGSSTONAD);

        saksbehandler.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        saksbehandler.hentFagsak(saksnummer);
        var vurderManglendeFodselBekreftelse = saksbehandler
                .hentAksjonspunktbekreftelse(VurderManglendeFodselBekreftelse.class)
                .bekreftDokumentasjonForeligger(1, LocalDate.now().minusMonths(7));
        saksbehandler.bekreftAksjonspunkt(vurderManglendeFodselBekreftelse);

        // Får rtesultat utvandret?

        /*
         * saksbehandler.hentAksjonspunktbekreftelse(
         * AvklarBrukerHarGyldigPeriodeBekreftelse.class)
         * .setVurdering(hentKodeverk().MedlemskapManuellVurderingType.getKode("MEDLEM")
         * ); saksbehandler.bekreftAksjonspunktBekreftelse(
         * AvklarBrukerHarGyldigPeriodeBekreftelse.class);
         */
    }

    @Test
    @DisplayName("Behandle søknadsfrist og sent tilbake")
    @Description("Behandle søknadsfrist og sent tilbake på grunn av søknadsfrist")
    public void behandleSøknadsfristOgSentTilbakePåGrunnAvSøknadsfrist() {
        TestscenarioDto testscenario = opprettTestscenarioFraVTPTemplate("55");
        String aktørID = testscenario.getPersonopplysninger().getSøkerAktørIdent();
        LocalDate fødselsdato = LocalDate.now().minusMonths(7);
        EngangstønadBuilder søknad = lagEngangstønadFødsel(aktørID, SøkersRolle.MOR, fødselsdato);

        fordel.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        long saksnummer = fordel.sendInnSøknad(søknad.build(), testscenario,
                DokumenttypeId.FOEDSELSSOKNAD_ENGANGSSTONAD);

        saksbehandler.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        saksbehandler.hentFagsak(saksnummer);

        var vurderManglendeFodselBekreftelse = saksbehandler
                .hentAksjonspunktbekreftelse(VurderManglendeFodselBekreftelse.class)
                .bekreftDokumentasjonForeligger(1, LocalDate.now().minusMonths(7));
        saksbehandler.bekreftAksjonspunkt(vurderManglendeFodselBekreftelse);

        var vurderSoknadsfristBekreftelse = saksbehandler
                .hentAksjonspunktbekreftelse(VurderSoknadsfristBekreftelse.class)
                .bekreftVilkårErOk();
        saksbehandler.bekreftAksjonspunkt(vurderSoknadsfristBekreftelse);

        saksbehandler.bekreftAksjonspunktMedDefaultVerdier(ForeslåVedtakBekreftelse.class);

        beslutter.erLoggetInnMedRolle(Rolle.BESLUTTER);
        beslutter.hentFagsak(saksnummer);

        var fatterVedtakBekreftelse = beslutter.hentAksjonspunktbekreftelse(FatterVedtakBekreftelse.class)
                .godkjennAksjonspunkt(saksbehandler.hentAksjonspunkt(AksjonspunktKoder.SJEKK_MANGLENDE_FØDSEL))
                .avvisAksjonspunkt(
                        saksbehandler.hentAksjonspunkt(AksjonspunktKoder.MANUELL_VURDERING_AV_SØKNADSFRISTVILKÅRET),
                        new Kode("FEIL_FAKTA"));
        beslutter.bekreftAksjonspunkt(fatterVedtakBekreftelse);

        saksbehandler.hentFagsak(saksnummer);
        verifiserLikhet(saksbehandler.hentAksjonspunkt(AksjonspunktKoder.SJEKK_MANGLENDE_FØDSEL).getStatus().kode,
                "UTFO");
        verifiserLikhet(saksbehandler.hentAksjonspunkt(AksjonspunktKoder.MANUELL_VURDERING_AV_SØKNADSFRISTVILKÅRET)
                .getStatus().kode, "OPPR");
    }

    @Test
    @DisplayName("Behandle søknadsfrist og sent tilbake på grunn av fødsel")
    @Description("Behandle søknadsfrist og sent tilbake på grunn av fødsel - tester tilbakesending")
    public void behandleSøknadsfristOgSentTilbakePåGrunnAvFodsel() {
        TestscenarioDto testscenario = opprettTestscenarioFraVTPTemplate("55");
        String aktørID = testscenario.getPersonopplysninger().getSøkerAktørIdent();
        LocalDate fødselsdato = LocalDate.now().minusMonths(7);
        EngangstønadBuilder søknad = lagEngangstønadFødsel(aktørID, SøkersRolle.MOR, fødselsdato);

        fordel.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        long saksnummer = fordel.sendInnSøknad(søknad.build(), testscenario,
                DokumenttypeId.FOEDSELSSOKNAD_ENGANGSSTONAD);

        saksbehandler.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        saksbehandler.hentFagsak(saksnummer);
        var vurderManglendeFodselBekreftelse = saksbehandler
                .hentAksjonspunktbekreftelse(VurderManglendeFodselBekreftelse.class)
                .bekreftDokumentasjonForeligger(1, LocalDate.now().minusMonths(7));
        saksbehandler.bekreftAksjonspunkt(vurderManglendeFodselBekreftelse);

        saksbehandler.bekreftAksjonspunktMedDefaultVerdier(VurderSoknadsfristBekreftelse.class);
        saksbehandler.bekreftAksjonspunktMedDefaultVerdier(ForeslåVedtakBekreftelse.class);

        beslutter.erLoggetInnMedRolle(Rolle.BESLUTTER);
        beslutter.hentFagsak(saksnummer);
        var vedtakBekreftelse = beslutter.hentAksjonspunktbekreftelse(FatterVedtakBekreftelse.class)
                .godkjennAksjonspunkt(
                        saksbehandler.hentAksjonspunkt(AksjonspunktKoder.MANUELL_VURDERING_AV_SØKNADSFRISTVILKÅRET))
                .avvisAksjonspunkt(saksbehandler.hentAksjonspunkt(AksjonspunktKoder.SJEKK_MANGLENDE_FØDSEL),
                        new Kode("FEIL_FAKTA"));
        beslutter.bekreftAksjonspunkt(vedtakBekreftelse);

        saksbehandler.hentFagsak(saksnummer);
        verifiserLikhet(saksbehandler.hentAksjonspunkt(AksjonspunktKoder.SJEKK_MANGLENDE_FØDSEL).getStatus().kode,
                "OPPR");

        var harSøknadsfristAP = saksbehandler.valgtBehandling.getAksjonspunkter().stream()
                .anyMatch(ap -> ap.getDefinisjon().kode
                        .equals(AksjonspunktKoder.MANUELL_VURDERING_AV_SØKNADSFRISTVILKÅRET));
        verifiser(!harSøknadsfristAP, "Behandling hadde aksjonspunkt "
                + AksjonspunktKoder.MANUELL_VURDERING_AV_SØKNADSFRISTVILKÅRET + " selv om det ikke var forventet");
    }

}
