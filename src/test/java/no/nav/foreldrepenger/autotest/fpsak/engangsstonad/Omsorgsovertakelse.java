package no.nav.foreldrepenger.autotest.fpsak.engangsstonad;

import static no.nav.foreldrepenger.autotest.dokumentgenerator.foreldrepengesoknad.xml.OmsorgsovertakelseÅrsak.ANDRE_FORELDER_DØD;
import static no.nav.foreldrepenger.autotest.dokumentgenerator.foreldrepengesoknad.xml.erketyper.SøknadEngangstønadErketyper.lagEngangstønadOmsorg;
import static no.nav.foreldrepenger.autotest.domain.foreldrepenger.OmsorgsovertakelseVilkårType.FORELDREANSVARSVILKÅRET_2_LEDD;
import static no.nav.foreldrepenger.autotest.domain.foreldrepenger.OmsorgsovertakelseVilkårType.OMSORGSVILKÅRET;
import static no.nav.foreldrepenger.vtp.testmodell.dokument.modell.koder.DokumenttypeId.SØKNAD_ENGANGSSTØNAD_ADOPSJON;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import io.qameta.allure.Description;
import no.nav.foreldrepenger.autotest.base.FpsakTestBase;
import no.nav.foreldrepenger.autotest.dokumentgenerator.foreldrepengesoknad.xml.SøkersRolle;
import no.nav.foreldrepenger.autotest.dokumentgenerator.foreldrepengesoknad.xml.builders.EngangstønadBuilder;
import no.nav.foreldrepenger.autotest.dokumentgenerator.foreldrepengesoknad.xml.erketyper.RelasjonTilBarnetErketyper;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.Avslagsårsak;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.BehandlingResultatType;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.FatterVedtakBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.ForeslåVedtakBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.VurderingAvForeldreansvarAndreLedd;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.VurderingAvOmsorgsvilkoret;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.avklarfakta.AvklarFaktaOmsorgOgForeldreansvarBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.AksjonspunktKoder;
import no.nav.foreldrepenger.vtp.kontrakter.TestscenarioDto;

@Tag("fpsak")
@Tag("engangsstonad")
class Omsorgsovertakelse extends FpsakTestBase {

    @Test
    @DisplayName("Mor søker Omsorgsovertakelse - godkjent")
    @Description("Mor søker Omsorgsovertakelse - godkjent happy case")
    void MorSøkerOmsorgsovertakelseGodkjent() {
        TestscenarioDto testscenario = opprettTestscenario("55");
        String søkerAktørID = testscenario.personopplysninger().søkerAktørIdent();

        EngangstønadBuilder søknad = lagEngangstønadOmsorg(søkerAktørID, SøkersRolle.MOR,
                ANDRE_FORELDER_DØD);
        long saksnummer = fordel.sendInnSøknad(søknad.build(), testscenario,
                SØKNAD_ENGANGSSTØNAD_ADOPSJON);

        saksbehandler.hentFagsak(saksnummer);
        AvklarFaktaOmsorgOgForeldreansvarBekreftelse avklarFaktaOmsorgOgForeldreansvarBekreftelse = saksbehandler
                .hentAksjonspunktbekreftelse(AvklarFaktaOmsorgOgForeldreansvarBekreftelse.class);
        avklarFaktaOmsorgOgForeldreansvarBekreftelse
                .setVilkårType(OMSORGSVILKÅRET);
        saksbehandler.bekreftAksjonspunkt(avklarFaktaOmsorgOgForeldreansvarBekreftelse);

        VurderingAvOmsorgsvilkoret vurderingAvOmsorgsvilkoret = saksbehandler
                .hentAksjonspunktbekreftelse(VurderingAvOmsorgsvilkoret.class);
        vurderingAvOmsorgsvilkoret.bekreftGodkjent();
        saksbehandler.bekreftAksjonspunkt(vurderingAvOmsorgsvilkoret);

        saksbehandler.bekreftAksjonspunktMedDefaultVerdier(ForeslåVedtakBekreftelse.class);

        beslutter.hentFagsak(saksnummer);

        FatterVedtakBekreftelse bekreftelse = beslutter.hentAksjonspunktbekreftelse(FatterVedtakBekreftelse.class);
        bekreftelse.godkjennAksjonspunkt(
                saksbehandler.hentAksjonspunkt(AksjonspunktKoder.MANUELL_VURDERING_AV_OMSORGSVILKÅRET));
        beslutter.fattVedtakOgVentTilAvsluttetBehandling(bekreftelse);

        assertThat(beslutter.valgtBehandling.behandlingsresultat.getType())
                .as("Behandlingsresultat")
                .isEqualTo(BehandlingResultatType.INNVILGET);
    }

    @Test
    @DisplayName("Mor søker Omsorgsovertakelse - avvist")
    @Description("Mor søker Omsorgsovertakelse - avvist fordi mor ikke er død")
    void morSøkerOmsorgsovertakelseAvvist() {
        TestscenarioDto testscenario = opprettTestscenario("55");
        String søkerAktørID = testscenario.personopplysninger().søkerAktørIdent();

        EngangstønadBuilder søknad = lagEngangstønadOmsorg(søkerAktørID, SøkersRolle.MOR,
                ANDRE_FORELDER_DØD);
        long saksnummer = fordel.sendInnSøknad(søknad.build(), testscenario,
                SØKNAD_ENGANGSSTØNAD_ADOPSJON);

        saksbehandler.hentFagsak(saksnummer);
        AvklarFaktaOmsorgOgForeldreansvarBekreftelse avklarFaktaOmsorgOgForeldreansvarBekreftelse = saksbehandler
                .hentAksjonspunktbekreftelse(AvklarFaktaOmsorgOgForeldreansvarBekreftelse.class);
        avklarFaktaOmsorgOgForeldreansvarBekreftelse
                .setVilkårType(OMSORGSVILKÅRET);
        saksbehandler.bekreftAksjonspunkt(avklarFaktaOmsorgOgForeldreansvarBekreftelse);

        VurderingAvOmsorgsvilkoret vurderingAvOmsorgsvilkoret = saksbehandler
                .hentAksjonspunktbekreftelse(VurderingAvOmsorgsvilkoret.class);
        vurderingAvOmsorgsvilkoret
                .bekreftAvvist(Avslagsårsak.MOR_IKKE_DØD);
        saksbehandler.bekreftAksjonspunkt(vurderingAvOmsorgsvilkoret);

        saksbehandler.bekreftAksjonspunktMedDefaultVerdier(ForeslåVedtakBekreftelse.class);

        beslutter.hentFagsak(saksnummer);

        FatterVedtakBekreftelse bekreftelse = beslutter.hentAksjonspunktbekreftelse(FatterVedtakBekreftelse.class);
        bekreftelse.godkjennAksjonspunkt(
                saksbehandler.hentAksjonspunkt(AksjonspunktKoder.MANUELL_VURDERING_AV_OMSORGSVILKÅRET));
        beslutter.fattVedtakOgVentTilAvsluttetBehandling(bekreftelse);

        assertThat(beslutter.valgtBehandling.behandlingsresultat.getType())
                .as("Behandlingsresultat")
                .isEqualTo(BehandlingResultatType.AVSLÅTT);
    }

    @Test
    @DisplayName("Far søker Omsorgsovertakelse - godkjent")
    @Description("Far søker Omsorgsovertakelse - får godkjent aksjonspunkt og blir invilget")
    void farSøkerOmsorgsovertakelseGodkjent() {
        TestscenarioDto testscenario = opprettTestscenario("61");
        String søkerAktørID = testscenario.personopplysninger().søkerAktørIdent();
        RelasjonTilBarnetErketyper.omsorgsovertakelse(ANDRE_FORELDER_DØD);
        EngangstønadBuilder søknad = lagEngangstønadOmsorg(søkerAktørID, SøkersRolle.MOR,
                ANDRE_FORELDER_DØD);
        long saksnummer = fordel.sendInnSøknad(søknad.build(), testscenario,
                SØKNAD_ENGANGSSTØNAD_ADOPSJON);

        saksbehandler.hentFagsak(saksnummer);
        AvklarFaktaOmsorgOgForeldreansvarBekreftelse avklarFaktaOmsorgOgForeldreansvarBekreftelse = saksbehandler
                .hentAksjonspunktbekreftelse(AvklarFaktaOmsorgOgForeldreansvarBekreftelse.class);
        avklarFaktaOmsorgOgForeldreansvarBekreftelse
                .setVilkårType(OMSORGSVILKÅRET);
        saksbehandler.bekreftAksjonspunkt(avklarFaktaOmsorgOgForeldreansvarBekreftelse);

        VurderingAvOmsorgsvilkoret vurderingAvOmsorgsvilkoret = saksbehandler
                .hentAksjonspunktbekreftelse(VurderingAvOmsorgsvilkoret.class);
        vurderingAvOmsorgsvilkoret.bekreftGodkjent();
        saksbehandler.bekreftAksjonspunkt(vurderingAvOmsorgsvilkoret);

        saksbehandler.bekreftAksjonspunktMedDefaultVerdier(ForeslåVedtakBekreftelse.class);

        beslutter.hentFagsak(saksnummer);

        FatterVedtakBekreftelse bekreftelse = beslutter.hentAksjonspunktbekreftelse(FatterVedtakBekreftelse.class);
        bekreftelse.godkjennAksjonspunkt(
                saksbehandler.hentAksjonspunkt(AksjonspunktKoder.MANUELL_VURDERING_AV_OMSORGSVILKÅRET));
        beslutter.fattVedtakOgVentTilAvsluttetBehandling(bekreftelse);

        assertThat(beslutter.valgtBehandling.behandlingsresultat.getType())
                .as("Behandlingsresultat")
                .isEqualTo(BehandlingResultatType.INNVILGET);
    }

    @Test
    @DisplayName("Far søker Foreldreansvar 2. ledd - godkjent")
    @Description("Far søker Foreldreansvar 2. ledd - får godkjent aksjonspunkt og blir invilget")
    void farSøkerForeldreansvarGodkjent() {
        TestscenarioDto testscenario = opprettTestscenario("61");
        String søkerAktørID = testscenario.personopplysninger().søkerAktørIdent();
        EngangstønadBuilder søknad = lagEngangstønadOmsorg(søkerAktørID, SøkersRolle.MOR, ANDRE_FORELDER_DØD);
        long saksnummer = fordel.sendInnSøknad(søknad.build(), testscenario, SØKNAD_ENGANGSSTØNAD_ADOPSJON);

        saksbehandler.hentFagsak(saksnummer);
        AvklarFaktaOmsorgOgForeldreansvarBekreftelse avklarFaktaOmsorgOgForeldreansvarBekreftelse = saksbehandler
                .hentAksjonspunktbekreftelse(AvklarFaktaOmsorgOgForeldreansvarBekreftelse.class);
        avklarFaktaOmsorgOgForeldreansvarBekreftelse
                .setVilkårType(FORELDREANSVARSVILKÅRET_2_LEDD);
        saksbehandler.bekreftAksjonspunkt(avklarFaktaOmsorgOgForeldreansvarBekreftelse);

        var vurderingAvForeldreansvarAndreLedd = saksbehandler
                .hentAksjonspunktbekreftelse(VurderingAvForeldreansvarAndreLedd.class);
        vurderingAvForeldreansvarAndreLedd.bekreftGodkjent();
        saksbehandler.bekreftAksjonspunkt(vurderingAvForeldreansvarAndreLedd);

        saksbehandler.bekreftAksjonspunktMedDefaultVerdier(ForeslåVedtakBekreftelse.class);

        beslutter.hentFagsak(saksnummer);

        FatterVedtakBekreftelse bekreftelse = beslutter.hentAksjonspunktbekreftelse(FatterVedtakBekreftelse.class);
        bekreftelse.godkjennAksjonspunkt(
                saksbehandler.hentAksjonspunkt(AksjonspunktKoder.MANUELL_VURDERING_AV_FORELDREANSVARSVILKÅRET_2_LEDD));
        beslutter.fattVedtakOgVentTilAvsluttetBehandling(bekreftelse);

        assertThat(beslutter.valgtBehandling.behandlingsresultat.getType())
                .as("Behandlingsresultat")
                .isEqualTo(BehandlingResultatType.INNVILGET);
    }
}
