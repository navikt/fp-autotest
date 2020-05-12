package no.nav.foreldrepenger.autotest.foreldrepenger.engangsstonad;

import io.qameta.allure.Description;
import no.nav.foreldrepenger.autotest.aktoerer.Aktoer.Rolle;
import no.nav.foreldrepenger.autotest.base.FpsakTestBase;
import no.nav.foreldrepenger.autotest.dokumentgenerator.foreldrepengesoknad.OmsorgsovertakelseÅrsak;
import no.nav.foreldrepenger.autotest.dokumentgenerator.foreldrepengesoknad.SøkersRolle;
import no.nav.foreldrepenger.autotest.dokumentgenerator.foreldrepengesoknad.builders.EngangstønadBuilder;
import no.nav.foreldrepenger.autotest.erketyper.RelasjonTilBarnetErketyper;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.FatterVedtakBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.ForesloVedtakBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.VurderingAvForeldreansvarAndreLedd;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.VurderingAvOmsorgsvilkoret;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.avklarfakta.AvklarFaktaOmsorgOgForeldreansvarBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.avklarfakta.AvklarFaktaTillegsopplysningerBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.AksjonspunktKoder;
import no.nav.foreldrepenger.vtp.kontrakter.TestscenarioDto;
import no.nav.foreldrepenger.vtp.testmodell.dokument.modell.koder.DokumenttypeId;
import no.nav.vedtak.felles.xml.soeknad.felles.v3.SoekersRelasjonTilBarnet;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import static no.nav.foreldrepenger.autotest.erketyper.SøknadEngangstønadErketyper.lagEngangstønadOmsorg;

@Execution(ExecutionMode.CONCURRENT)
@Tag("fpsak")
@Tag("engangsstonad")
public class Omsorgsovertakelse extends FpsakTestBase {

    @Test
    @DisplayName("Mor søker Omsorgsovertakelse - godkjent")
    @Description("Mor søker Omsorgsovertakelse - godkjent happy case")
    public void MorSøkerOmsorgsovertakelseGodkjent() throws Exception {
        TestscenarioDto testscenario = opprettTestscenarioFraVTPTemplate("55");
        String søkerAktørID = testscenario.getPersonopplysninger().getSøkerAktørIdent();

        EngangstønadBuilder søknad = lagEngangstønadOmsorg(søkerAktørID, SøkersRolle.MOR,
                OmsorgsovertakelseÅrsak.ANDRE_FORELDER_DØD);
        fordel.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        long saksnummer = fordel.sendInnSøknad(søknad.build(), testscenario, DokumenttypeId.ADOPSJONSSOKNAD_ENGANGSSTONAD);

        saksbehandler.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        saksbehandler.hentFagsak(saksnummer);
        AvklarFaktaOmsorgOgForeldreansvarBekreftelse avklarFaktaOmsorgOgForeldreansvarBekreftelse = saksbehandler.hentAksjonspunktbekreftelse(AvklarFaktaOmsorgOgForeldreansvarBekreftelse.class);
        avklarFaktaOmsorgOgForeldreansvarBekreftelse.setVilkårType(saksbehandler.kodeverk.OmsorgsovertakelseVilkårType.getKode("FP_VK_5"));
        saksbehandler.bekreftAksjonspunkt(avklarFaktaOmsorgOgForeldreansvarBekreftelse);

        VurderingAvOmsorgsvilkoret vurderingAvOmsorgsvilkoret = saksbehandler.hentAksjonspunktbekreftelse(VurderingAvOmsorgsvilkoret.class);
        vurderingAvOmsorgsvilkoret.bekreftGodkjent();
        saksbehandler.bekreftAksjonspunkt(vurderingAvOmsorgsvilkoret);

        saksbehandler.bekreftAksjonspunktMedDefaultVerdier(ForesloVedtakBekreftelse.class);

        beslutter.erLoggetInnMedRolle(Rolle.BESLUTTER);
        beslutter.hentFagsak(saksnummer);

        FatterVedtakBekreftelse bekreftelse = beslutter.hentAksjonspunktbekreftelse(FatterVedtakBekreftelse.class);
        bekreftelse.godkjennAksjonspunkt(saksbehandler.hentAksjonspunkt(AksjonspunktKoder.MANUELL_VURDERING_AV_OMSORGSVILKÅRET));
        beslutter.fattVedtakOgVentTilAvsluttetBehandling(bekreftelse);

        verifiserLikhet(beslutter.valgtBehandling.behandlingsresultat.toString(), "INNVILGET", "Behandlingstatus");
        //TODO: Fjernet vent på brev sendt - bytte med annen assertion
    }

    @Test
    @DisplayName("Mor søker Omsorgsovertakelse - avvist")
    @Description("Mor søker Omsorgsovertakelse - avvist fordi mor ikke er død")
    public void morSøkerOmsorgsovertakelseAvvist() throws Exception {
        TestscenarioDto testscenario = opprettTestscenarioFraVTPTemplate("55");
        String søkerAktørID = testscenario.getPersonopplysninger().getSøkerAktørIdent();

        EngangstønadBuilder søknad = lagEngangstønadOmsorg(søkerAktørID, SøkersRolle.MOR,
                OmsorgsovertakelseÅrsak.ANDRE_FORELDER_DØD);
        fordel.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        long saksnummer = fordel.sendInnSøknad(søknad.build(), testscenario, DokumenttypeId.ADOPSJONSSOKNAD_ENGANGSSTONAD);

        saksbehandler.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        saksbehandler.hentFagsak(saksnummer);
        AvklarFaktaOmsorgOgForeldreansvarBekreftelse avklarFaktaOmsorgOgForeldreansvarBekreftelse = saksbehandler.hentAksjonspunktbekreftelse(AvklarFaktaOmsorgOgForeldreansvarBekreftelse.class);
        avklarFaktaOmsorgOgForeldreansvarBekreftelse.setVilkårType(saksbehandler.kodeverk.OmsorgsovertakelseVilkårType.getKode("FP_VK_5"));
        saksbehandler.bekreftAksjonspunkt(avklarFaktaOmsorgOgForeldreansvarBekreftelse);

        VurderingAvOmsorgsvilkoret vurderingAvOmsorgsvilkoret = saksbehandler.hentAksjonspunktbekreftelse(VurderingAvOmsorgsvilkoret.class);
        vurderingAvOmsorgsvilkoret.bekreftAvvist(saksbehandler.kodeverk.Avslagsårsak.get("FP_VK_5").getKode("1009" /* Mor ikke død */));
        saksbehandler.bekreftAksjonspunkt(vurderingAvOmsorgsvilkoret);

        saksbehandler.bekreftAksjonspunktMedDefaultVerdier(ForesloVedtakBekreftelse.class);

        beslutter.erLoggetInnMedRolle(Rolle.BESLUTTER);
        beslutter.hentFagsak(saksnummer);

        FatterVedtakBekreftelse bekreftelse = beslutter.hentAksjonspunktbekreftelse(FatterVedtakBekreftelse.class);
        bekreftelse.godkjennAksjonspunkt(saksbehandler.hentAksjonspunkt(AksjonspunktKoder.MANUELL_VURDERING_AV_OMSORGSVILKÅRET));
        beslutter.fattVedtakOgVentTilAvsluttetBehandling(bekreftelse);

        verifiserLikhet(beslutter.valgtBehandling.behandlingsresultat.toString(), "AVSLÅTT", "Behandlingstatus");
    }

    @Test
    @Disabled("TODO hvorfor")
    public void behenadleOmsorgsovertakelseMorOverstyrt() throws Exception {
        TestscenarioDto testscenario = opprettTestscenarioFraVTPTemplate("55");
        String søkerAktørID = testscenario.getPersonopplysninger().getSøkerAktørIdent();
        EngangstønadBuilder søknad = lagEngangstønadOmsorg(søkerAktørID, SøkersRolle.MOR,
                OmsorgsovertakelseÅrsak.ANDRE_FORELDER_DØD);
        fordel.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        long saksnummer = fordel.sendInnSøknad(søknad.build(), testscenario, DokumenttypeId.ADOPSJONSSOKNAD_ENGANGSSTONAD);

        saksbehandler.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        saksbehandler.hentFagsak(saksnummer);
        saksbehandler.hentAksjonspunktbekreftelse(AvklarFaktaOmsorgOgForeldreansvarBekreftelse.class)
                .setVilkårType(saksbehandler.kodeverk.OmsorgsovertakelseVilkårType.getKode("FP_VK_5"));
        saksbehandler.bekreftAksjonspunktMedDefaultVerdier(AvklarFaktaOmsorgOgForeldreansvarBekreftelse.class);

        saksbehandler.hentAksjonspunktbekreftelse(VurderingAvOmsorgsvilkoret.class)
                .bekreftAvvist(saksbehandler.kodeverk.Avslagsårsak.get("FP_VK_33").getKode("1018"));
        saksbehandler.bekreftAksjonspunktMedDefaultVerdier(VurderingAvOmsorgsvilkoret.class);

        //TODO bør gå til beslutter
    }

    @Test
    @DisplayName("Far søker Omsorgsovertakelse - godkjent")
    @Description("Far søker Omsorgsovertakelse - får godkjent aksjonspunkt og blir invilget")
    public void farSøkerOmsorgsovertakelseGodkjent() throws Exception {
        TestscenarioDto testscenario = opprettTestscenarioFraVTPTemplate("61");
        String søkerAktørID = testscenario.getPersonopplysninger().getSøkerAktørIdent();
        SoekersRelasjonTilBarnet relasjonTilBarnet = RelasjonTilBarnetErketyper.omsorgsovertakelse(OmsorgsovertakelseÅrsak.ANDRE_FORELDER_DØD);
        EngangstønadBuilder søknad = lagEngangstønadOmsorg(søkerAktørID, SøkersRolle.MOR,
                OmsorgsovertakelseÅrsak.ANDRE_FORELDER_DØD);
        fordel.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        long saksnummer = fordel.sendInnSøknad(søknad.build(), testscenario, DokumenttypeId.ADOPSJONSSOKNAD_ENGANGSSTONAD);

        saksbehandler.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        saksbehandler.hentFagsak(saksnummer);
        saksbehandler.bekreftAksjonspunktMedDefaultVerdier(AvklarFaktaTillegsopplysningerBekreftelse.class);


        AvklarFaktaOmsorgOgForeldreansvarBekreftelse avklarFaktaOmsorgOgForeldreansvarBekreftelse = saksbehandler.hentAksjonspunktbekreftelse(AvklarFaktaOmsorgOgForeldreansvarBekreftelse.class);
        avklarFaktaOmsorgOgForeldreansvarBekreftelse.setVilkårType(saksbehandler.kodeverk.OmsorgsovertakelseVilkårType.getKode("FP_VK_5"));
        saksbehandler.bekreftAksjonspunkt(avklarFaktaOmsorgOgForeldreansvarBekreftelse);

        VurderingAvOmsorgsvilkoret vurderingAvOmsorgsvilkoret = saksbehandler.hentAksjonspunktbekreftelse(VurderingAvOmsorgsvilkoret.class);
        vurderingAvOmsorgsvilkoret.bekreftGodkjent();
        saksbehandler.bekreftAksjonspunkt(vurderingAvOmsorgsvilkoret);

        saksbehandler.bekreftAksjonspunktMedDefaultVerdier(ForesloVedtakBekreftelse.class);

        beslutter.erLoggetInnMedRolle(Rolle.BESLUTTER);
        beslutter.hentFagsak(saksnummer);

        FatterVedtakBekreftelse bekreftelse = beslutter.hentAksjonspunktbekreftelse(FatterVedtakBekreftelse.class);
        bekreftelse.godkjennAksjonspunkt(saksbehandler.hentAksjonspunkt(AksjonspunktKoder.MANUELL_VURDERING_AV_OMSORGSVILKÅRET));
        beslutter.fattVedtakOgVentTilAvsluttetBehandling(bekreftelse);

        verifiserLikhet(beslutter.valgtBehandling.behandlingsresultat.toString(), "INNVILGET", "Behandlingstatus");
        //TODO: Fjernet vent på brev sendt - bytte med annen assertion
    }

    @Test
    @DisplayName("Far søker Foreldreansvar 2. ledd - godkjent")
    @Description("Far søker Foreldreansvar 2. ledd - får godkjent aksjonspunkt og blir invilget")
    public void farSøkerForeldreansvarGodkjent() throws Exception {
        TestscenarioDto testscenario = opprettTestscenarioFraVTPTemplate("61");
        String søkerAktørID = testscenario.getPersonopplysninger().getSøkerAktørIdent();
        EngangstønadBuilder søknad = lagEngangstønadOmsorg(søkerAktørID, SøkersRolle.MOR,
                OmsorgsovertakelseÅrsak.ANDRE_FORELDER_DØD);
        fordel.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        long saksnummer = fordel.sendInnSøknad(søknad.build(), testscenario, DokumenttypeId.ADOPSJONSSOKNAD_ENGANGSSTONAD);

        saksbehandler.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        saksbehandler.hentFagsak(saksnummer);
        saksbehandler.bekreftAksjonspunktMedDefaultVerdier(AvklarFaktaTillegsopplysningerBekreftelse.class);

        AvklarFaktaOmsorgOgForeldreansvarBekreftelse avklarFaktaOmsorgOgForeldreansvarBekreftelse = saksbehandler.hentAksjonspunktbekreftelse(AvklarFaktaOmsorgOgForeldreansvarBekreftelse.class);
        avklarFaktaOmsorgOgForeldreansvarBekreftelse.setVilkårType(saksbehandler.kodeverk.OmsorgsovertakelseVilkårType.getKode("FP_VK_8"));
        saksbehandler.bekreftAksjonspunkt(avklarFaktaOmsorgOgForeldreansvarBekreftelse);

        var vurderingAvForeldreansvarAndreLedd = saksbehandler.hentAksjonspunktbekreftelse(VurderingAvForeldreansvarAndreLedd.class);
        vurderingAvForeldreansvarAndreLedd.bekreftGodkjent();
        saksbehandler.bekreftAksjonspunkt(vurderingAvForeldreansvarAndreLedd);

        saksbehandler.bekreftAksjonspunktMedDefaultVerdier(ForesloVedtakBekreftelse.class);

        beslutter.erLoggetInnMedRolle(Rolle.BESLUTTER);
        beslutter.hentFagsak(saksnummer);

        FatterVedtakBekreftelse bekreftelse = beslutter.hentAksjonspunktbekreftelse(FatterVedtakBekreftelse.class);
        bekreftelse.godkjennAksjonspunkt(saksbehandler.hentAksjonspunkt(AksjonspunktKoder.MANUELL_VURDERING_AV_FORELDREANSVARSVILKÅRET_2_LEDD));
        beslutter.fattVedtakOgVentTilAvsluttetBehandling(bekreftelse);

        verifiserLikhet(beslutter.valgtBehandling.behandlingsresultat.toString(), "INNVILGET", "Behandlingstatus");
        //TODO: Fjernet vent på brev sendt - bytte med annen assertion
    }
}
