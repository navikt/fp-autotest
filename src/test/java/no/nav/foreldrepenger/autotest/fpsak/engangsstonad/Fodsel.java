package no.nav.foreldrepenger.autotest.fpsak.engangsstonad;

import static no.nav.foreldrepenger.autotest.dokumentgenerator.foreldrepengesoknad.xml.erketyper.SøknadEngangstønadErketyper.lagEngangstønadFødsel;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;

import org.junit.jupiter.api.Disabled;
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
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.BehandlingÅrsakType;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.MedlemskapManuellVurderingType;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.FatterVedtakBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.ForeslåVedtakBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.VarselOmRevurderingBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.VurderManglendeFodselBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.avklarfakta.AvklarBrukerBosattBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.avklarfakta.AvklarBrukerHarGyldigPeriodeBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.avklarfakta.AvklarFaktaTillegsopplysningerBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.avklarfakta.AvklarFaktaVergeBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.overstyr.OverstyrBeregning;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.overstyr.OverstyrFodselsvilkaaret;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.beregning.Beregningsresultat;
import no.nav.foreldrepenger.vtp.kontrakter.TestscenarioDto;
import no.nav.foreldrepenger.vtp.testmodell.dokument.modell.koder.DokumenttypeId;

@Tag("fpsak")
@Tag("engangsstonad")
class Fodsel extends FpsakTestBase {

    @Test
    @DisplayName("Mor søker fødsel - godkjent")
    @Description("Mor søker fødsel - godkjent happy case")
    void morSøkerFødselGodkjent() {
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
                .as("Behandlingstatus")
                .isEqualTo(BehandlingResultatType.INNVILGET);
    }

    @Test
    @DisplayName("Mor søker fødsel - avvist")
    @Description("Mor søker fødsel - avvist fordi dokumentasjon mangler og barn er ikke registrert i tps")
    void morSøkerFødselAvvist() {
        TestscenarioDto testscenario = opprettTestscenario("55");
        EngangstønadBuilder søknad = lagEngangstønadFødsel(
                testscenario.personopplysninger().søkerAktørIdent(),
                SøkersRolle.MOR,
                LocalDate.now().minusDays(30L));

        long saksnummer = fordel.sendInnSøknad(søknad.build(), testscenario,
                DokumenttypeId.SØKNAD_ENGANGSSTØNAD_FØDSEL);

        saksbehandler.hentFagsak(saksnummer);
        VurderManglendeFodselBekreftelse vurderManglendeFodselBekreftelse = saksbehandler
                .hentAksjonspunktbekreftelse(VurderManglendeFodselBekreftelse.class);
        vurderManglendeFodselBekreftelse.bekreftDokumentasjonIkkeForeligger();
        saksbehandler.bekreftAksjonspunkt(vurderManglendeFodselBekreftelse);

        saksbehandler.bekreftAksjonspunktMedDefaultVerdier(ForeslåVedtakBekreftelse.class);

        beslutter.hentFagsak(saksnummer);
        FatterVedtakBekreftelse bekreftelse = beslutter.hentAksjonspunktbekreftelse(FatterVedtakBekreftelse.class);
        bekreftelse.godkjennAksjonspunkter(beslutter.hentAksjonspunktSomSkalTilTotrinnsBehandling());
        beslutter.fattVedtakOgVentTilAvsluttetBehandling(bekreftelse);

        assertThat(beslutter.valgtBehandling.hentBehandlingsresultat())
                .as("Behandlingstatus")
                .isEqualTo(BehandlingResultatType.AVSLÅTT);
        assertThat(saksbehandler.valgtBehandling.hentAvslagsarsak())
                .as("Avslagsarsak (Forventer at behandlingen er avslått fordi fødselsdato er ikke oppgitt eller registrert!)")
                .isEqualTo(Avslagsårsak.FØDSELSDATO_IKKE_OPPGITT_ELLER_REGISTRERT);
    }

    @Test
    @DisplayName("Far søker registrert fødsel")
    @Description("Far søker registrert fødsel og blir avvist fordi far søker")
    void farSøkerFødselRegistrert() {
        TestscenarioDto testscenario = opprettTestscenario("60");
        EngangstønadBuilder søknad = lagEngangstønadFødsel(
                testscenario.personopplysninger().søkerAktørIdent(),
                SøkersRolle.MOR,
                testscenario.personopplysninger().fødselsdato());
        long saksnummer = fordel.sendInnSøknad(søknad.build(), testscenario,
                DokumenttypeId.SØKNAD_ENGANGSSTØNAD_FØDSEL);

        saksbehandler.hentFagsak(saksnummer);
        saksbehandler.ventTilAvsluttetBehandling();

        assertThat(saksbehandler.valgtBehandling.hentBehandlingsresultat())
                .as("Behandlingstatus")
                .isEqualTo(BehandlingResultatType.AVSLÅTT);
        assertThat(saksbehandler.valgtBehandling.hentAvslagsarsak())
                .as("Avslagsårsak (Forventer at behandlingen er avslått fordi søker er far!)")
                .isEqualTo(Avslagsårsak.SØKER_ER_FAR);
    }

    @Test
    @DisplayName("Mor søker fødsel overstyrt vilkår")
    @Description("Mor søker fødsel overstyrt vilkår adopsjon fra godkjent til avslått")
    void morSøkerFødselOverstyrt() {
        TestscenarioDto testscenario = opprettTestscenario("55");
        EngangstønadBuilder søknad = lagEngangstønadFødsel(
                testscenario.personopplysninger().søkerAktørIdent(),
                SøkersRolle.MOR,
                LocalDate.now().minusDays(30L));

        long saksnummer = fordel.sendInnSøknad(søknad.build(), testscenario,
                DokumenttypeId.SØKNAD_ENGANGSSTØNAD_FØDSEL);

        saksbehandler.hentFagsak(saksnummer);
        VurderManglendeFodselBekreftelse vurderManglendeFodselBekreftelse = saksbehandler
                .hentAksjonspunktbekreftelse(VurderManglendeFodselBekreftelse.class);
        vurderManglendeFodselBekreftelse.bekreftDokumentasjonForeligger(1, LocalDate.now().minusMonths(1));
        saksbehandler.bekreftAksjonspunkt(vurderManglendeFodselBekreftelse);

        assertThat(saksbehandler.valgtBehandling.hentBehandlingsresultat())
                .as("Behandlingstatus")
                .isEqualTo(BehandlingResultatType.INNVILGET);

        overstyrer.hentFagsak(saksnummer);

        OverstyrFodselsvilkaaret overstyr = new OverstyrFodselsvilkaaret();
        overstyr.avvis(Avslagsårsak.SØKER_ER_FAR);
        overstyr.setBegrunnelse("avvist");
        overstyrer.overstyr(overstyr);

        assertThat(overstyrer.valgtBehandling.hentBehandlingsresultat())
                .as("Behandlingsresultat")
                .isEqualTo(BehandlingResultatType.AVSLÅTT);
        assertThat(overstyrer.valgtBehandling.hentAvslagsarsak())
                .as("Avslagsarsak")
                .isEqualTo(Avslagsårsak.SØKER_ER_FAR);
        overstyrer.bekreftAksjonspunktMedDefaultVerdier(ForeslåVedtakBekreftelse.class);

        beslutter.hentFagsak(saksnummer);
        FatterVedtakBekreftelse bekreftelse = beslutter.hentAksjonspunktbekreftelse(FatterVedtakBekreftelse.class);
        bekreftelse.godkjennAksjonspunkter(beslutter.hentAksjonspunktSomSkalTilTotrinnsBehandling());
        beslutter.fattVedtakOgVentTilAvsluttetBehandling(bekreftelse);
    }

    // TODO (OL): Analyser hvorfor denne ikke fungerer med ny henting av aksjonspunkter.
    @Test
    @Disabled
    @DisplayName("Mor søker fødsel - beregning overstyrt")
    @Description("Mor søker fødsel - beregning overstyrt fra ett beløp til 10 kroner")
    void morSøkerFødselBeregningOverstyrt() {
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

        // Overstyr beregning
        overstyrer.hentFagsak(saksnummer);
        overstyrer.opprettBehandlingRevurdering(BehandlingÅrsakType.RE_FEIL_PROSESSUELL);
        overstyrer.ventPåOgVelgRevurderingBehandling();

        var varselOmRevurderingBekreftelse = overstyrer
                .hentAksjonspunktbekreftelse(VarselOmRevurderingBekreftelse.class);
        varselOmRevurderingBekreftelse.bekreftIkkeSendVarsel();
        overstyrer.bekreftAksjonspunkt(varselOmRevurderingBekreftelse);

        overstyrer.bekreftAksjonspunktMedDefaultVerdier(AvklarFaktaTillegsopplysningerBekreftelse.class);
        OverstyrBeregning overstyrBeregning = new OverstyrBeregning(10);
        overstyrer.overstyr(overstyrBeregning);
        assertThat(overstyrer.valgtBehandling.getBeregningResultatEngangsstonad().getBeregnetTilkjentYtelse())
                .as("BeregnetTilkjentYtelse")
                .isEqualTo(10);

        ForeslåVedtakBekreftelse foreslåVedtakBekreftelse = overstyrer
                .hentAksjonspunktbekreftelse(ForeslåVedtakBekreftelse.class);
        overstyrer.bekreftAksjonspunkt(foreslåVedtakBekreftelse);

        beslutter.hentFagsak(saksnummer);
        beslutter.ventPåOgVelgRevurderingBehandling();

        FatterVedtakBekreftelse bekreftelse = beslutter.hentAksjonspunktbekreftelse(FatterVedtakBekreftelse.class);
        bekreftelse.godkjennAksjonspunkter(beslutter.hentAksjonspunktSomSkalTilTotrinnsBehandling());
        beslutter.fattVedtakOgVentTilAvsluttetBehandling(bekreftelse);
    }

    @Test
    @DisplayName("Mor søker fødsel med flere barn")
    @Description("Mor søker fødsel med flere barn - happy case flere barn")
    void morSøkerFødselFlereBarn() {
        TestscenarioDto testscenario = opprettTestscenario("53");
        var aktørID = testscenario.personopplysninger().søkerAktørIdent();
        var fødselsdato = testscenario.personopplysninger().fødselsdato();
        EngangstønadBuilder søknad = lagEngangstønadFødsel(aktørID, SøkersRolle.MOR, fødselsdato)
                .medSoekersRelasjonTilBarnet(RelasjonTilBarnetErketyper.fødsel(2, fødselsdato));

        long saksnummer = fordel.sendInnSøknad(søknad.build(), testscenario,
                DokumenttypeId.SØKNAD_ENGANGSSTØNAD_FØDSEL);

        saksbehandler.hentFagsak(saksnummer);
        VurderManglendeFodselBekreftelse vurderManglendeFodselBekreftelse = saksbehandler
                .hentAksjonspunktbekreftelse(VurderManglendeFodselBekreftelse.class);
        vurderManglendeFodselBekreftelse.bekreftDokumentasjonForeligger(2, LocalDate.now().minusDays(40));
        saksbehandler.bekreftAksjonspunkt(vurderManglendeFodselBekreftelse);

        AvklarBrukerHarGyldigPeriodeBekreftelse avklarBrukerHarGyldigPeriodeBekreftelse = saksbehandler
                .hentAksjonspunktbekreftelse(AvklarBrukerHarGyldigPeriodeBekreftelse.class);
        avklarBrukerHarGyldigPeriodeBekreftelse.setVurdering(
                MedlemskapManuellVurderingType.MEDLEM,
                saksbehandler.valgtBehandling.getMedlem().getMedlemskapPerioder());
        saksbehandler.bekreftAksjonspunkt(avklarBrukerHarGyldigPeriodeBekreftelse);
        var bosatt = saksbehandler.hentAksjonspunktbekreftelse(AvklarBrukerBosattBekreftelse.class);
        bosatt.getBekreftedePerioder().forEach(p -> p.setBosattVurdering(true));
        saksbehandler.bekreftAksjonspunkt(bosatt);

        saksbehandler.bekreftAksjonspunktMedDefaultVerdier(ForeslåVedtakBekreftelse.class);

        beslutter.hentFagsak(saksnummer);
        FatterVedtakBekreftelse bekreftelse = beslutter.hentAksjonspunktbekreftelse(FatterVedtakBekreftelse.class);
        bekreftelse.godkjennAksjonspunkter(beslutter.hentAksjonspunktSomSkalTilTotrinnsBehandling());
        beslutter.fattVedtakOgVentTilAvsluttetBehandling(bekreftelse);

        assertThat(beslutter.valgtBehandling.hentBehandlingsresultat())
                .as("Behandlingsresultat")
                .isEqualTo(BehandlingResultatType.INNVILGET);
        Beregningsresultat beregningResultatEngangsstonad = beslutter.valgtBehandling.getBeregningResultatEngangsstonad();
        assertThat(beregningResultatEngangsstonad.getBeregnetTilkjentYtelse())
                .as("Beregnet tilkjent ytelse")
                .isPositive();
        assertThat(beregningResultatEngangsstonad.getAntallBarn())
                .as("Antall barn")
                .isEqualTo(2);
    }

    @Test
    @DisplayName("Mor søker fødsel med verge")
    @Description("Mor søker fødsel med verge - skal få aksjonspunkt om registrering av verge når man er under 18")
    void morSøkerFødselMedVerge() {
        TestscenarioDto testscenario = opprettTestscenario("54");
        var aktørID = testscenario.personopplysninger().søkerAktørIdent();
        var fødselsdato = testscenario.personopplysninger().fødselsdato();
        EngangstønadBuilder søknad = lagEngangstønadFødsel(aktørID, SøkersRolle.MOR, fødselsdato);
        long saksnummer = fordel.sendInnSøknad(søknad.build(), testscenario,
                DokumenttypeId.SØKNAD_ENGANGSSTØNAD_FØDSEL);

        saksbehandler.hentFagsak(saksnummer);

        AvklarFaktaVergeBekreftelse avklarFaktaVergeBekreftelse = saksbehandler
                .hentAksjonspunktbekreftelse(AvklarFaktaVergeBekreftelse.class);
        avklarFaktaVergeBekreftelse.bekreftSøkerErKontaktperson()
                .bekreftSøkerErIkkeUnderTvungenForvaltning()
                .setVerge(testscenario.personopplysninger().annenpartIdent());
        saksbehandler.bekreftAksjonspunkt(avklarFaktaVergeBekreftelse);

        AvklarBrukerHarGyldigPeriodeBekreftelse avklarBrukerHarGyldigPeriodeBekreftelse = saksbehandler
                .hentAksjonspunktbekreftelse(AvklarBrukerHarGyldigPeriodeBekreftelse.class);
        avklarBrukerHarGyldigPeriodeBekreftelse.setVurdering(
                MedlemskapManuellVurderingType.MEDLEM,
                saksbehandler.valgtBehandling.getMedlem().getMedlemskapPerioder());
        saksbehandler.bekreftAksjonspunkt(avklarBrukerHarGyldigPeriodeBekreftelse);
        var bosatt = saksbehandler.hentAksjonspunktbekreftelse(AvklarBrukerBosattBekreftelse.class);
        bosatt.getBekreftedePerioder().forEach(p -> p.setBosattVurdering(true));
        saksbehandler.bekreftAksjonspunkt(bosatt);

        saksbehandler.bekreftAksjonspunktMedDefaultVerdier(ForeslåVedtakBekreftelse.class);

        beslutter.hentFagsak(saksnummer);
        FatterVedtakBekreftelse bekreftelse = beslutter.hentAksjonspunktbekreftelse(FatterVedtakBekreftelse.class);
        bekreftelse.godkjennAksjonspunkter(beslutter.hentAksjonspunktSomSkalTilTotrinnsBehandling());
        beslutter.fattVedtakOgVentTilAvsluttetBehandling(bekreftelse);

        assertThat(beslutter.valgtBehandling.hentBehandlingsresultat())
                .as("Behandlingsresultat")
                .isEqualTo(BehandlingResultatType.INNVILGET);
    }

    @Test
    @DisplayName("Mor søker uregistrert fødsel mindre enn 14 dager etter fødsel")
    @Description("Mor søker uregistrert fødsel mindre enn 14 dager etter fødsel. Behandlingen skal bli satt på vent")
    void morSøkerUregistrertFødselMindreEnn14DagerEtter() {
        TestscenarioDto testscenario = opprettTestscenario("55");
        String aktørID = testscenario.personopplysninger().søkerAktørIdent();
        LocalDate fødselsdato = LocalDate.now().minusWeeks(1);

        EngangstønadBuilder søknad = lagEngangstønadFødsel(aktørID, SøkersRolle.MOR, fødselsdato);
        long saksnummer = fordel.sendInnSøknad(søknad.build(), testscenario,
                DokumenttypeId.SØKNAD_ENGANGSSTØNAD_FØDSEL);

        saksbehandler.hentFagsak(saksnummer);

        assertThat(saksbehandler.valgtBehandling.erSattPåVent()).as("behandling er ikke satt på vent").isTrue();
    }

    @Test
    @DisplayName("Medmor søker fødsel")
    @Description("Medmor søker fødsel - søkand blir avslått fordi søker er medmor")
    void medmorSøkerFødsel() {
        TestscenarioDto testscenario = opprettTestscenario("90");
        EngangstønadBuilder søknad = lagEngangstønadFødsel(
                testscenario.personopplysninger().søkerAktørIdent(),
                SøkersRolle.MEDMOR,
                testscenario.personopplysninger().fødselsdato());

        long saksnummer = fordel.sendInnSøknad(søknad.build(), testscenario,
                DokumenttypeId.SØKNAD_ENGANGSSTØNAD_FØDSEL);

        saksbehandler.hentFagsak(saksnummer);
        saksbehandler.ventTilAvsluttetBehandling();

        assertThat(saksbehandler.valgtBehandling.hentBehandlingsresultat())
                .as("Behandlingsresultat")
                .isEqualTo(BehandlingResultatType.AVSLÅTT);
        assertThat(saksbehandler.valgtBehandling.hentAvslagsarsak())
                .as("Avslagsårsak")
                .isEqualTo(Avslagsårsak.SØKER_ER_MEDMOR);
    }

}
