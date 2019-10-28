package no.nav.foreldrepenger.autotest.foreldrepenger.engangsstonad;

import io.qameta.allure.Description;
import no.nav.foreldrepenger.autotest.aktoerer.Aktoer.Rolle;
import no.nav.foreldrepenger.autotest.base.EngangsstonadTestBase;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.FatterVedtakBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.ForesloVedtakBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.VurderManglendeFodselBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.VurderSoknadsfristBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.avklarfakta.AvklarFaktaTillegsopplysningerBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.AksjonspunktKoder;
import no.nav.foreldrepenger.autotest.klienter.fpsak.kodeverk.dto.Kode;
import no.nav.foreldrepenger.vtp.dokumentgenerator.foreldrepengesoknad.SøkersRolle;
import no.nav.foreldrepenger.vtp.dokumentgenerator.foreldrepengesoknad.builders.SøknadBuilder;
import no.nav.foreldrepenger.vtp.dokumentgenerator.foreldrepengesoknad.builders.ytelse.EngangstønadYtelseBuilder;
import no.nav.foreldrepenger.vtp.dokumentgenerator.foreldrepengesoknad.erketyper.SoekersRelasjonErketyper;
import no.nav.foreldrepenger.vtp.kontrakter.TestscenarioDto;
import no.nav.foreldrepenger.vtp.testmodell.dokument.modell.koder.DokumenttypeId;
import no.nav.vedtak.felles.xml.soeknad.engangsstoenad.v3.Engangsstønad;
import no.nav.vedtak.felles.xml.soeknad.felles.v3.SoekersRelasjonTilBarnet;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import java.time.LocalDate;

@Execution(ExecutionMode.CONCURRENT)
@Tag("foreldrepenger")
public class Soknadsfrist extends EngangsstonadTestBase {


    @Test
    @Disabled
    @DisplayName("Mor søker for sent men får godkjent")
    @Description("Mor søker for sent men får godkjent alikevel")
    public void behandleFødselEngangstønadSøknadsfristGodkjent() throws Exception {
        TestscenarioDto testscenario = opprettTestscenarioFraVTPTemplate("52");
        String aktørID = testscenario.getPersonopplysninger().getSøkerAktørIdent();
        LocalDate fødselsdato = LocalDate.now().minusMonths(7);
        SoekersRelasjonTilBarnet relasjonTilBarnet = SoekersRelasjonErketyper.fødsel(1, fødselsdato);
        Engangsstønad engangsstønad = new EngangstønadYtelseBuilder(relasjonTilBarnet).build();
        SøknadBuilder søknad = new SøknadBuilder(engangsstønad, aktørID, SøkersRolle.MOR);

        fordel.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        long saksnummer = fordel.sendInnSøknad(søknad.build(), testscenario, DokumenttypeId.FOEDSELSSOKNAD_ENGANGSSTONAD);

        saksbehandler.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        saksbehandler.hentFagsak(saksnummer);
        saksbehandler.bekreftAksjonspunktBekreftelse(AvklarFaktaTillegsopplysningerBekreftelse.class);

        saksbehandler.hentAksjonspunktbekreftelse(VurderManglendeFodselBekreftelse.class)
                .bekreftDokumentasjonForeligger(1, LocalDate.now().minusMonths(7));
        saksbehandler.bekreftAksjonspunktBekreftelse(VurderManglendeFodselBekreftelse.class);

        //Får rtesultat utvandret?

        /*
        saksbehandler.hentAksjonspunktbekreftelse(AvklarBrukerHarGyldigPeriodeBekreftelse.class)
            .setVurdering(hentKodeverk().MedlemskapManuellVurderingType.getKode("MEDLEM"));
        saksbehandler.bekreftAksjonspunktBekreftelse(AvklarBrukerHarGyldigPeriodeBekreftelse.class);
        */
    }

    @Test
    @DisplayName("Behandle søknadsfrist og sent tilbake")
    @Description("Behandle søknadsfrist og sent tilbake på grunn av søknadsfrist")
    public void behandleSøknadsfristOgSentTilbakePåGrunnAvSøknadsfrist() throws Exception {
        TestscenarioDto testscenario = opprettTestscenarioFraVTPTemplate("55");
        String aktørID = testscenario.getPersonopplysninger().getSøkerAktørIdent();
        LocalDate fødselsdato = LocalDate.now().minusMonths(7);
        SoekersRelasjonTilBarnet relasjonTilBarnet = SoekersRelasjonErketyper.fødsel(1, fødselsdato);
        Engangsstønad engangsstønad = new EngangstønadYtelseBuilder(relasjonTilBarnet).build();
        SøknadBuilder søknad = new SøknadBuilder(engangsstønad, aktørID, SøkersRolle.MOR);

        fordel.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        long saksnummer = fordel.sendInnSøknad(søknad.build(), testscenario, DokumenttypeId.FOEDSELSSOKNAD_ENGANGSSTONAD);

        saksbehandler.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        saksbehandler.hentFagsak(saksnummer);
        saksbehandler.bekreftAksjonspunktBekreftelse(AvklarFaktaTillegsopplysningerBekreftelse.class);

        saksbehandler.hentAksjonspunktbekreftelse(VurderManglendeFodselBekreftelse.class)
            .bekreftDokumentasjonForeligger(1, LocalDate.now().minusMonths(7));
        saksbehandler.bekreftAksjonspunktBekreftelse(VurderManglendeFodselBekreftelse.class);

        saksbehandler.hentAksjonspunktbekreftelse(VurderSoknadsfristBekreftelse.class)
            .bekreftVilkårErOk();
        saksbehandler.bekreftAksjonspunktBekreftelse(VurderSoknadsfristBekreftelse.class);


        saksbehandler.bekreftAksjonspunktBekreftelse(ForesloVedtakBekreftelse.class);

        beslutter.erLoggetInnMedRolle(Rolle.BESLUTTER);
        beslutter.hentFagsak(saksnummer);


        beslutter.hentAksjonspunktbekreftelse(FatterVedtakBekreftelse.class)
                .godkjennAksjonspunkt(saksbehandler.hentAksjonspunkt(AksjonspunktKoder.SJEKK_MANGLENDE_FØDSEL))
                .avvisAksjonspunkt(saksbehandler.hentAksjonspunkt(AksjonspunktKoder.MANUELL_VURDERING_AV_SØKNADSFRISTVILKÅRET), new Kode("FEIL_FAKTA"));
        beslutter.bekreftAksjonspunktBekreftelse(FatterVedtakBekreftelse.class);

        saksbehandler.hentFagsak(saksnummer);
        verifiserLikhet(saksbehandler.hentAksjonspunkt(AksjonspunktKoder.SJEKK_MANGLENDE_FØDSEL).getStatus().kode, "UTFO");
        verifiserLikhet(saksbehandler.hentAksjonspunkt(AksjonspunktKoder.MANUELL_VURDERING_AV_SØKNADSFRISTVILKÅRET).getStatus().kode, "OPPR");
    }

    @Test
    @DisplayName("Behandle søknadsfrist og sent tilbake på grunn av fødsel")
    @Description("Behandle søknadsfrist og sent tilbake på grunn av fødsel - tester tilbakesending")
    public void behandleSøknadsfristOgSentTilbakePåGrunnAvFodsel() throws Exception {
        TestscenarioDto testscenario = opprettTestscenarioFraVTPTemplate("55");
        String aktørID = testscenario.getPersonopplysninger().getSøkerAktørIdent();
        LocalDate fødselsdato = LocalDate.now().minusMonths(7);
        SoekersRelasjonTilBarnet relasjonTilBarnet = SoekersRelasjonErketyper.fødsel(1, fødselsdato);
        Engangsstønad engangsstønad = new EngangstønadYtelseBuilder(relasjonTilBarnet).build();
        SøknadBuilder søknad = new SøknadBuilder(engangsstønad, aktørID, SøkersRolle.MOR);

        fordel.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        long saksnummer = fordel.sendInnSøknad(søknad.build(), testscenario, DokumenttypeId.FOEDSELSSOKNAD_ENGANGSSTONAD);

        saksbehandler.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        saksbehandler.hentFagsak(saksnummer);
        saksbehandler.bekreftAksjonspunktBekreftelse(AvklarFaktaTillegsopplysningerBekreftelse.class);

        saksbehandler.hentAksjonspunktbekreftelse(VurderManglendeFodselBekreftelse.class)
            .bekreftDokumentasjonForeligger(1, LocalDate.now().minusMonths(7));
        saksbehandler.bekreftAksjonspunktBekreftelse(VurderManglendeFodselBekreftelse.class);

        saksbehandler.hentAksjonspunktbekreftelse(VurderSoknadsfristBekreftelse.class)
            .bekreftVilkårErOk();
        saksbehandler.bekreftAksjonspunktBekreftelse(VurderSoknadsfristBekreftelse.class);


        saksbehandler.bekreftAksjonspunktBekreftelse(ForesloVedtakBekreftelse.class);

        beslutter.erLoggetInnMedRolle(Rolle.BESLUTTER);
        beslutter.hentFagsak(saksnummer);


        beslutter.hentAksjonspunktbekreftelse(FatterVedtakBekreftelse.class)
                .godkjennAksjonspunkt(saksbehandler.hentAksjonspunkt(AksjonspunktKoder.MANUELL_VURDERING_AV_SØKNADSFRISTVILKÅRET))
                .avvisAksjonspunkt(saksbehandler.hentAksjonspunkt(AksjonspunktKoder.SJEKK_MANGLENDE_FØDSEL), new Kode("FEIL_FAKTA"));
        beslutter.bekreftAksjonspunktBekreftelse(FatterVedtakBekreftelse.class);

        saksbehandler.hentFagsak(saksnummer);
        verifiserLikhet(saksbehandler.hentAksjonspunkt(AksjonspunktKoder.SJEKK_MANGLENDE_FØDSEL).getStatus().kode, "OPPR");
        verifiser(!saksbehandler.harAksjonspunkt(AksjonspunktKoder.MANUELL_VURDERING_AV_SØKNADSFRISTVILKÅRET), "Behandling hadde aksjonspunkt " + AksjonspunktKoder.MANUELL_VURDERING_AV_SØKNADSFRISTVILKÅRET + " selv om det ikke var forventet");
    }

}
