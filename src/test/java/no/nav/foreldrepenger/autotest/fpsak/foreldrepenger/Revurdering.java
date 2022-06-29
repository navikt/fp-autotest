package no.nav.foreldrepenger.autotest.fpsak.foreldrepenger;

import static no.nav.foreldrepenger.autotest.dokumentgenerator.foreldrepengesoknad.json.erketyper.FordelingErketyper.fordelingEndringssøknadGradering;
import static no.nav.foreldrepenger.autotest.dokumentgenerator.foreldrepengesoknad.json.erketyper.FordelingErketyper.generiskFordeling;
import static no.nav.foreldrepenger.autotest.dokumentgenerator.foreldrepengesoknad.json.erketyper.SøknadEndringErketyper.lagEndringssøknadFødsel;
import static no.nav.foreldrepenger.autotest.dokumentgenerator.foreldrepengesoknad.json.erketyper.SøknadForeldrepengerErketyper.lagSøknadForeldrepengerFødsel;
import static no.nav.foreldrepenger.autotest.dokumentgenerator.foreldrepengesoknad.json.erketyper.SøknadForeldrepengerErketyper.lagSøknadForeldrepengerTermin;
import static no.nav.foreldrepenger.autotest.dokumentgenerator.foreldrepengesoknad.json.erketyper.UttaksperioderErketyper.uttaksperiode;
import static no.nav.foreldrepenger.autotest.util.AllureHelper.debugFritekst;
import static no.nav.foreldrepenger.autotest.util.AllureHelper.debugLoggBehandling;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.Comparator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.qameta.allure.Description;
import no.nav.foreldrepenger.autotest.base.FpsakTestBase;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.Avslagsårsak;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.BehandlingResultatType;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.BehandlingStatus;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.BehandlingType;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.BehandlingÅrsakType;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.FagsakStatus;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.KonsekvensForYtelsen;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.PeriodeResultatType;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.PeriodeResultatÅrsak;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.FatterVedtakBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.ForeslåVedtakBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.ForeslåVedtakManueltBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.KontrollerManueltOpprettetRevurdering;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.VurderSoknadsfristForeldrepengerBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.overstyr.OverstyrMedlemskapsvilkaaret;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.uttak.UttakResultatPeriode;
import no.nav.foreldrepenger.autotest.klienter.fpsak.historikk.dto.HistorikkinnslagType;
import no.nav.foreldrepenger.autotest.util.AllureHelper;
import no.nav.foreldrepenger.autotest.util.localdate.Virkedager;
import no.nav.foreldrepenger.autotest.util.testscenario.modell.Familie;
import no.nav.foreldrepenger.common.domain.BrukerRolle;
import no.nav.foreldrepenger.common.domain.foreldrepenger.fordeling.StønadskontoType;

@Tag("fpsak")
@Tag("foreldrepenger")
class Revurdering extends FpsakTestBase {

    private static final Logger logger = LoggerFactory.getLogger(Revurdering.class);

    @Test
    @DisplayName("Revurdering opprettet manuelt av saksbehandler.")
    @Description("Førstegangsbehandling til positivt vedtak. Saksbehandler oppretter revurdering manuelt. " +
            "Overstyrer medlemskap. Vedtaket opphører.")
    void opprettRevurderingManuelt() {
        var familie = new Familie("50", fordel);
        var mor = familie.mor();
        var fødselsdato = familie.barn().fødselsdato();
        var fpStartdato = fødselsdato.minusWeeks(3);
        var søknad = lagSøknadForeldrepengerFødsel(fødselsdato, BrukerRolle.MOR)
                .medAnnenForelder(lagNorskAnnenforeldre(familie.far()));
        var saksnummer = mor.søk(søknad.build());

        var arbeidsgiver = mor.arbeidsgiver();
        arbeidsgiver.sendInntektsmeldingerFP(saksnummer, fpStartdato);

        saksbehandler.hentFagsak(saksnummer);
        AllureHelper.debugLoggBehandlingsliste(saksbehandler.behandlinger);
        saksbehandler.ventPåOgVelgFørstegangsbehandling();
        saksbehandler.ventTilAvsluttetBehandling();
        assertThat(saksbehandler.valgtBehandling.hentBehandlingsresultat())
                .as("Behandlingsresultat")
                .isEqualTo(BehandlingResultatType.INNVILGET);
        saksbehandler.opprettBehandlingRevurdering(BehandlingÅrsakType.RE_OPPLYSNINGER_OM_MEDLEMSKAP);

        overstyrer.hentFagsak(saksnummer);
        overstyrer.ventPåOgVelgRevurderingBehandling();
        var overstyrMedlemskapsvilkaaret = new OverstyrMedlemskapsvilkaaret()
                .avvis(Avslagsårsak.SØKER_ER_IKKE_MEDLEM)
                .setBegrunnelse("avvist");
        overstyrer.overstyr(overstyrMedlemskapsvilkaaret);
        overstyrer.bekreftAksjonspunktMedDefaultVerdier(KontrollerManueltOpprettetRevurdering.class);
        overstyrer.bekreftAksjonspunktMedDefaultVerdier(ForeslåVedtakBekreftelse.class);
        assertThat(overstyrer.valgtBehandling.behandlingsresultat.getType())
                .as("Behandlingsresultat")
                .isEqualTo(BehandlingResultatType.OPPHØR);

        beslutter.hentFagsak(saksnummer);
        beslutter.ventPåOgVelgRevurderingBehandling();
        var bekreftelse = beslutter.hentAksjonspunktbekreftelse(FatterVedtakBekreftelse.class);
        bekreftelse.godkjennAksjonspunkter(beslutter.hentAksjonspunktSomSkalTilTotrinnsBehandling());
        beslutter.fattVedtakOgVentTilAvsluttetBehandling(bekreftelse);

        assertThat(beslutter.valgtBehandling.behandlingsresultat.getType())
                .as("Behandlingsresultat")
                .isEqualTo(BehandlingResultatType.OPPHØR);
        assertThat(beslutter.valgtBehandling.hentAvslagsarsak())
                .as("Avslagsårsak")
                .isEqualTo(Avslagsårsak.SØKER_ER_IKKE_MEDLEM);
        assertThat(beslutter.valgtBehandling.status)
                .as("Behandlingsstatus")
                .isEqualTo(BehandlingStatus.AVSLUTTET);
        logger.info("Status på sak: {}", beslutter.valgtFagsak.status().getKode());
    }

    @Test
    @DisplayName("Endringssøknad med ekstra uttaksperiode.")
    @Description("Førstegangsbehandling til positivt vedtak. Søker sender inn endringsøknad. Endring i uttak. Vedtak fortsatt løpende.")
    void endringssøknad() {
        var familie = new Familie("50", fordel);
        var mor = familie.mor();
        var fødselsdato = familie.barn().fødselsdato();
        var fpStartdato = fødselsdato.minusWeeks(3);
        var søknad = lagSøknadForeldrepengerFødsel(fødselsdato, BrukerRolle.MOR)
                .medAnnenForelder(lagNorskAnnenforeldre(familie.far()));
        var saksnummer = mor.søk(søknad.build());

        var arbeidsgiver = mor.arbeidsgiver();
        arbeidsgiver.sendInntektsmeldingerFP(saksnummer, fpStartdato);

        saksbehandler.hentFagsak(saksnummer);
        saksbehandler.ventPåOgVelgFørstegangsbehandling();
        saksbehandler.ventTilHistorikkinnslag(HistorikkinnslagType.VEDLEGG_MOTTATT);
        debugLoggBehandling(saksbehandler.valgtBehandling);
        saksbehandler.ventTilAvsluttetBehandling();
        assertThat(saksbehandler.behandlinger)
                .as("Antall behandlinger")
                .hasSize(1);
        assertThat(saksbehandler.valgtBehandling.type)
                .as("Behandlingstype")
                .isEqualTo(BehandlingType.FØRSTEGANGSSØKNAD);
        saksbehandler.ventTilAvsluttetBehandling();
        debugFritekst("Ferdig med første behandling");

        // Endringssøknad
        var fordeling = generiskFordeling(
                uttaksperiode(StønadskontoType.FELLESPERIODE, fødselsdato.plusWeeks(8), fødselsdato.plusWeeks(10).minusDays(1)));
        var søknadE = lagEndringssøknadFødsel(fødselsdato, BrukerRolle.MOR, fordeling, saksnummer);
        var saksnummerE = mor.søk(søknadE.build());

        saksbehandler.hentFagsak(saksnummerE);
        saksbehandler.ventPåOgVelgRevurderingBehandling();
        saksbehandler.ventTilAvsluttetBehandling();
        assertThat(saksbehandler.valgtBehandling.behandlingsresultat.getType())
                .as("Behandlingsresultat")
                .isEqualTo(BehandlingResultatType.FORELDREPENGER_ENDRET);
        assertThat(saksbehandler.valgtBehandling.behandlingsresultat.getKonsekvenserForYtelsen())
                .as("konsekvensForYtelsen")
                .contains(KonsekvensForYtelsen.ENDRING_I_UTTAK);

        saksbehandler.hentFagsak(saksnummerE);
        assertThat(saksbehandler.valgtFagsak.status())
                .as("Fagsak stauts")
                .isEqualTo(FagsakStatus.LØPENDE);
    }

    @Test
    @DisplayName("Endringssøknad med gradering")
    @Description("Førstegangsbehandling til positivt vedtak. Endringssøknad med gradering fra bruker. Vedtak fortsatt løpende.")
    void endringssøknadMedGradering() {
        var familie = new Familie("50", fordel);
        var mor = familie.mor();
        var fødselsdato = familie.barn().fødselsdato();
        var fpStartdato = fødselsdato.minusWeeks(3);
        var søknad = lagSøknadForeldrepengerFødsel(fødselsdato, BrukerRolle.MOR)
                .medAnnenForelder(lagNorskAnnenforeldre(familie.far()));
        var saksnummer = mor.søk(søknad.build());

        var arbeidsgiver = mor.arbeidsgiver();
        arbeidsgiver.sendInntektsmeldingerFP(saksnummer, fpStartdato);

        saksbehandler.hentFagsak(saksnummer);
        saksbehandler.ventPåOgVelgFørstegangsbehandling();
        saksbehandler.ventTilAvsluttetBehandling();
        assertThat(saksbehandler.valgtBehandling.getUttakResultatPerioder().getPerioderSøker())
                .as("Antall uttaksperioder")
                .hasSize(4);

        // Endringssøknad
        var graderingFom = fødselsdato.plusWeeks(20);
        var graderingTom = fødselsdato.plusWeeks(23).minusDays(1);
        var arbeidsgiveridentifikator = arbeidsgiver.arbeidsgiverIdentifikator();
        var fordelingGradering = fordelingEndringssøknadGradering(StønadskontoType.FELLESPERIODE, graderingFom, graderingTom,
                arbeidsgiveridentifikator, 40);
        var endretSøknad = lagEndringssøknadFødsel(fødselsdato, BrukerRolle.MOR,
                fordelingGradering, saksnummer);
        var saksnummerE = mor.søk(endretSøknad.build());

        saksbehandler.hentFagsak(saksnummerE);
        saksbehandler.ventPåOgVelgRevurderingBehandling();
        saksbehandler.ventTilAvsluttetBehandling();
        assertThat(saksbehandler.valgtFagsak.status())
                .as("Fagsak stauts")
                .isEqualTo(FagsakStatus.LØPENDE);
        assertThat(saksbehandler.valgtBehandling.hentBehandlingsresultat())
                .as("Behandlingsresultat")
                .isEqualTo(BehandlingResultatType.FORELDREPENGER_ENDRET);
        assertThat(saksbehandler.valgtBehandling.behandlingsresultat.getKonsekvenserForYtelsen())
                .as("Konsekvenser for ytelse")
                .contains(KonsekvensForYtelsen.ENDRING_I_UTTAK);
        assertThat(saksbehandler.valgtBehandling.getUttakResultatPerioder().getPerioderSøker())
                .as("Antall uttaksperioder")
                .hasSize(5);
        for (UttakResultatPeriode periode : saksbehandler.valgtBehandling.getUttakResultatPerioder()
                .getPerioderSøker()) {
            assertThat(periode.getAktiviteter())
                    .as("Aktiviteter i uttaksperiode")
                    .hasSize(1);
        }
        assertThat(saksbehandler.valgtBehandling.getUttakResultatPerioder().getPerioderSøker().get(4).getGraderingInnvilget())
                .as("Uttaksperiode 4 er en graderingsperiode")
                .isTrue();
    }

    @Test
    @DisplayName("Ikke få avslåg på innvilget perioder pga søknadsfrist")
    @Description("Ikke få avslåg på innvilget perioder pga søknadsfrist.")
    void ikke_avslag_pa_innvilget_perioder_pga_søknadsfrist_i_revurdering() {
        var familie = new Familie("74", fordel);
        var mor = familie.mor();
        var fødselsdato = familie.barn().fødselsdato();
        var fpStartdato = fødselsdato.minusWeeks(3);
        var fordeling = generiskFordeling(
                uttaksperiode(StønadskontoType.FORELDREPENGER_FØR_FØDSEL, fpStartdato, fødselsdato.minusDays(1)),
                uttaksperiode(StønadskontoType.MØDREKVOTE, fødselsdato, fødselsdato.plusWeeks(13).minusDays(1)));
        var søknad = lagSøknadForeldrepengerFødsel(fødselsdato, BrukerRolle.MOR)
                .medFordeling(fordeling)
                .medAnnenForelder(lagNorskAnnenforeldre(familie.far()))
                .medMottatdato(fødselsdato.plusWeeks(9));
        var saksnummer = mor.søk(søknad.build());

        var arbeidsgiver = mor.arbeidsgiver();
        arbeidsgiver.sendInntektsmeldingerFP(saksnummer, fpStartdato);

        saksbehandler.hentFagsak(saksnummer);
        saksbehandler.ventTilAvsluttetBehandling();

        // Sender endringssøknad for å gi fagsaken en ny søknad mottatt dato
        var fordelingEndringssøknad = generiskFordeling(
                uttaksperiode(StønadskontoType.FELLESPERIODE, fødselsdato.plusWeeks(13), fødselsdato.plusWeeks(14).minusDays(1)));
        var søknadE = lagEndringssøknadFødsel(fødselsdato, BrukerRolle.MOR, fordelingEndringssøknad, saksnummer)
                .medMottattDato(fødselsdato.plusWeeks(10));
        mor.søk(søknadE.build());

        saksbehandler.velgSisteBehandling();
        saksbehandler.ventTilAvsluttetBehandling();

        // Manuell behandling for å få endringssdato satt til første uttaksdag
        saksbehandler.opprettBehandlingRevurdering(BehandlingÅrsakType.RE_OPPLYSNINGER_OM_FORDELING);
        saksbehandler.velgSisteBehandling();
        saksbehandler.bekreftAksjonspunktMedDefaultVerdier(KontrollerManueltOpprettetRevurdering.class);
        saksbehandler.bekreftAksjonspunktMedDefaultVerdier(ForeslåVedtakManueltBekreftelse.class);
        saksbehandler.ventTilAvsluttetBehandling();

        assertThat(saksbehandler.valgtBehandling.hentUttaksperioder())
                .as("Forventer at alle uttaksperioder er innvilget")
                .allMatch(p -> p.getPeriodeResultatType().equals(PeriodeResultatType.INNVILGET));
    }

    @Test
    @DisplayName("Fortsatt få avslag på avslåtte perioder pga søknadsfrist i neste revurdering")
    @Description("Fortsatt få avslag på avslåtte perioder pga søknadsfrist i neste revurdering. Bruker papirsøknad for å kunne sette mottatt dato tilbake i tid")
    void fortsatt_tape_avslåtte_perioder_pga_søknadsfrist_i_revurdering() {
        var familie = new Familie("74", fordel);
        var mor = familie.mor();
        var fødselsdato = familie.barn().fødselsdato();
        var fpStartdato = fødselsdato.minusWeeks(3);
        var fordeling = generiskFordeling(
                uttaksperiode(StønadskontoType.FORELDREPENGER_FØR_FØDSEL, fpStartdato, fødselsdato.minusDays(1)),
                uttaksperiode(StønadskontoType.MØDREKVOTE, fødselsdato, fødselsdato.plusWeeks(13).minusDays(1)));
        var søknad = lagSøknadForeldrepengerFødsel(fødselsdato, BrukerRolle.MOR)
                .medFordeling(fordeling)
                .medAnnenForelder(lagNorskAnnenforeldre(familie.far()))
                .medMottatdato(fødselsdato.plusWeeks(18));
        var saksnummer = mor.søk(søknad.build());

        var arbeidsgiver = mor.arbeidsgiver();
        arbeidsgiver.sendInntektsmeldingerFP(saksnummer, fpStartdato);

        saksbehandler.hentFagsak(saksnummer);

        var vurderSoknadsfristForeldrepengerBekreftelse = saksbehandler
                .hentAksjonspunktbekreftelse(VurderSoknadsfristForeldrepengerBekreftelse.class)
                .bekreftHarIkkeGyldigGrunn();
        saksbehandler.bekreftAksjonspunkt(vurderSoknadsfristForeldrepengerBekreftelse);
        saksbehandler.bekreftAksjonspunktMedDefaultVerdier(ForeslåVedtakBekreftelse.class);

        beslutter.hentFagsak(saksnummer);
        var bekreftelse = beslutter.hentAksjonspunktbekreftelse(FatterVedtakBekreftelse.class);
        bekreftelse.godkjennAksjonspunkter(beslutter.hentAksjonspunktSomSkalTilTotrinnsBehandling());
        beslutter.bekreftAksjonspunkt(bekreftelse);

        saksbehandler.ventTilAvsluttetBehandling();
        assertThat(saksbehandler.hentAvslåtteUttaksperioder())
                .as("Avslåtte uttaksperioder")
                .hasSizeGreaterThan(1);

        var fordelingEndringssøknad = generiskFordeling(
                uttaksperiode(StønadskontoType.FELLESPERIODE, fødselsdato.plusWeeks(13), fødselsdato.plusWeeks(12).plusWeeks(2)));
        var søknadE = lagEndringssøknadFødsel(fødselsdato, BrukerRolle.MOR, fordelingEndringssøknad, saksnummer)
                .medMottattDato(fødselsdato.plusWeeks(10));
        mor.søk(søknadE.build());

        saksbehandler.velgSisteBehandling();
        saksbehandler.ventTilAvsluttetBehandling();

        // Manuell behandling for å få endringssdato satt til første uttaksdag
        saksbehandler.opprettBehandlingRevurdering(BehandlingÅrsakType.RE_OPPLYSNINGER_OM_FORDELING);
        saksbehandler.velgSisteBehandling();
        assertThat(saksbehandler.hentAvslåtteUttaksperioder())
                .as("Avslåtte uttaksperioder")
                .hasSizeGreaterThan(1);
    }

    @Test
    @DisplayName("Mor innvilges for ny termin - revurder løpende FP")
    @Description("Mor har FP og får innvilget ny FP - revurder tidligste FP og avslå perioder inn i ny stønadsperiode")
    void revurder_fp_pga_innvilget_fp_nytt_barn() {
        // Barn 1 er 25 uker gammelt
        var familie = new Familie("74", fordel);
        var mor = familie.mor();
        var fødselsdato = familie.barn().fødselsdato();
        var fpStartdato = fødselsdato.minusWeeks(3);
        var fordeling = generiskFordeling(
                uttaksperiode(StønadskontoType.FORELDREPENGER_FØR_FØDSEL, fpStartdato, fødselsdato.minusDays(1)),
                uttaksperiode(StønadskontoType.MØDREKVOTE, fødselsdato, fødselsdato.plusWeeks(15).minusDays(1)),
                // Her har mor utsettet uttaket sitt i 20 uker.
                uttaksperiode(StønadskontoType.FELLESPERIODE, fødselsdato.plusWeeks(45), fødselsdato.plusWeeks(61).minusDays(1)));
        var søknad = lagSøknadForeldrepengerFødsel(fødselsdato, BrukerRolle.MOR)
                .medFordeling(fordeling)
                .medAnnenForelder(lagNorskAnnenforeldre(familie.far()))
                .medMottatdato(fødselsdato.minusWeeks(2));
        var saksnummer = mor.søk(søknad.build());

        var arbeidsgiver = mor.arbeidsgiver();
        arbeidsgiver.sendInntektsmeldingerFP(saksnummer, fpStartdato);

        saksbehandler.hentFagsak(saksnummer);
        saksbehandler.ventTilAvsluttetBehandling();



        // Barn 2: Før uke 48. Vi ønsker å ha noe igjen av minsteretten. Minsteretten til MOR er 15
        // Barn 2: Har termin 25 + 6 uker = 31 UKER
        //
        // Barn 2: Søker for barn 2 med termin om 6 uker og blir innvilget med start om 3 uker.

        var termindato = Virkedager.helgejustertTilMandag(LocalDate.now().plusWeeks(6));
        var søknadFP = lagSøknadForeldrepengerTermin(termindato, BrukerRolle.MOR)
                .medAnnenForelder(lagNorskAnnenforeldre(familie.far()));
        var saksnummerFP = mor.søk(søknadFP.build());
        arbeidsgiver.sendInntektsmeldingerFP(saksnummerFP, termindato.minusWeeks(3));

        saksbehandler.hentFagsak(saksnummerFP);
        saksbehandler.ventTilAvsluttetBehandling();

        // Barn 1: Revurdering skal avslå siste uttaksperiode med rett årsak
        saksbehandler.hentFagsak(saksnummer);
        saksbehandler.ventPåOgVelgRevurderingBehandling();
        saksbehandler.ventTilAvsluttetBehandling();

        //assertThat(saksbehandler.valgtBehandling.hentBehandlingsresultat()).isEqualTo(BehandlingResultatType.OPPHØR);
        var sisteUttaksperiode = saksbehandler.valgtBehandling.getUttakResultatPerioder().getPerioderSøker().stream()
                .max(Comparator.comparing(UttakResultatPeriode::getFom))
                .orElseThrow();
        assertThat(sisteUttaksperiode.getFom())
                .as("Siste periode knekt ved startdato ny sak")
                .isEqualTo(termindato.minusWeeks(3));
        assertThat(sisteUttaksperiode.getPeriodeResultatÅrsak())
                .as("Siste periode avslått med årsak ny stønadsperiode")
                .isEqualTo(PeriodeResultatÅrsak.STØNADSPERIODE_NYTT_BARN);

    }

}



