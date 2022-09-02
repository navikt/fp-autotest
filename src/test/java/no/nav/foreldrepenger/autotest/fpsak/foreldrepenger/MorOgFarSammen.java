package no.nav.foreldrepenger.autotest.fpsak.foreldrepenger;

import static no.nav.foreldrepenger.autotest.dokumentgenerator.foreldrepengesoknad.json.erketyper.FordelingErketyper.generiskFordeling;
import static no.nav.foreldrepenger.autotest.dokumentgenerator.foreldrepengesoknad.json.erketyper.SøknadEndringErketyper.lagEndringssøknadFødsel;
import static no.nav.foreldrepenger.autotest.dokumentgenerator.foreldrepengesoknad.json.erketyper.SøknadForeldrepengerErketyper.lagSøknadForeldrepengerFødsel;
import static no.nav.foreldrepenger.autotest.dokumentgenerator.foreldrepengesoknad.json.erketyper.UttaksperiodeType.SAMTIDIGUTTAK;
import static no.nav.foreldrepenger.autotest.dokumentgenerator.foreldrepengesoknad.json.erketyper.UttaksperioderErketyper.utsettelsesperiode;
import static no.nav.foreldrepenger.autotest.dokumentgenerator.foreldrepengesoknad.json.erketyper.UttaksperioderErketyper.uttaksperiode;
import static no.nav.foreldrepenger.autotest.domain.foreldrepenger.BehandlingÅrsakType.BERØRT_BEHANDLING;
import static no.nav.foreldrepenger.autotest.domain.foreldrepenger.BehandlingÅrsakType.RE_ENDRING_FRA_BRUKER;
import static no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.uttak.Saldoer.SaldoVisningStønadskontoType.FELLESPERIODE;
import static no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.uttak.Saldoer.SaldoVisningStønadskontoType.FORELDREPENGER_FØR_FØDSEL;
import static no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.uttak.Saldoer.SaldoVisningStønadskontoType.MØDREKVOTE;
import static no.nav.foreldrepenger.autotest.util.AllureHelper.debugFritekst;
import static no.nav.foreldrepenger.autotest.util.localdate.Virkedager.helgejustertTilMandag;
import static no.nav.foreldrepenger.common.domain.foreldrepenger.fordeling.StønadskontoType.FEDREKVOTE;
import static no.nav.foreldrepenger.common.domain.foreldrepenger.fordeling.UtsettelsesÅrsak.FRI;
import static no.nav.foreldrepenger.common.domain.foreldrepenger.fordeling.UtsettelsesÅrsak.SYKDOM;
import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.qameta.allure.Description;
import io.qameta.allure.Step;
import no.nav.foreldrepenger.autotest.base.FpsakTestBase;
import no.nav.foreldrepenger.autotest.dokumentgenerator.foreldrepengesoknad.json.erketyper.FordelingErketyper;
import no.nav.foreldrepenger.autotest.dokumentgenerator.foreldrepengesoknad.json.erketyper.SøknadForeldrepengerErketyper;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.Avslagsårsak;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.BehandlingResultatType;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.BehandlingStatus;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.BehandlingÅrsakType;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.KonsekvensForYtelsen;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.PeriodeResultatType;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.FatterVedtakBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.ForeslåVedtakBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.ForeslåVedtakManueltBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.KontrollerManueltOpprettetRevurdering;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.VurderManglendeFodselBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.VurderSoknadsfristForeldrepengerBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.avklarfakta.AvklarBrukerBosattBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.avklarfakta.AvklarFaktaUttakBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.overstyr.OverstyrFodselsvilkaaret;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.Aksjonspunkt;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.AksjonspunktKoder;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.BehandlingÅrsak;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.uttak.Saldoer;
import no.nav.foreldrepenger.autotest.klienter.fpsak.fagsak.dto.FagsakStatus;
import no.nav.foreldrepenger.autotest.util.localdate.Virkedager;
import no.nav.foreldrepenger.autotest.util.testscenario.modell.Familie;
import no.nav.foreldrepenger.common.domain.BrukerRolle;
import no.nav.foreldrepenger.common.domain.Saksnummer;
import no.nav.foreldrepenger.common.domain.foreldrepenger.fordeling.StønadskontoType;

@Tag("fpsak")
@Tag("foreldrepenger")
class MorOgFarSammen extends FpsakTestBase {

    private final Logger LOG = LoggerFactory.getLogger(MorOgFarSammen.class);


    @Test
    @DisplayName("Mor og far koblet sak, kant til kant")
    @Description("Mor søker, får aksjonspunkt slik at behandling stopper opp. " +
            "Far sender deretter søknad med uttak ifm fødsel og blir satt på vent pga manglende inntektsmelding." +
            "Behandler ferdig mor sin søknad (positivt vedtak). Behandler deretter far sin søknad (positivt og automatisk vedtak). "+
            "Ingen overlapp, med unntak av de to ukene ifm med fødsel. Verifiserer at sakene er koblet og at det ikke opprettes revurdering berørt sak.")
    void morOgFar_fødsel_ettArbeidsforholdHver_kobletsak_kantTilKant() {
        var familie = new Familie("82", fordel);
        var mor = familie.mor();
        var fødselsdato = familie.barn().fødselsdato();
        var fpstartdatoMor = fødselsdato.minusWeeks(3);
        var fordelingMor = generiskFordeling(
                uttaksperiode(StønadskontoType.FORELDREPENGER_FØR_FØDSEL, fpstartdatoMor, fødselsdato.minusDays(1)),
                utsettelsesperiode(SYKDOM, fødselsdato, fødselsdato.plusWeeks(6).minusDays(1)));
        var søknad = lagSøknadForeldrepengerFødsel(fødselsdato, BrukerRolle.MOR)
                .medFordeling(fordelingMor)
                .medAnnenForelder(lagNorskAnnenforeldre(familie.far()));
        var saksnummerMor = mor.søk(søknad.build());
        mor.arbeidsgiver().sendInntektsmeldingerFP(saksnummerMor, fpstartdatoMor);

        saksbehandler.hentFagsak(saksnummerMor);
        saksbehandler.harAksjonspunkt("5070");
        assertThat(saksbehandler.valgtFagsak.status())
                .as("Fagsak status")
                .isEqualTo(FagsakStatus.UNDER_BEHANDLING);

        var far = familie.far();
        var fordeling = generiskFordeling(
                uttaksperiode(FEDREKVOTE, fødselsdato, fødselsdato.plusWeeks(2).minusDays(1), SAMTIDIGUTTAK),
                uttaksperiode(FEDREKVOTE, fødselsdato.plusWeeks(6), fødselsdato.plusWeeks(8).minusDays(1)));
        var søknadFar = SøknadForeldrepengerErketyper.lagSøknadForeldrepengerTerminFødsel(fødselsdato, BrukerRolle.FAR)
                .medFordeling(fordeling)
                .medAnnenForelder(lagNorskAnnenforeldre(familie.mor()));
        var saksnummerFar = far.søk(søknadFar.build());
        saksbehandler.hentFagsak(saksnummerFar);
        assertThat(saksbehandler.valgtFagsak.status())
                .as("Fagsak status")
                .isEqualTo(FagsakStatus.UNDER_BEHANDLING);
        assertThat(saksbehandler.valgtBehandling.erSattPåVent())
                .as("Behandling er satt på vent")
                .isTrue();

        // Behandle ferdig mor sin sak
        saksbehandler.hentFagsak(saksnummerMor);
        var avklarFaktaUttak = saksbehandler
                .hentAksjonspunktbekreftelse(AvklarFaktaUttakBekreftelse.AvklarFaktaUttakPerioder.class)
                .sykdomErDokumentertForPeriode();
        saksbehandler.bekreftAksjonspunkt(avklarFaktaUttak);
        saksbehandler.bekreftAksjonspunktMedDefaultVerdier(ForeslåVedtakBekreftelse.class);
        beslutter.hentFagsak(saksnummerMor);
        var bekreftelse = beslutter.hentAksjonspunktbekreftelse(FatterVedtakBekreftelse.class);
        bekreftelse.godkjennAksjonspunkter(beslutter.hentAksjonspunktSomSkalTilTotrinnsBehandling());
        beslutter.fattVedtakOgVentTilAvsluttetBehandling(bekreftelse);
        beslutter.ventTilFagsakLøpende();
        assertThat(beslutter.valgtBehandling.hentBehandlingsresultat())
                .as("Behandlingsresultat")
                .isEqualTo(BehandlingResultatType.INNVILGET);
        debugFritekst("Ferdig med behandling mor");

        // Behandle ferdig far sin sak
        var arbeidsgivere = far.arbeidsgivere();
        arbeidsgivere.sendDefaultInnteksmeldingerSVP(saksnummerFar);
        saksbehandler.hentFagsak(saksnummerFar);
        assertThat(saksbehandler.sakErKobletTilAnnenpart())
                .as("Saken er koblet til en annen behandling")
                .isTrue();
        saksbehandler.ventTilAvsluttetBehandlingOgFagsakLøpendeEllerAvsluttet();
        assertThat(saksbehandler.valgtBehandling.hentBehandlingsresultat())
                .as("Behandlingsresultat")
                .isEqualTo(BehandlingResultatType.INNVILGET);
        debugFritekst("Ferdig med behandling far");

        // Verifisere at det ikke er blitt opprettet revurdering berørt sak på mor
        saksbehandler.hentFagsak(saksnummerMor);
        assertThat(saksbehandler.behandlinger)
                .as("Antall behandlinger")
                .hasSize(1);
    }

    @Test
    @DisplayName("Mor og far koblet sak, mors endringssøknad sniker")
    @Description("Mor sin endringssøknad sniker i køen, når far sin behandling venter på IM. " +
            "Dette skal ikke føre til at far mister til periode som overlapper med mors førstegangssøknad. " +
            "Mor søker om perioden på nytt får å ta tilbake den tapte perioden.")
    void far_skal_ikke_miste_perioder_til_mor_ved_sniking() {
        var familie = new Familie("82", fordel);
        var mor = familie.mor();
        var far = familie.far();
        var fødselsdato = familie.barn().fødselsdato();
        var periodeFomBasedato = Virkedager.helgejustertTilMandag(fødselsdato);
        var periodeTomBasedato = Virkedager.helgejustertTilFredag(fødselsdato.minusDays(1));
        var fpStartdatoMor = periodeFomBasedato.minusWeeks(3);
        var fomFellesperiodeMorFørstegangssøknad = periodeFomBasedato.plusWeeks(6);
        var tomFellesperiodeMorFørstegangssøknad = periodeTomBasedato.plusWeeks(7);
        var fordelingMor = generiskFordeling(
                uttaksperiode(StønadskontoType.FORELDREPENGER_FØR_FØDSEL, fpStartdatoMor, periodeTomBasedato),
                uttaksperiode(StønadskontoType.MØDREKVOTE, periodeFomBasedato, periodeTomBasedato.plusWeeks(6)),
                uttaksperiode(StønadskontoType.FELLESPERIODE, fomFellesperiodeMorFørstegangssøknad, tomFellesperiodeMorFørstegangssøknad));
        var søknadMor = SøknadForeldrepengerErketyper.lagSøknadForeldrepengerTerminFødsel(fødselsdato, BrukerRolle.MOR)
                .medFordeling(fordelingMor)
                .medAnnenForelder(lagNorskAnnenforeldre(far))
                .medMottatdato(fødselsdato);
        var morSaksnummer = mor.søk(søknadMor.build());
        var arbeidsgiver = mor.arbeidsgiver();
        arbeidsgiver.sendInntektsmeldingerFP(morSaksnummer, fpStartdatoMor);
        saksbehandler.hentFagsak(morSaksnummer);
        saksbehandler.ventTilAvsluttetBehandlingOgFagsakLøpendeEllerAvsluttet();

        var fpStartdatoFar = periodeFomBasedato.minusWeeks(2);
        var søknadFar = SøknadForeldrepengerErketyper.lagSøknadForeldrepengerTerminFødsel(fødselsdato, BrukerRolle.FAR)
                .medFordeling(generiskFordeling(
                        uttaksperiode(FEDREKVOTE, fpStartdatoFar, periodeTomBasedato, SAMTIDIGUTTAK),
                        uttaksperiode(
                                FEDREKVOTE, fomFellesperiodeMorFørstegangssøknad, fomFellesperiodeMorFørstegangssøknad.plusWeeks(6).minusDays(1))))
                .medAnnenForelder(lagNorskAnnenforeldre(familie.mor()))
                .medMottatdato(fødselsdato.plusWeeks(1));
        var farSaksnummer = far.søk(søknadFar.build());

        saksbehandler.hentFagsak(farSaksnummer);
        saksbehandler.ventPåOgVelgÅpenFørstegangsbehandling();
        saksbehandler.harBehandlingsstatus(BehandlingStatus.UTREDES);

        /*
            Mor utvider fellesperioden sin med 1 uke mens far sin behandling er på vent pga manglende inntektsmelding
            Mor sniker i køen. Far skal ikke miste perioder som overlapper med mor sin førstegangssøknad.
        */
        var fomEndringssøknad1Mor = periodeFomBasedato.plusWeeks(7);
        var tomEndringssøknad1Mor = periodeTomBasedato.plusWeeks(8);
        var fordeling1 = generiskFordeling(
                uttaksperiode(StønadskontoType.FELLESPERIODE, fomEndringssøknad1Mor, tomEndringssøknad1Mor));
        var endringssøknadMor = lagEndringssøknadFødsel(
                fødselsdato,
                BrukerRolle.MOR,
                fordeling1,
                morSaksnummer)
                .medMottattDato(fødselsdato.plusWeeks(2));
        mor.søk(endringssøknadMor.build());

        saksbehandler.hentFagsak(morSaksnummer);
        saksbehandler.ventPåOgVelgRevurderingBehandling();
        saksbehandler.ventTilAvsluttetBehandlingOgFagsakLøpendeEllerAvsluttet();
        assertThat(saksbehandler.hentAvslåtteUttaksperioder()).isEmpty();

        /*
            Arbeidsgiver til far sender IM. Far sin sak ferdigbehandles automatisk.
            Far eller mor har ikke søkt om samtidig uttak og vi vil dermed avslå enten far eller mor sin periode i det tidsrommet.
            1)  Far skal ikke få avslag på periodene som overlapper med mor sin førstegangsbehandling siden far sin søknad ble
                sendt etter mors førstegangsøknad. Det skal opprettes berørt behandling på mor som avslår denne perioden.
            2)  Tilsvarende skal far få avslag på perioden som mor har søkt om i revurderingen sin.
         */
        far.arbeidsgivere().sendDefaultInntektsmeldingerFP(farSaksnummer, fødselsdato.minusWeeks(2));
        saksbehandler.hentFagsak(farSaksnummer);
        saksbehandler.ventTilAvsluttetBehandlingOgFagsakLøpendeEllerAvsluttet();
        var avslåttUttaksperiode = saksbehandler.hentAvslåtteUttaksperioder();
        assertThat(avslåttUttaksperiode).hasSize(1);
        assertThat(avslåttUttaksperiode.get(0).getFom()).isEqualTo(fomEndringssøknad1Mor);
        assertThat(avslåttUttaksperiode.get(0).getTom()).isEqualTo(tomEndringssøknad1Mor);

        // Mor's uttak
        saksbehandler.hentFagsak(morSaksnummer);
        saksbehandler.ventPåOgVelgRevurderingBehandling(BERØRT_BEHANDLING);
        assertThat(saksbehandler.hentAvslåtteUttaksperioder()).hasSize(1);
        assertThat(saksbehandler.hentAvslåtteUttaksperioder().get(0).getFom()).isEqualTo(fomFellesperiodeMorFørstegangssøknad);
        assertThat(saksbehandler.hentAvslåtteUttaksperioder().get(0).getTom()).isEqualTo(tomFellesperiodeMorFørstegangssøknad);

        /*
            Mor søker om perioden som ble avslått siden far søkte siste og ingen hadde søkt samtidig uttak.
            Mor vil dermed stjele perioden tilbake fra far. Fører til at begge periodene til far blir avlått.
         */
        var fordeling2 = generiskFordeling(
                uttaksperiode(StønadskontoType.FELLESPERIODE, fomFellesperiodeMorFørstegangssøknad, tomEndringssøknad1Mor));
        var endringssøknadMor2 = lagEndringssøknadFødsel(
                fødselsdato,
                BrukerRolle.MOR,
                fordeling2,
                morSaksnummer)
                .medMottattDato(fødselsdato.plusWeeks(3));
        mor.søk(endringssøknadMor2.build());

        saksbehandler.ventPåOgVelgRevurderingBehandling(RE_ENDRING_FRA_BRUKER, 2);
        saksbehandler.ventTilAvsluttetBehandlingOgFagsakLøpendeEllerAvsluttet();
        assertThat(saksbehandler.hentAvslåtteUttaksperioder()).isEmpty();

        saksbehandler.hentFagsak(farSaksnummer);
        saksbehandler.ventPåOgVelgRevurderingBehandling(BERØRT_BEHANDLING);
        var avslåttePerioderFarRevurdering = saksbehandler.hentAvslåtteUttaksperioder();
        assertThat(avslåttePerioderFarRevurdering).hasSize(1);
        assertThat(avslåttePerioderFarRevurdering.get(0).getFom()).isEqualTo(fomFellesperiodeMorFørstegangssøknad);
    }

    @Test
    @DisplayName("Far og mor søker fødsel med overlappende uttaksperiode")
    @Description("Mor søker og får innvilget. Far søker med to uker overlapp med mor (stjeling) og ifm fødsel. Far får innvilget. " +
            "Berørt sak opprettes på mor. Siste periode blir spittet i to og siste del blir avlsått. Det opprettes ikke" +
            "berørt sak på far.")
    void farOgMor_fødsel_ettArbeidsforholdHver_overlappendePeriode() {
        var familie = new Familie("82", fordel);
        var mor = familie.mor();
        var fødselsdato = familie.barn().fødselsdato();
        var periodeFomBasedato = Virkedager.helgejustertTilMandag(fødselsdato);
        var periodeTomBasedato = Virkedager.helgejustertTilFredag(fødselsdato.minusDays(1));
        var fpstartdatoMor = fødselsdato.minusWeeks(3);
        var fpStartdatoFar = fødselsdato.plusWeeks(8);

        // MOR
        var fordelingMor = generiskFordeling(
                uttaksperiode(StønadskontoType.FORELDREPENGER_FØR_FØDSEL, periodeFomBasedato.minusWeeks(3), periodeTomBasedato),
                uttaksperiode(StønadskontoType.MØDREKVOTE, periodeFomBasedato, periodeTomBasedato.plusWeeks(10)));
        var søknadMor = lagSøknadForeldrepengerFødsel(fødselsdato, BrukerRolle.MOR)
                .medFordeling(fordelingMor)
                .medAnnenForelder(lagNorskAnnenforeldre(familie.far()))
                .medMottatdato(fødselsdato.minusWeeks(3));
        var saksnummerMor = mor.søk(søknadMor.build());

        var arbeidsgivere = mor.arbeidsgivere();
        arbeidsgivere.sendDefaultInntektsmeldingerFP(saksnummerMor, fpstartdatoMor);

        saksbehandler.hentFagsak(saksnummerMor);
        saksbehandler.ventTilAvsluttetBehandlingOgFagsakLøpendeEllerAvsluttet();
        debugFritekst("Ferdig med første behandling mor");
        assertThat(saksbehandler.valgtBehandling.hentBehandlingsresultat())
                .as("Behandlingsresultat")
                .isEqualTo(BehandlingResultatType.INNVILGET);
        assertThat(saksbehandler.valgtBehandling.getUttakResultatPerioder().getPerioderSøker())
                .as("Antall uttkasperioder for søker")
                .hasSize(3);
        assertThat(saksbehandler.valgtBehandling.getSaldoer().stonadskontoer())
                .as("Antall stønadskontoer i saldo")
                .hasSize(4);

        // FAR
        var far = familie.far();
        var fordelingFar  = generiskFordeling(
                uttaksperiode(FEDREKVOTE, periodeFomBasedato, periodeTomBasedato.plusWeeks(2), SAMTIDIGUTTAK),
                uttaksperiode(FEDREKVOTE, periodeFomBasedato.plusWeeks(8), periodeTomBasedato.plusWeeks(12)));
        var søknadFar = SøknadForeldrepengerErketyper.lagSøknadForeldrepengerTerminFødsel(fødselsdato, BrukerRolle.FAR)
                .medFordeling(fordelingFar)
                .medAnnenForelder(lagNorskAnnenforeldre(familie.mor()))
                .medMottatdato(fødselsdato);
        var saksnummerFar = far.søk(søknadFar.build());

        far.arbeidsgivere().sendDefaultInntektsmeldingerFP(saksnummerFar, fpStartdatoFar);

        saksbehandler.hentFagsak(saksnummerFar);
        saksbehandler.ventTilAvsluttetBehandlingOgFagsakLøpendeEllerAvsluttet();
        debugFritekst("Ferdig med første behandling til far");
        assertThat(saksbehandler.valgtBehandling.hentBehandlingsresultat())
                .as("Behandlingsresultat")
                .isEqualTo(BehandlingResultatType.INNVILGET);
        assertThat(saksbehandler.sakErKobletTilAnnenpart())
                .as("Sak er koblet til mor sin behandling")
                .isTrue();
        assertThat(saksbehandler.hentAvslåtteUttaksperioder())
                .as("Antall avslåtte uttaksperioder")
                .isEmpty();

        // Revurdering berørt sak mor
        saksbehandler.hentFagsak(saksnummerMor);
        saksbehandler.ventPåOgVelgRevurderingBehandling();
        debugFritekst("Revurdering berørt sak opprettet på mor.");
        assertThat(saksbehandler.sakErKobletTilAnnenpart())
                .as("Saken er koblet til annenpart")
                .isTrue();
        assertThat(saksbehandler.hentAvslåtteUttaksperioder())
                .as("Antall avslåtte uttaksperioder")
                .hasSize(1);
        assertThat(saksbehandler.hentAvslåtteUttaksperioder().get(0).getFom()).isEqualTo(periodeFomBasedato.plusWeeks(8));
        assertThat(saksbehandler.valgtBehandling.getSaldoer().stonadskontoer().get(Saldoer.SaldoVisningStønadskontoType.FEDREKVOTE).saldo())
                .as("Saldo fro stønadskonto FEDREKVOTE")
                .isPositive();
        assertThat(saksbehandler.valgtBehandling.getSaldoer().stonadskontoer().get(MØDREKVOTE).saldo())
                .as("Saldo fro stønadskonto MØDREKVOTE")
                .isPositive();
        assertThat(saksbehandler.valgtBehandling.getSaldoer().stonadskontoer().get(FELLESPERIODE).saldo())
                .as("Saldo fro stønadskonto FELLESPERIODE")
                .isEqualTo(80);
        assertThat(saksbehandler.valgtBehandling.getSaldoer().stonadskontoer().get(FORELDREPENGER_FØR_FØDSEL).saldo())
                .as("Saldo fro stønadskonto FORELDREPENGER_FØR_FØDSEL")
                .isNotNegative();

        // FAR: Verifiser at far ikke får berørt behandling
        saksbehandler.hentFagsak(saksnummerFar);
        assertThat(saksbehandler.harRevurderingBehandling()).isFalse();
        assertThat(saksbehandler.hentAlleBehandlingerForFagsak(saksnummerFar))
                .as("Antall behandlinger")
                .hasSize(1);
    }

    @Test
    @DisplayName("Koblet sak endringssøknad ingen endring")
    @Description("Sender inn søknad mor. Sender inn søknad far uten overlapp. Sender inn endringssøknad mor som er lik " +
                "førstegangsbehandlingen. Verifiserer at det ikke blir berørt sak på far.")
    void kobletSakIngenEndring() {
        var familie = new Familie("84", fordel);
        var fødselsdato = LocalDate.now().minusMonths(4);
        var saksnummerMor = behandleSøknadForMorUtenOverlapp(familie, fødselsdato);
        var saksnummerFar = behandleSøknadForFarUtenOverlapp(familie, fødselsdato);

        saksbehandler.hentFagsak(saksnummerMor);
        assertThat(saksbehandler.harRevurderingBehandling())
                .as("Har revurdert behandling")
                .isFalse();

        sendInnEndringssøknadforMor(familie, saksnummerMor);

        saksbehandler.hentFagsak(saksnummerMor);
        saksbehandler.ventPåOgVelgRevurderingBehandling();
        saksbehandler.ventTilAvsluttetBehandlingOgFagsakLøpendeEllerAvsluttet();

        assertThat(saksbehandler.valgtBehandling.hentBehandlingsresultat())
                .as("Behandlingsresultat")
                .isEqualTo(BehandlingResultatType.INGEN_ENDRING);

        saksbehandler.hentFagsak(saksnummerFar);
        assertThat(saksbehandler.harRevurderingBehandling())
                .as("Har revurdert behandling (Fars behandling fikk revurdering selv uten endringer i mors behandling av endringssøknaden)")
                .isFalse();
    }

    @Test
    @DisplayName("Koblet sak. Far utsetter alt/gir fra seg alt. Far ny 1gang")
    @Description("Mor sender inn førstegangssøknad som ferdigbehandles før far sender inn søknad uten overlapp. "
            + "Far sin sak ferdigbehandles. Far tar deretter å sender inn en endringssøknad for å gi fra seg alle "
            + "innvilgede periodene. Etter dette så sender far inn en ny førstegangssøknad, med senere start.")
    void kobletSakFarUtsetterAlt() {
        var fødselsdato = helgejustertTilMandag(LocalDate.now().minusMonths(4));
        var farOpprinneligStartdato = fødselsdato.plusWeeks(10).plusDays(1);
        var farUtsattStartDato = fødselsdato.plusWeeks(20).plusDays(1);

        var familie = new Familie("84", fordel);
        var saksnummerMor = behandleSøknadForMorUtenOverlapp(familie, fødselsdato); // fødselsdato-3w -> fødselsdato+10w
        var saksnummerFar = behandleSøknadForFarUtenOverlapp(familie, fødselsdato, farOpprinneligStartdato); // fødselsdato+10w1d -> fødselsdato+12w1d
        saksbehandler.hentFagsak(saksnummerFar);
        assertThat(saksbehandler.harRevurderingBehandling())
                .as("Har revurdert behandling")
                .isFalse();

        // Endringssøknad som sier opp innvilget uttak fra start
        var fordelingFrasiPerioder = generiskFordeling(utsettelsesperiode(FRI, farOpprinneligStartdato, farOpprinneligStartdato.plusWeeks(2)));
        var søknad = lagEndringssøknadFødsel(fødselsdato, BrukerRolle.FAR, fordelingFrasiPerioder, saksnummerFar);
        familie.far().søk(søknad.build());

        saksbehandler.hentFagsak(saksnummerMor);
        saksbehandler.velgSisteBehandling();
        saksbehandler.ventTilAvsluttetBehandlingOgFagsakLøpendeEllerAvsluttet();

        saksbehandler.hentFagsak(saksnummerFar);
        saksbehandler.ventPåOgVelgRevurderingBehandling();
        saksbehandler.ventTilAvsluttetBehandlingOgFagsakLøpendeEllerAvsluttet();

        // Har fått tømt uttaket
        assertThat(saksbehandler.valgtBehandling.hentBehandlingsresultat())
                .as("Behandlingsresultat")
                .isEqualTo(BehandlingResultatType.FORELDREPENGER_SENERE);

        // Sender ny førstegangssøknad
        behandleSøknadForFarUtenOverlapp(familie, fødselsdato, farUtsattStartDato, saksnummerFar);
    }

    @Test
    @DisplayName("Koblet sak. Far utsetter oppstart rundt fødsel")
    @Description("Mor søker og får innvilget. Far søker og får innvilget rundt fødsel. Far søker utsatt oppstart "
            + "og blir innvilget uten berørt behandling hos mor.")
    void farUtsetterOppstartRundtFødsel() {
        var familie = new Familie("82", fordel);
        var fødselsdato = familie.barn().fødselsdato();

        var far = familie.far();
        var søknadMor = SøknadForeldrepengerErketyper.lagSøknadForeldrepengerTerminFødsel(fødselsdato, BrukerRolle.MOR)
                .medFordeling(FordelingErketyper.fordelingMorHappyCase(fødselsdato))
                .medAnnenForelder(lagNorskAnnenforeldre(far));
        var mor = familie.mor();
        var saksnummerMor = mor.søk(søknadMor.build());
        mor.arbeidsgiver().sendInntektsmeldingerFP(saksnummerMor, fødselsdato.minusWeeks(3));
        saksbehandler.hentFagsak(saksnummerMor);
        saksbehandler.ventTilAvsluttetBehandlingOgFagsakLøpendeEllerAvsluttet();

        var søknadFar = SøknadForeldrepengerErketyper.lagSøknadForeldrepengerTerminFødsel(fødselsdato, BrukerRolle.FAR)
                .medFordeling(generiskFordeling(uttaksperiode(FEDREKVOTE, fødselsdato, fødselsdato.plusWeeks(2).minusDays(1),
                        SAMTIDIGUTTAK)))
                .medAnnenForelder(lagNorskAnnenforeldre(familie.mor()));
        var saksnummerFar = far.søk(søknadFar.build());
        far.arbeidsgiver().sendInntektsmeldingerFP(saksnummerFar, fødselsdato);
        saksbehandler.hentFagsak(saksnummerFar);
        saksbehandler.ventTilAvsluttetBehandlingOgFagsakLøpendeEllerAvsluttet();

        var endringssøknad = lagEndringssøknadFødsel(fødselsdato, BrukerRolle.FAR,
                generiskFordeling(
                        utsettelsesperiode(FRI, fødselsdato, fødselsdato.plusWeeks(1).minusDays(1)),
                        uttaksperiode(FEDREKVOTE, fødselsdato.plusWeeks(1), fødselsdato.plusWeeks(3).minusDays(1), SAMTIDIGUTTAK)
                ),
                saksnummerFar);
        far.søk(endringssøknad.build());

        saksbehandler.ventPåOgVelgRevurderingBehandling(RE_ENDRING_FRA_BRUKER);
        saksbehandler.ventTilAvsluttetBehandlingOgFagsakLøpendeEllerAvsluttet();
        var uttak = saksbehandler.valgtBehandling.hentUttaksperioder();
        assertThat(uttak).hasSize(1);
        assertThat(uttak.get(0).getPeriodeResultatType()).isEqualTo(PeriodeResultatType.INNVILGET);
        assertThat(uttak.get(0).getAktiviteter().get(0).getStønadskontoType()).isEqualTo(FEDREKVOTE);
        assertThat(uttak.get(0).getAktiviteter().get(0).getUtbetalingsgrad()).isEqualTo(BigDecimal.valueOf(100));

        saksbehandler.hentFagsak(saksnummerMor);
        saksbehandler.ventTilAvsluttetBehandlingOgFagsakLøpendeEllerAvsluttet();
        assertThat(saksbehandler.valgtBehandling.getBehandlingÅrsaker().stream().map(BehandlingÅrsak::getBehandlingArsakType))
                .doesNotContain(BERØRT_BEHANDLING);
    }

    @Test
    @DisplayName("Koblet sak. Far utsetter fra start med senere uttaksdato")
    @Description("Sender inn søknad mor. Sender inn søknad far uten overlapp. Sender inn endringssøknad far med " +
            "fri utsettelse og uttaksperioder med start senere. Sender inn IM")
    void kobletSakFarUtsetterStartdato() {
        var fødselsdato = helgejustertTilMandag(LocalDate.now().minusMonths(4));
        var farOpprinneligStartdato = fødselsdato.plusWeeks(10).plusDays(1);
        var farUtsattStartDato = LocalDate.now().plusWeeks(5).plusDays(2);

        var familie = new Familie("84", fordel);
        var saksnummerMor = behandleSøknadForMorUtenOverlapp(familie, fødselsdato);
        var saksnummerFar = behandleSøknadForFarUtenOverlapp(familie, fødselsdato, farOpprinneligStartdato);

        saksbehandler.hentFagsak(saksnummerFar);
        assertThat(saksbehandler.harRevurderingBehandling())
                .as("Har revurdert behandling")
                .isFalse();

        // Sier opp uttaket men søker om uttak ca 15 uker senere
        var fordelingFrasiPerioder = generiskFordeling(
                utsettelsesperiode(FRI, farOpprinneligStartdato, farOpprinneligStartdato.plusWeeks(2).minusDays(1)),
                uttaksperiode(StønadskontoType.FEDREKVOTE, farUtsattStartDato, farUtsattStartDato.plusWeeks(2).minusDays(1)));
        var søknad = lagEndringssøknadFødsel(fødselsdato, BrukerRolle.FAR, fordelingFrasiPerioder, saksnummerFar);
        familie.far().søk(søknad.build());

        saksbehandler.hentFagsak(saksnummerFar);
        saksbehandler.ventPåOgVelgRevurderingBehandling();
        saksbehandler.ventTilAvsluttetBehandling();
        assertThat(saksbehandler.valgtBehandling.hentBehandlingsresultat())
                .as("Behandlingsresultat")
                .isEqualTo(BehandlingResultatType.FORELDREPENGER_SENERE);

        saksbehandler.ventPåOgVelgÅpenFørstegangsbehandling();
        saksbehandler.harBehandlingsstatus(BehandlingStatus.UTREDES);

        // Søkt for tidlig - kan behandles om litt over en uke
        assertThat(saksbehandler.valgtBehandling.getAksjonspunkter()
                .stream().map(Aksjonspunkt::getDefinisjon)
                .anyMatch(AksjonspunktKoder.AUTO_VENT_PGA_FOR_TIDLIG_SØKNAD::equals)).isTrue();
    }

    @Test
    @DisplayName("Mor får revurdering fra endringssøknad vedtak opphører")
    @Description("Mor får revurdering fra endringssøknad vedtak opphører - far får revurdering")
    void berørtSakOpphør() {
        var familie = new Familie("84", fordel);
        var fødselsdato = LocalDate.now().minusMonths(4);
        var saksnummerMor = behandleSøknadForMorUtenOverlapp(familie, fødselsdato);
        var saksnummerFar = behandleSøknadForFarUtenOverlapp(familie, fødselsdato);

        sendInnEndringssøknadforMorMedEndretUttak(familie, saksnummerMor);

        overstyrer.hentFagsak(saksnummerMor);
        overstyrer.ventPåOgVelgRevurderingBehandling();

        var overstyr = new OverstyrFodselsvilkaaret();
        overstyr.avvis(Avslagsårsak.SØKER_ER_FAR);
        overstyr.setBegrunnelse("avvist");
        overstyrer.overstyr(overstyr);

        var vurderSoknadsfristForeldrepengerBekreftelse = overstyrer
                .hentAksjonspunktbekreftelse(VurderSoknadsfristForeldrepengerBekreftelse.class)
                .bekreftHarGyldigGrunn(fødselsdato);
        overstyrer.bekreftAksjonspunkt(vurderSoknadsfristForeldrepengerBekreftelse);

        overstyrer.bekreftAksjonspunktMedDefaultVerdier(ForeslåVedtakBekreftelse.class);

        beslutter.hentFagsak(saksnummerMor);
        beslutter.ventPåOgVelgRevurderingBehandling();
        var bekreftelse = beslutter.hentAksjonspunktbekreftelse(FatterVedtakBekreftelse.class);
        bekreftelse.godkjennAksjonspunkter(beslutter.hentAksjonspunktSomSkalTilTotrinnsBehandling());
        beslutter.fattVedtakOgVentTilAvsluttetBehandling(bekreftelse);
        assertThat(beslutter.valgtBehandling.hentBehandlingsresultat())
                .as("Behandlingsresultat")
                .isEqualTo(BehandlingResultatType.OPPHØR);

        saksbehandler.hentFagsak(saksnummerFar);
        saksbehandler.ventPåOgVelgRevurderingBehandling();
        assertThat(saksbehandler.valgtBehandling.hentBehandlingsresultat())
                .as("Behandlingsresultat")
                .isEqualTo(BehandlingResultatType.INGEN_ENDRING);
    }

    @Test
    @DisplayName("Mor får revurdering fra endringssøknad endring av uttak")
    @Description("Mor får revurdering fra endringssøknad endring av uttak - fører til revurdering hos far")
    void berørtSakEndringAvUttak() {
        var familie = new Familie("84", fordel);
        var fødselsdato = LocalDate.now().minusMonths(4).plusDays(2);
        var saksnummerMor = behandleSøknadForMorUtenOverlapp(familie, fødselsdato);
        var saksnummerFar = behandleSøknadForFarUtenOverlapp(familie, fødselsdato);

        sendInnEndringssøknadforMorMedEndretUttak(familie, saksnummerMor);
        saksbehandler.hentFagsak(saksnummerMor);
        saksbehandler.ventPåOgVelgRevurderingBehandling(RE_ENDRING_FRA_BRUKER);
        var vurderSoknadsfristForeldrepengerBekreftelse = saksbehandler
                .hentAksjonspunktbekreftelse(VurderSoknadsfristForeldrepengerBekreftelse.class)
                .bekreftHarGyldigGrunn(LocalDate.now());
        saksbehandler.bekreftAksjonspunkt(vurderSoknadsfristForeldrepengerBekreftelse);
        saksbehandler.bekreftAksjonspunktMedDefaultVerdier(ForeslåVedtakBekreftelse.class);

        beslutter.hentFagsak(saksnummerMor);
        beslutter.ventPåOgVelgRevurderingBehandling(RE_ENDRING_FRA_BRUKER);
        var bekreftelse = beslutter.hentAksjonspunktbekreftelse(FatterVedtakBekreftelse.class);
        bekreftelse.godkjennAksjonspunkter(beslutter.hentAksjonspunktSomSkalTilTotrinnsBehandling());
        beslutter.fattVedtakOgVentTilAvsluttetBehandling(bekreftelse);
        assertThat(beslutter.valgtBehandling.behandlingsresultat.getKonsekvenserForYtelsen())
                .as("Konsekvenser for ytelsen")
                .contains(KonsekvensForYtelsen.ENDRING_I_UTTAK);
    }

    @Test
    @DisplayName("Koblet sak mor søker etter far og sniker i køen")
    @Description("Far søker. Blir satt på vent pga for tidlig søknad. Mor søker og får innvilget. Oppretter manuell revurdering på mor.")
    void kobletSakMorSøkerEtterFar() {
        var familie = new Familie("84", fordel);
        var fødselsdato = LocalDate.now().minusDays(15);
        behandleSøknadForFarSattPåVent(familie, fødselsdato);
        var saksnummerMor = behandleSøknadForMorUregistrert(familie, fødselsdato);

        saksbehandler.hentFagsak(saksnummerMor);

        saksbehandler.opprettBehandlingRevurdering(BehandlingÅrsakType.RE_OPPLYSNINGER_OM_FORDELING);
        saksbehandler.ventPåOgVelgRevurderingBehandling();

        saksbehandler.bekreftAksjonspunktMedDefaultVerdier(KontrollerManueltOpprettetRevurdering.class);
        saksbehandler.bekreftAksjonspunktMedDefaultVerdier(ForeslåVedtakManueltBekreftelse.class);
        saksbehandler.ventTilAvsluttetBehandlingOgFagsakLøpendeEllerAvsluttet();
    }

    @Step("Behandle søknad for mor uregistrert")
    private Saksnummer behandleSøknadForMorUregistrert(Familie familie, LocalDate fødselsdato) {
        var saksnummer = sendInnSøknadOgInntektMor(familie, fødselsdato);

        saksbehandler.hentFagsak(saksnummer);

        var vurderManglendeFodselBekreftelse = saksbehandler
                .hentAksjonspunktbekreftelse(VurderManglendeFodselBekreftelse.class)
                .bekreftDokumentasjonForeligger(1, fødselsdato);
        saksbehandler.bekreftAksjonspunkt(vurderManglendeFodselBekreftelse);

        saksbehandler.bekreftAksjonspunktMedDefaultVerdier(ForeslåVedtakBekreftelse.class);


        beslutter.hentFagsak(saksnummer);
        var bekreftelse = beslutter.hentAksjonspunktbekreftelse(FatterVedtakBekreftelse.class);
        bekreftelse.godkjennAksjonspunkt(saksbehandler.hentAksjonspunkt(AksjonspunktKoder.SJEKK_MANGLENDE_FØDSEL));
        beslutter.fattVedtakOgVentTilAvsluttetBehandling(bekreftelse);

        return saksnummer;
    }

    private Saksnummer behandleSøknadForMorUtenOverlapp(Familie familie, LocalDate fødselsdato) {
        var saksnummer = sendInnSøknadOgInntektMor(familie, fødselsdato);

        saksbehandler.hentFagsak(saksnummer);

        var vurderManglendeFodselBekreftelse = saksbehandler
                .hentAksjonspunktbekreftelse(VurderManglendeFodselBekreftelse.class)
                .bekreftDokumentasjonForeligger(1, fødselsdato);
        saksbehandler.bekreftAksjonspunkt(vurderManglendeFodselBekreftelse);

        var vurderSoknadsfristForeldrepengerBekreftelse = saksbehandler
                .hentAksjonspunktbekreftelse(VurderSoknadsfristForeldrepengerBekreftelse.class)
                .bekreftHarGyldigGrunn(LocalDate.now().minusMonths(4));
        saksbehandler.bekreftAksjonspunkt(vurderSoknadsfristForeldrepengerBekreftelse);

        saksbehandler.bekreftAksjonspunktMedDefaultVerdier(ForeslåVedtakBekreftelse.class);

        beslutter.hentFagsak(saksnummer);
        var bekreftelse = beslutter.hentAksjonspunktbekreftelse(FatterVedtakBekreftelse.class);
        bekreftelse.godkjennAksjonspunkter(beslutter.hentAksjonspunktSomSkalTilTotrinnsBehandling());
        beslutter.fattVedtakOgVentTilAvsluttetBehandling(bekreftelse);
        assertThat(saksbehandler.valgtBehandling.hentBehandlingsresultat())
                .as("Behandlingsresultat")
                .isEqualTo(BehandlingResultatType.INNVILGET);

        return saksnummer;
    }

    private Saksnummer behandleSøknadForFarUtenOverlapp(Familie familie, LocalDate fødselsdato) {
        return behandleSøknadForFarUtenOverlapp(familie, fødselsdato, fødselsdato.plusWeeks(10).plusDays(1), null);
    }

    private Saksnummer behandleSøknadForFarUtenOverlapp(Familie familie, LocalDate fødselsdato, LocalDate startdato) {
        return behandleSøknadForFarUtenOverlapp(familie, fødselsdato, startdato, null);
    }

    private Saksnummer behandleSøknadForFarUtenOverlapp(Familie familie, LocalDate fødselsdato, LocalDate startdato, Saksnummer saksnummer) {
        saksnummer = sendInnSøknadOgInntektFar(familie, fødselsdato, startdato, saksnummer);
        behandleFerdigSøknadForFarUtenOverlapp(saksnummer);
        return saksnummer;

    }
    private void behandleFerdigSøknadForFarUtenOverlapp(Saksnummer saksnummer) {
        saksbehandler.hentFagsak(saksnummer);

        assertThat(saksbehandler.sakErKobletTilAnnenpart())
                .as("Sak koblet til annenpart")
                .isTrue();

        var vurderManglendeFodselBekreftelse = saksbehandler
                .hentAksjonspunktbekreftelse(VurderManglendeFodselBekreftelse.class)
                .bekreftDokumentasjonForeligger(1, LocalDate.now().minusMonths(4));
        saksbehandler.bekreftAksjonspunkt(vurderManglendeFodselBekreftelse);

        if (saksbehandler.harAksjonspunkt("5020"))
            saksbehandler.bekreftAksjonspunktMedDefaultVerdier(AvklarBrukerBosattBekreftelse.class);

        saksbehandler.bekreftAksjonspunktMedDefaultVerdier(ForeslåVedtakBekreftelse.class);

        beslutter.hentFagsak(saksnummer);
        var bekreftelse = beslutter.hentAksjonspunktbekreftelse(FatterVedtakBekreftelse.class);
        bekreftelse.godkjennAksjonspunkter(beslutter.hentAksjonspunktSomSkalTilTotrinnsBehandling());
        beslutter.fattVedtakOgVentTilAvsluttetBehandling(bekreftelse);
        assertThat(beslutter.valgtBehandling.hentBehandlingsresultat())
                .as("Behandlingsresultat")
                .isEqualTo(BehandlingResultatType.INNVILGET);

    }

    private void sendInnEndringssøknadforMor(Familie familie, Saksnummer saksnummerMor) {
        // TODO: Matcher ikke scenario!
        var fødselsdato = LocalDate.now().minusMonths(4);
        var fordeling = FordelingErketyper.fordelingMorHappyCase(fødselsdato);
        var søknad = lagEndringssøknadFødsel(fødselsdato, BrukerRolle.MOR, fordeling, saksnummerMor);
        familie.mor().søk(søknad.build());
    }

    private void sendInnEndringssøknadforMorMedEndretUttak(Familie familie, Saksnummer saksnummerMor) {
        // TODO: Matcher ikke scenario!
        var fødselsdato = LocalDate.now().minusMonths(4);
        var fordeling = FordelingErketyper.fordelingMorHappyCaseLong(fødselsdato);
        var søknad = lagEndringssøknadFødsel(fødselsdato, BrukerRolle.MOR, fordeling, saksnummerMor);
        familie.mor().søk(søknad.build());
    }

    private Saksnummer behandleSøknadForFarSattPåVent(Familie familie, LocalDate fødselsdato) {
        var saksnummer = sendInnSøknadOgInntektFar(familie, fødselsdato, fødselsdato.plusWeeks(10).plusDays(1), null);

        saksbehandler.hentFagsak(saksnummer);
        assertThat(saksbehandler.valgtBehandling.erSattPåVent())
                .as("Behandling satt på vent (Behandling ble ikke satt på vent selv om far har søkt for tidlig)")
                .isTrue();

        return saksnummer;
    }


    private Saksnummer sendInnSøknadOgInntektMor(Familie familie, LocalDate fødselsdato) {
        var saksnummer = sendInnSøknadMor(familie, fødselsdato);
        sendInnInntektsmeldingMor(familie, fødselsdato, saksnummer);
        return saksnummer;
    }

    private Saksnummer sendInnSøknadMor(Familie familie, LocalDate fødselsdato) {
        var mor = familie.mor();
        var søknad = SøknadForeldrepengerErketyper.lagSøknadForeldrepengerTerminFødsel(fødselsdato, BrukerRolle.MOR)
                .medFordeling(FordelingErketyper.fordelingMorHappyCase(fødselsdato))
                .medAnnenForelder(lagNorskAnnenforeldre(familie.far()));
        return mor.søk(søknad.build());
    }

    private void sendInnInntektsmeldingMor(Familie familie, LocalDate fødselsdato, Saksnummer saksnummer) {
        familie.mor().arbeidsgivere().sendDefaultInntektsmeldingerFP(saksnummer, fødselsdato.minusWeeks(3));
    }

    private Saksnummer sendInnSøknadOgInntektFar(Familie familie, LocalDate fødselsdato, LocalDate startDatoForeldrepenger, Saksnummer saksnummer) {
        saksnummer = sendInnSøknadFar(familie, fødselsdato, startDatoForeldrepenger, saksnummer);
        familie.far().arbeidsgivere().sendDefaultInntektsmeldingerFP(saksnummer, startDatoForeldrepenger);
        return saksnummer;
    }

    private Saksnummer sendInnSøknadFar(Familie familie, LocalDate fødselsdato, LocalDate startDatoForeldrepenger, Saksnummer saksnummer) {
        var far = familie.far();
        var fordeling = generiskFordeling(uttaksperiode(FEDREKVOTE, startDatoForeldrepenger, startDatoForeldrepenger.plusWeeks(2)));
        var søknad = SøknadForeldrepengerErketyper.lagSøknadForeldrepengerFødsel(fødselsdato, BrukerRolle.FAR)
                .medFordeling(fordeling)
                .medAnnenForelder(lagNorskAnnenforeldre(familie.mor()));
        return saksnummer != null ? far.søk(søknad.build(), saksnummer) : far.søk(søknad.build());
    }
}
