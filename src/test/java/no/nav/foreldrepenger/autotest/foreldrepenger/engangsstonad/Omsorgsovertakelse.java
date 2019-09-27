package no.nav.foreldrepenger.autotest.foreldrepenger.engangsstonad;

import io.qameta.allure.Description;
import no.nav.foreldrepenger.autotest.aktoerer.Aktoer.Rolle;
import no.nav.foreldrepenger.autotest.base.EngangsstonadTestBase;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.FatterVedtakBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.ForesloVedtakBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.VurderingAvForeldreansvarAndreLedd;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.VurderingAvOmsorgsvilkoret;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.avklarfakta.AvklarFaktaOmsorgOgForeldreansvarBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.avklarfakta.AvklarFaktaTillegsopplysningerBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.AksjonspunktKoder;
import no.nav.foreldrepenger.autotest.klienter.fpsak.historikk.dto.HistorikkInnslag;
import no.nav.foreldrepenger.vtp.dokumentgenerator.foreldrepengesoknad.OmsorgsovertakelseÅrsak;
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

@Execution(ExecutionMode.CONCURRENT)
@Tag("fpsak")
@Tag("engangsstonad")
public class Omsorgsovertakelse extends EngangsstonadTestBase {

    @Test
    @DisplayName("Mor søker Omsorgsovertakelse - godkjent")
    @Description("Mor søker Omsorgsovertakelse - godkjent happy case")
    public void MorSøkerOmsorgsovertakelseGodkjent() throws Exception {
        TestscenarioDto testscenario = opprettScenario("55");
        String søkerAktørID = testscenario.getPersonopplysninger().getSøkerAktørIdent();
        SoekersRelasjonTilBarnet relasjonTilBarnet = SoekersRelasjonErketyper.omsorgsovertakelse(OmsorgsovertakelseÅrsak.ANDRE_FORELDER_DØD);
        Engangsstønad engangsstønadYtelse = new EngangstønadYtelseBuilder(relasjonTilBarnet).build();
        SøknadBuilder søknad = new SøknadBuilder(engangsstønadYtelse, søkerAktørID, SøkersRolle.MOR)
                .withTilleggsopplysninger("Autogenerert erketypetest far søker på omsorgsovertakelse");
        fordel.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        long saksnummer = fordel.sendInnSøknad(søknad.build(), testscenario, DokumenttypeId.ADOPSJONSSOKNAD_ENGANGSSTONAD);

        saksbehandler.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        saksbehandler.hentFagsak(saksnummer);
        saksbehandler.bekreftAksjonspunktBekreftelse(AvklarFaktaTillegsopplysningerBekreftelse.class);


        saksbehandler.hentAksjonspunktbekreftelse(AvklarFaktaOmsorgOgForeldreansvarBekreftelse.class)
                .setVilkårType(saksbehandler.kodeverk.OmsorgsovertakelseVilkårType.getKode("FP_VK_5"));
        saksbehandler.bekreftAksjonspunktBekreftelse(AvklarFaktaOmsorgOgForeldreansvarBekreftelse.class);

        saksbehandler.hentAksjonspunktbekreftelse(VurderingAvOmsorgsvilkoret.class)
                .bekreftGodkjent();
        saksbehandler.bekreftAksjonspunktBekreftelse(VurderingAvOmsorgsvilkoret.class);

        saksbehandler.bekreftAksjonspunktBekreftelse(ForesloVedtakBekreftelse.class);

        beslutter.erLoggetInnMedRolle(Rolle.BESLUTTER);
        beslutter.hentFagsak(saksnummer);

        beslutter.hentAksjonspunktbekreftelse(FatterVedtakBekreftelse.class)
                .godkjennAksjonspunkt(saksbehandler.hentAksjonspunkt(AksjonspunktKoder.MANUELL_VURDERING_AV_OMSORGSVILKÅRET));
        beslutter.fattVedtakOgVentTilAvsluttetBehandling();

        verifiserLikhet(beslutter.valgtBehandling.behandlingsresultat.toString(), "INNVILGET", "Behandlingstatus");
        beslutter.ventTilHistorikkinnslag(HistorikkInnslag.BREV_SENDT);
    }

    @Test
    @DisplayName("Mor søker Omsorgsovertakelse - avvist")
    @Description("Mor søker Omsorgsovertakelse - avvist fordi mor ikke er død")
    public void morSøkerOmsorgsovertakelseAvvist() throws Exception {
        TestscenarioDto testscenario = opprettScenario("55");
        String søkerAktørID = testscenario.getPersonopplysninger().getSøkerAktørIdent();
        SoekersRelasjonTilBarnet relasjonTilBarnet = SoekersRelasjonErketyper.omsorgsovertakelse(OmsorgsovertakelseÅrsak.ANDRE_FORELDER_DØD);
        Engangsstønad engangsstønadYtelse = new EngangstønadYtelseBuilder(relasjonTilBarnet).build();
        SøknadBuilder søknad = new SøknadBuilder(engangsstønadYtelse, søkerAktørID, SøkersRolle.MOR)
                .withTilleggsopplysninger("Autogenerert erketypetest far søker på omsorgsovertakelse");
        fordel.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        long saksnummer = fordel.sendInnSøknad(søknad.build(), testscenario, DokumenttypeId.ADOPSJONSSOKNAD_ENGANGSSTONAD);

        saksbehandler.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        saksbehandler.hentFagsak(saksnummer);
        saksbehandler.bekreftAksjonspunktBekreftelse(AvklarFaktaTillegsopplysningerBekreftelse.class);


        saksbehandler.hentAksjonspunktbekreftelse(AvklarFaktaOmsorgOgForeldreansvarBekreftelse.class)
                .setVilkårType(saksbehandler.kodeverk.OmsorgsovertakelseVilkårType.getKode("FP_VK_5"));
        saksbehandler.bekreftAksjonspunktBekreftelse(AvklarFaktaOmsorgOgForeldreansvarBekreftelse.class);

        saksbehandler.hentAksjonspunktbekreftelse(VurderingAvOmsorgsvilkoret.class)
                .bekreftAvvist(saksbehandler.kodeverk.Avslagsårsak.get("FP_VK_5").getKode("1009" /* Mor ikke død */));
        saksbehandler.bekreftAksjonspunktBekreftelse(VurderingAvOmsorgsvilkoret.class);

        saksbehandler.bekreftAksjonspunktBekreftelse(ForesloVedtakBekreftelse.class);

        beslutter.erLoggetInnMedRolle(Rolle.BESLUTTER);
        beslutter.hentFagsak(saksnummer);

        beslutter.hentAksjonspunktbekreftelse(FatterVedtakBekreftelse.class)
                .godkjennAksjonspunkt(saksbehandler.hentAksjonspunkt(AksjonspunktKoder.MANUELL_VURDERING_AV_OMSORGSVILKÅRET));
        beslutter.fattVedtakOgVentTilAvsluttetBehandling();

        verifiserLikhet(beslutter.valgtBehandling.behandlingsresultat.toString(), "AVSLÅTT", "Behandlingstatus");
    }

    @Test
    @Disabled("TODO hvorfor")
    public void behenadleOmsorgsovertakelseMorOverstyrt() throws Exception {
        TestscenarioDto testscenario = opprettScenario("55");
        String søkerAktørID = testscenario.getPersonopplysninger().getSøkerAktørIdent();
        SoekersRelasjonTilBarnet relasjonTilBarnet = SoekersRelasjonErketyper.omsorgsovertakelse(OmsorgsovertakelseÅrsak.ANDRE_FORELDER_DØD);
        Engangsstønad engangsstønadYtelse = new EngangstønadYtelseBuilder(relasjonTilBarnet).build();
        SøknadBuilder søknad = new SøknadBuilder(engangsstønadYtelse, søkerAktørID, SøkersRolle.MOR)
                .withTilleggsopplysninger("Autogenerert erketypetest far søker på omsorgsovertakelse");
        fordel.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        long saksnummer = fordel.sendInnSøknad(søknad.build(), testscenario, DokumenttypeId.ADOPSJONSSOKNAD_ENGANGSSTONAD);

        saksbehandler.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        saksbehandler.hentFagsak(saksnummer);
        saksbehandler.bekreftAksjonspunktBekreftelse(AvklarFaktaTillegsopplysningerBekreftelse.class);


        saksbehandler.hentAksjonspunktbekreftelse(AvklarFaktaOmsorgOgForeldreansvarBekreftelse.class)
                .setVilkårType(saksbehandler.kodeverk.OmsorgsovertakelseVilkårType.getKode("FP_VK_5"));
        saksbehandler.bekreftAksjonspunktBekreftelse(AvklarFaktaOmsorgOgForeldreansvarBekreftelse.class);

        saksbehandler.hentAksjonspunktbekreftelse(VurderingAvOmsorgsvilkoret.class)
                .bekreftAvvist(saksbehandler.kodeverk.Avslagsårsak.get("FP_VK_33").getKode("1018"));
        saksbehandler.bekreftAksjonspunktBekreftelse(VurderingAvOmsorgsvilkoret.class);

        //TODO bør gå til beslutter
    }

    @Test
    @DisplayName("Far søker Omsorgsovertakelse - godkjent")
    @Description("Far søker Omsorgsovertakelse - får godkjent aksjonspunkt og blir invilget")
    public void farSøkerOmsorgsovertakelseGodkjent() throws Exception {
        TestscenarioDto testscenario = opprettScenario("61");
        String søkerAktørID = testscenario.getPersonopplysninger().getSøkerAktørIdent();
        SoekersRelasjonTilBarnet relasjonTilBarnet = SoekersRelasjonErketyper.omsorgsovertakelse(OmsorgsovertakelseÅrsak.ANDRE_FORELDER_DØD);
        Engangsstønad engangsstønadYtelse = new EngangstønadYtelseBuilder(relasjonTilBarnet).build();
        SøknadBuilder søknad = new SøknadBuilder(engangsstønadYtelse, søkerAktørID, SøkersRolle.MOR)
                .withTilleggsopplysninger("Autogenerert erketypetest far søker på omsorgsovertakelse");
        fordel.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        long saksnummer = fordel.sendInnSøknad(søknad.build(), testscenario, DokumenttypeId.ADOPSJONSSOKNAD_ENGANGSSTONAD);

        saksbehandler.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        saksbehandler.hentFagsak(saksnummer);
        saksbehandler.bekreftAksjonspunktBekreftelse(AvklarFaktaTillegsopplysningerBekreftelse.class);


        saksbehandler.hentAksjonspunktbekreftelse(AvklarFaktaOmsorgOgForeldreansvarBekreftelse.class)
                .setVilkårType(saksbehandler.kodeverk.OmsorgsovertakelseVilkårType.getKode("FP_VK_5"));
        saksbehandler.bekreftAksjonspunktBekreftelse(AvklarFaktaOmsorgOgForeldreansvarBekreftelse.class);

        saksbehandler.hentAksjonspunktbekreftelse(VurderingAvOmsorgsvilkoret.class)
                .bekreftGodkjent();
        saksbehandler.bekreftAksjonspunktBekreftelse(VurderingAvOmsorgsvilkoret.class);

        saksbehandler.bekreftAksjonspunktBekreftelse(ForesloVedtakBekreftelse.class);

        beslutter.erLoggetInnMedRolle(Rolle.BESLUTTER);
        beslutter.hentFagsak(saksnummer);

        beslutter.hentAksjonspunktbekreftelse(FatterVedtakBekreftelse.class)
                .godkjennAksjonspunkt(saksbehandler.hentAksjonspunkt(AksjonspunktKoder.MANUELL_VURDERING_AV_OMSORGSVILKÅRET));
        beslutter.fattVedtakOgVentTilAvsluttetBehandling();

        verifiserLikhet(beslutter.valgtBehandling.behandlingsresultat.toString(), "INNVILGET", "Behandlingstatus");
        beslutter.ventTilHistorikkinnslag(HistorikkInnslag.BREV_SENDT);
    }
    
    @Test
    @DisplayName("Far søker Foreldreansvar 2. ledd - godkjent")
    @Description("Far søker Foreldreansvar 2. ledd - får godkjent aksjonspunkt og blir invilget")
    public void farSøkerForeldreansvarGodkjent() throws Exception {
        TestscenarioDto testscenario = opprettScenario("61");
        String søkerAktørID = testscenario.getPersonopplysninger().getSøkerAktørIdent();
        SoekersRelasjonTilBarnet relasjonTilBarnet = SoekersRelasjonErketyper.omsorgsovertakelse(OmsorgsovertakelseÅrsak.ANDRE_FORELDER_DØD);
        Engangsstønad engangsstønadYtelse = new EngangstønadYtelseBuilder(relasjonTilBarnet).build();
        SøknadBuilder søknad = new SøknadBuilder(engangsstønadYtelse, søkerAktørID, SøkersRolle.MOR)
                .withTilleggsopplysninger("Autogenerert erketypetest far søker på omsorgsovertakelse");
        fordel.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        long saksnummer = fordel.sendInnSøknad(søknad.build(), testscenario, DokumenttypeId.ADOPSJONSSOKNAD_ENGANGSSTONAD);

        saksbehandler.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        saksbehandler.hentFagsak(saksnummer);
        saksbehandler.bekreftAksjonspunktBekreftelse(AvklarFaktaTillegsopplysningerBekreftelse.class);


        saksbehandler.hentAksjonspunktbekreftelse(AvklarFaktaOmsorgOgForeldreansvarBekreftelse.class)
                .setVilkårType(saksbehandler.kodeverk.OmsorgsovertakelseVilkårType.getKode("FP_VK_8"));
        saksbehandler.bekreftAksjonspunktBekreftelse(AvklarFaktaOmsorgOgForeldreansvarBekreftelse.class);

        
        saksbehandler.hentAksjonspunktbekreftelse(VurderingAvForeldreansvarAndreLedd.class)
                .bekreftGodkjent();
        saksbehandler.bekreftAksjonspunktBekreftelse(VurderingAvForeldreansvarAndreLedd.class);

        saksbehandler.bekreftAksjonspunktBekreftelse(ForesloVedtakBekreftelse.class);

        beslutter.erLoggetInnMedRolle(Rolle.BESLUTTER);
        beslutter.hentFagsak(saksnummer);

        beslutter.hentAksjonspunktbekreftelse(FatterVedtakBekreftelse.class)
                .godkjennAksjonspunkt(saksbehandler.hentAksjonspunkt(AksjonspunktKoder.MANUELL_VURDERING_AV_FORELDREANSVARSVILKÅRET_2_LEDD));
        beslutter.fattVedtakOgVentTilAvsluttetBehandling();

        verifiserLikhet(beslutter.valgtBehandling.behandlingsresultat.toString(), "INNVILGET", "Behandlingstatus");
        beslutter.ventTilHistorikkinnslag(HistorikkInnslag.BREV_SENDT);
    }
}
