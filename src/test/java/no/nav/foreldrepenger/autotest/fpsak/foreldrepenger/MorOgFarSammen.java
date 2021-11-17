package no.nav.foreldrepenger.autotest.fpsak.foreldrepenger;

import static no.nav.foreldrepenger.autotest.dokumentgenerator.foreldrepengesoknad.json.erketyper.FordelingErketyper.generiskFordeling;
import static no.nav.foreldrepenger.autotest.util.AllureHelper.debugFritekst;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.qameta.allure.Description;
import io.qameta.allure.Step;
import no.nav.foreldrepenger.autotest.base.FpsakTestBase;
import no.nav.foreldrepenger.autotest.dokumentgenerator.foreldrepengesoknad.json.erketyper.SøknadEndringErketyper;
import no.nav.foreldrepenger.autotest.dokumentgenerator.foreldrepengesoknad.json.erketyper.SøknadForeldrepengerErketyper;
import no.nav.foreldrepenger.autotest.dokumentgenerator.foreldrepengesoknad.json.erketyper.UttaksperioderErketyper;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.Avslagsårsak;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.BehandlingResultatType;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.BehandlingStatus;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.BehandlingÅrsakType;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.FagsakStatus;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.KonsekvensForYtelsen;
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
import no.nav.foreldrepenger.autotest.util.testscenario.modell.Familie;
import no.nav.foreldrepenger.common.domain.BrukerRolle;
import no.nav.foreldrepenger.common.domain.foreldrepenger.fordeling.StønadskontoType;
import no.nav.foreldrepenger.common.domain.foreldrepenger.fordeling.UtsettelsesÅrsak;

@Tag("fpsak")
@Tag("foreldrepenger")
class MorOgFarSammen extends FpsakTestBase {

    private static final Logger logger = LoggerFactory.getLogger(MorOgFarSammen.class);

    @Test
    @DisplayName("Mor og far koblet sak, kant til kant")
    @Description("Mor søker, får AP slik at behandling stopper opp. Far sender søknad og blir satt på vent. Behandler " +
            "ferdig mor sin søknad (positivt vedtak). Behandler far sin søknad (positivt vedtak). Ingen overlapp. " +
            "Verifiserer at sakene er koblet og at det ikke opprettes revurdering berørt sak.")
    void morOgFar_fødsel_ettArbeidsforholdHver_kobletsak_kantTilKant() {
        var familie = new Familie("82", fordel);
        var mor = familie.mor();
        var fødselsdato = familie.barn().fødselsdato();
        var fpstartdatoMor = fødselsdato.minusWeeks(3);
        var saksnummerMor = sendInnSøknadMorMedAksjonspunkt(familie);

        mor.arbeidsgiver().sendInntektsmeldingerFP(saksnummerMor, fpstartdatoMor);

        saksbehandler.hentFagsak(saksnummerMor);
        saksbehandler.harAksjonspunkt("5070");
        assertThat(saksbehandler.valgtFagsak.status())
                .as("Fagsak stauts")
                .isEqualTo(FagsakStatus.UNDER_BEHANDLING);

        var fpstartdatoFar = fødselsdato.plusWeeks(6);
        var saksnummerFar = sendInnSøknadFar(familie, fødselsdato, fpstartdatoFar, null);
        saksbehandler.hentFagsak(saksnummerFar);
        assertThat(saksbehandler.valgtFagsak.status())
                .as("Fagsak stauts")
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
        var arbeidsgivere = familie.far().arbeidsgivere();
        arbeidsgivere.sendDefaultInnteksmeldingerSVP(saksnummerFar);
        saksbehandler.hentFagsak(saksnummerFar);
        assertThat(saksbehandler.sakErKobletTilAnnenpart())
                .as("Saken er koblet til en annen behandling")
                .isTrue();
        saksbehandler.ventTilAvsluttetBehandling();
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
    @Description("Mor sin endringssøknad sniker i køen, når far sin behandling venter på IM. Dette skal ikke føre til"
            + "at far mister til periode som overlapper med mors førstegangssøknad. Mor søker om perioden på nytt får å ta den tilbake")
    void far_skal_ikke_miste_perioder_til_mor_ved_sniking() {
        var familie = new Familie("82", fordel);
        var mor = familie.mor();
        var far = familie.far();
        var fødselsdato = familie.barn().fødselsdato();
        var fpStartdato = fødselsdato.minusWeeks(3);
        var fordelingMor = generiskFordeling(
                UttaksperioderErketyper.uttaksperiode(StønadskontoType.MØDREKVOTE, fødselsdato, fødselsdato.plusWeeks(6).minusDays(1)),
                UttaksperioderErketyper.uttaksperiode(StønadskontoType.FELLESPERIODE, fødselsdato.plusWeeks(6), fødselsdato.plusWeeks(7)));
        var søknadMor = SøknadForeldrepengerErketyper.lagSøknadForeldrepengerFødsel(fødselsdato, BrukerRolle.MOR)
                .medFordeling(fordelingMor)
                .medAnnenForelder(lagNorskAnnenforeldre(far))
                .medMottatdato(fødselsdato);
        var morSaksnummer = mor.søk(søknadMor.build());

        var arbeidsgiver = mor.arbeidsgiver();
        arbeidsgiver.sendInntektsmeldingerFP(morSaksnummer, fpStartdato);

        saksbehandler.hentFagsak(morSaksnummer);
        saksbehandler.ventTilAvsluttetBehandling();

        var farStartDato = fødselsdato.plusWeeks(6);
        var søknad = SøknadForeldrepengerErketyper.lagSøknadForeldrepengerFødsel(fødselsdato, BrukerRolle.FAR)
                .medFordeling(generiskFordeling(
                        UttaksperioderErketyper.uttaksperiode(StønadskontoType.FEDREKVOTE, farStartDato, fødselsdato.plusWeeks(7))))
                .medAnnenForelder(lagNorskAnnenforeldre(familie.mor()))
                .medMottatdato(fødselsdato.plusWeeks(1));
        var farSaksnummer = far.søk(søknad.build());

        //Endringssøknad til mor sniker i køen når fars behandling venter på inntektsmelding
        var fordeling1 = generiskFordeling(
                UttaksperioderErketyper.uttaksperiode(StønadskontoType.FELLESPERIODE, fødselsdato.plusWeeks(7).plusDays(1), fødselsdato.plusWeeks(8)));
        var endringssøknadMor = SøknadEndringErketyper.lagEndringssøknadFødsel(
                fødselsdato,
                BrukerRolle.MOR,
                fordeling1,
                morSaksnummer)
                .medMottattDato(fødselsdato.plusWeeks(2));
        mor.søk(endringssøknadMor.build());

        saksbehandler.ventPåOgVelgRevurderingBehandling();
        saksbehandler.ventTilAvsluttetBehandling();

        // Far inntektsmelding
        far.arbeidsgivere().sendDefaultInntektsmeldingerFP(farSaksnummer, farStartDato);

        saksbehandler.hentFagsak(farSaksnummer);
        saksbehandler.ventTilAvsluttetBehandling();
        assertThat(saksbehandler.hentAvslåtteUttaksperioder()).isEmpty();

        var fordeling2 = generiskFordeling(
                UttaksperioderErketyper.uttaksperiode(StønadskontoType.FELLESPERIODE, fødselsdato.plusWeeks(6), fødselsdato.plusWeeks(7)));
        var endringssøknadMor2 = SøknadEndringErketyper.lagEndringssøknadFødsel(
                fødselsdato,
                BrukerRolle.MOR,
                fordeling2,
                morSaksnummer)
                .medMottattDato(fødselsdato.plusWeeks(2));
        mor.søk(endringssøknadMor2.build());

        saksbehandler.hentFagsak(morSaksnummer);
        saksbehandler.ventTilAvsluttetBehandling();
        assertThat(saksbehandler.hentAvslåtteUttaksperioder()).isEmpty();
    }

    @Test
    @DisplayName("Far og mor søker fødsel med overlappende uttaksperiode")
    @Description("Mor søker og får innvilget. Far søker med to uker overlapp med mor (stjeling). Far får innvilget. " +
            "Berørt sak opprettet mor. Siste periode blir spittet i to og siste del blir avlsått. Det opprettes ikke" +
            "berørt sak på far.")
    void farOgMor_fødsel_ettArbeidsforholdHver_overlappendePeriode() {
        var familie = new Familie("82", fordel);
        var mor = familie.mor();
        var fødselsdato = familie.barn().fødselsdato();
        var fpstartdatoMor = fødselsdato.minusWeeks(3);
        var fpStartdatoFar = fødselsdato.plusWeeks(8);

        // MOR
        var fordelingMor = generiskFordeling(
                UttaksperioderErketyper.uttaksperiode(StønadskontoType.FORELDREPENGER_FØR_FØDSEL, fødselsdato.minusWeeks(3), fødselsdato.minusDays(1)),
                UttaksperioderErketyper.uttaksperiode(StønadskontoType.MØDREKVOTE, fødselsdato, fødselsdato.plusWeeks(10).minusDays(1)));
        var søknadMor = SøknadForeldrepengerErketyper.lagSøknadForeldrepengerFødsel(fødselsdato, BrukerRolle.MOR)
                .medFordeling(fordelingMor)
                .medAnnenForelder(lagNorskAnnenforeldre(familie.far()));
        var saksnummerMor = mor.søk(søknadMor.build());

        var arbeidsgivere = mor.arbeidsgivere();
        arbeidsgivere.sendDefaultInntektsmeldingerFP(saksnummerMor, fpstartdatoMor);

        saksbehandler.hentFagsak(saksnummerMor);
        saksbehandler.ventTilAvsluttetBehandling();
        debugFritekst("Ferdig med første behandling mor");
        assertThat(saksbehandler.valgtBehandling.hentBehandlingsresultat())
                .as("Behandlingsresultat")
                .isEqualTo(BehandlingResultatType.INNVILGET);
        assertThat(saksbehandler.valgtBehandling.getUttakResultatPerioder().getPerioderSøker())
                .as("Antall uttkasperioder for søker")
                .hasSize(3);
        assertThat(saksbehandler.valgtBehandling.getSaldoer().getStonadskontoer())
                .as("Antall stønadskontoer i saldo")
                .hasSize(4);

        // FAR
        var far = familie.far();
        var fordelingFar  = generiskFordeling(
                UttaksperioderErketyper.uttaksperiode(StønadskontoType.FEDREKVOTE, fødselsdato.plusWeeks(8), fødselsdato.plusWeeks(12)));
        var søknadFar = SøknadForeldrepengerErketyper.lagSøknadForeldrepengerFødsel(fødselsdato, BrukerRolle.FAR)
                .medFordeling(fordelingFar)
                .medAnnenForelder(lagNorskAnnenforeldre(familie.mor()));
        var saksnummerFar = far.søk(søknadFar.build());

        far.arbeidsgivere().sendDefaultInntektsmeldingerFP(saksnummerFar, fpStartdatoFar);

        saksbehandler.hentFagsak(saksnummerFar);
        saksbehandler.ventTilAvsluttetBehandling();
        debugFritekst("Ferdig med første behandling til far");
        assertThat(saksbehandler.valgtBehandling.hentBehandlingsresultat())
                .as("Behandlingsresultat")
                .isEqualTo(BehandlingResultatType.INNVILGET);
        assertThat(saksbehandler.sakErKobletTilAnnenpart())
                .as("Sak er koblet til mor sin behandling")
                .isTrue();
        assertThat(saksbehandler.valgtBehandling.hentUttaksperioder())
                .as("Antall uttaksperioder")
                .hasSize(2);
        assertThat(saksbehandler.valgtBehandling.getSaldoer().getStonadskontoer())
                .as("Antall stønadskontoer")
                .hasSize(4);

        // Revurdering berørt sak mor
        saksbehandler.hentFagsak(saksnummerMor);
        saksbehandler.ventPåOgVelgRevurderingBehandling();
        debugFritekst("Revurdering berørt sak opprettet på mor.");
        assertThat(saksbehandler.sakErKobletTilAnnenpart())
                .as("Saken er koblet til annenpart")
                .isTrue();
        assertThat(saksbehandler.valgtBehandling.hentUttaksperioder())
                .as("Antall uttaksperioder")
                .hasSize(4);
        assertThat(saksbehandler.valgtBehandling.getSaldoer().getStonadskontoer().get(StønadskontoType.FEDREKVOTE).getSaldo())
                .as("Saldo fro stønadskonto FEDREKVOTE")
                .isPositive();
        assertThat(saksbehandler.valgtBehandling.getSaldoer().getStonadskontoer().get(StønadskontoType.MØDREKVOTE).getSaldo())
                .as("Saldo fro stønadskonto MØDREKVOTE")
                .isPositive();
        assertThat(saksbehandler.valgtBehandling.getSaldoer().getStonadskontoer().get(StønadskontoType.FELLESPERIODE).getSaldo())
                .as("Saldo fro stønadskonto FELLESPERIODE")
                .isEqualTo(80);
        assertThat(saksbehandler.valgtBehandling.getSaldoer().getStonadskontoer().get(StønadskontoType.FORELDREPENGER_FØR_FØDSEL).getSaldo())
                .as("Saldo fro stønadskonto FORELDREPENGER_FØR_FØDSEL")
                .isNotNegative();

        // verifiser ikke berørt sak far
        saksbehandler.hentFagsak(saksnummerFar);
        saksbehandler.ventPåOgVelgRevurderingBehandling();
        assertThat(saksbehandler.valgtBehandling.behandlingÅrsaker.stream()
                .map(BehandlingÅrsak::getBehandlingArsakType)
                .anyMatch(BehandlingÅrsakType.REBEREGN_FERIEPENGER::equals)).isTrue();
        assertThat(saksbehandler.behandlinger)
                .as("Antall behandlinger")
                .hasSize(2);
    }

    @Test
    @DisplayName("Koblet sak endringssøknad ingen endring")
    @Description("Sender inn søknad mor. Sender inn søknad far uten overlapp. Sender inn endringssøknad mor som er lik " +
                 "førstegangsbehandlingen. Verifiserer at det ikke blir berørt sak på far.")
    void KobletSakIngenEndring() {
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

        var vurderSoknadsfristForeldrepengerBekreftelse = saksbehandler
                .hentAksjonspunktbekreftelse(VurderSoknadsfristForeldrepengerBekreftelse.class)
                .bekreftHarGyldigGrunn(fødselsdato);
        saksbehandler.bekreftAksjonspunkt(vurderSoknadsfristForeldrepengerBekreftelse);

        saksbehandler.bekreftAksjonspunktMedDefaultVerdier(ForeslåVedtakBekreftelse.class);


        beslutter.hentFagsak(saksnummerMor);
        beslutter.ventPåOgVelgRevurderingBehandling();
        var bekreftelse = beslutter.hentAksjonspunktbekreftelse(FatterVedtakBekreftelse.class);
        bekreftelse.godkjennAksjonspunkter(beslutter.hentAksjonspunktSomSkalTilTotrinnsBehandling());
        beslutter.fattVedtakOgVentTilAvsluttetBehandling(bekreftelse);
        assertThat(beslutter.valgtBehandling.hentBehandlingsresultat())
                .as("Behandlingsresultat")
                .isEqualTo(BehandlingResultatType.INGEN_ENDRING);

        saksbehandler.hentFagsak(saksnummerFar);
        assertThat(saksbehandler.harRevurderingBehandling())
                .as("Har revurdert behandling (Fars behandling fikk revurdering selv uten endringer i mors behandling av endringssøknaden)")
                .isFalse();
    }

    @Test
    @DisplayName("Koblet sak. Far utsetter alt. Far ny 1gang")
    @Description("Sender inn søknad mor. Sender inn søknad far uten overlapp. Sender inn endringssøknad far med kun" +
            "fri utsettelse. Deretter ny førstegang med senere start.")
    void KobletSakFarUtsetterAlt() {
        var fødselsdato = LocalDate.now().minusMonths(4);
        var fkdato = fødselsdato.plusWeeks(10).plusDays(1);
        var farUtsattStartDato = fødselsdato.plusWeeks(20).plusDays(1);

        var familie = new Familie("84", fordel);
        var saksnummerMor = behandleSøknadForMorUtenOverlapp(familie, fødselsdato); // fødselsdato-3w -> fødselsdato+10w
        var saksnummerFar = behandleSøknadForFarUtenOverlapp(familie, fødselsdato); // fødselsdato+10w1d -> fødselsdato+12w1d
        saksbehandler.hentFagsak(saksnummerFar);
        assertThat(saksbehandler.harRevurderingBehandling())
                .as("Har revurdert behandling")
                .isFalse();

        // Endringssøknad som sier opp innvilget uttak fra start
        var fordelingFrasiPerioder = generiskFordeling(UttaksperioderErketyper.utsettelsesperiode(UtsettelsesÅrsak.FRI, fkdato, fkdato.plusWeeks(2)));
        var søknad = SøknadEndringErketyper.lagEndringssøknadFødsel(fødselsdato, BrukerRolle.FAR, fordelingFrasiPerioder, saksnummerFar);
        familie.far().søk(søknad.build());

        saksbehandler.hentFagsak(saksnummerFar);
        saksbehandler.ventPåOgVelgRevurderingBehandling();

        saksbehandler.ventTilAvsluttetBehandling();

        // Har fått tømt uttaket
        assertThat(saksbehandler.valgtBehandling.hentBehandlingsresultat())
                .as("Behandlingsresultat")
                .isEqualTo(BehandlingResultatType.FORELDREPENGER_SENERE);

        // Sender ny førstegangssøknad
        behandleSøknadForFarUtenOverlapp(familie, fødselsdato, farUtsattStartDato, saksnummerFar);
    }

    @Test
    @DisplayName("Koblet sak. Far utsetter fra start med senere uttaksdato")
    @Description("Sender inn søknad mor. Sender inn søknad far uten overlapp. Sender inn endringssøknad far med " +
            "fri utsettelse og uttaksperioder med start senere. Sender inn IM")
    void KobletSakFarUtsetterStartdato() {
        var fødselsdato = LocalDate.now().minusMonths(4);
        var fkdato = fødselsdato.plusWeeks(10).plusDays(1);
        var farUtsattStartDato = LocalDate.now().plusWeeks(5).plusDays(2);

        var familie = new Familie("84", fordel);
        var saksnummerMor = behandleSøknadForMorUtenOverlapp(familie, fødselsdato);
        var saksnummerFar = behandleSøknadForFarUtenOverlapp(familie, fødselsdato);

        saksbehandler.hentFagsak(saksnummerFar);
        assertThat(saksbehandler.harRevurderingBehandling())
                .as("Har revurdert behandling")
                .isFalse();

        // Sier opp uttaket men søker om uttak ca 15 uker senere
        var fordelingFrasiPerioder = generiskFordeling(UttaksperioderErketyper.utsettelsesperiode(UtsettelsesÅrsak.FRI, fkdato, fkdato.plusWeeks(2)),
                UttaksperioderErketyper.uttaksperiode(StønadskontoType.FEDREKVOTE, farUtsattStartDato, farUtsattStartDato.plusWeeks(2)));
        var søknad = SøknadEndringErketyper.lagEndringssøknadFødsel(fødselsdato, BrukerRolle.FAR, fordelingFrasiPerioder, saksnummerFar);
        familie.far().søk(søknad.build());

        saksbehandler.hentFagsak(saksnummerFar);
        saksbehandler.ventPåOgVelgRevurderingBehandling();

        saksbehandler.ventTilAvsluttetBehandling();
        assertThat(saksbehandler.valgtBehandling.hentBehandlingsresultat())
                .as("Behandlingsresultat")
                .isEqualTo(BehandlingResultatType.FORELDREPENGER_SENERE);

        saksbehandler.hentFagsak(saksnummerFar);
        saksbehandler.ventPåOgVelgÅpenFørstegangsbehandling();
        saksbehandler.ventTilBehandlingsstatus(BehandlingStatus.UTREDES);

        // Søkt for tidlig - kan behandles om litt over en uke
        assertThat(saksbehandler.valgtBehandling.getAksjonspunkter()
                .stream().map(Aksjonspunkt::getDefinisjon)
                .anyMatch(k -> AksjonspunktKoder.VENT_PGA_FOR_TIDLIG_SØKNAD.equals(k.kode))).isTrue();
    }

    @Test
    @DisplayName("Mor får revurdering fra endringssøknad vedtak opphører")
    @Description("Mor får revurdering fra endringssøknad vedtak opphører - far får revurdering")
    void BerørtSakOpphør() {
        var familie = new Familie("84", fordel);
        var saksnummerMor = behandleSøknadForMorUtenOverlapp(familie, LocalDate.now().minusMonths(4));
        var saksnummerFar = behandleSøknadForFarUtenOverlapp(familie, LocalDate.now().minusMonths(4));

        sendInnEndringssøknadforMor(familie, saksnummerMor);

        overstyrer.hentFagsak(saksnummerMor);
        overstyrer.ventPåOgVelgRevurderingBehandling();

        var overstyr = new OverstyrFodselsvilkaaret();
        overstyr.avvis(Avslagsårsak.SØKER_ER_FAR);
        overstyr.setBegrunnelse("avvist");
        overstyrer.overstyr(overstyr);

        var vurderSoknadsfristForeldrepengerBekreftelse = overstyrer
                .hentAksjonspunktbekreftelse(VurderSoknadsfristForeldrepengerBekreftelse.class)
                .bekreftHarGyldigGrunn(LocalDate.now().minusMonths(4));
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
    void BerørtSakEndringAvUttak() {
        var familie = new Familie("84", fordel);
        var saksnummerMor = behandleSøknadForMorUtenOverlapp(familie, LocalDate.now().minusMonths(4));
        var saksnummerFar = behandleSøknadForFarUtenOverlapp(familie, LocalDate.now().minusMonths(4));
        sendInnEndringssøknadforMorMedEndretUttak(familie, saksnummerMor);

        saksbehandler.hentFagsak(saksnummerMor);

        saksbehandler.ventPåOgVelgRevurderingBehandling();

        var vurderSoknadsfristForeldrepengerBekreftelse = saksbehandler
                .hentAksjonspunktbekreftelse(VurderSoknadsfristForeldrepengerBekreftelse.class)
                .bekreftHarGyldigGrunn(LocalDate.now());
        saksbehandler.bekreftAksjonspunkt(vurderSoknadsfristForeldrepengerBekreftelse);

        saksbehandler.bekreftAksjonspunktMedDefaultVerdier(ForeslåVedtakBekreftelse.class);


        beslutter.hentFagsak(saksnummerMor);
        beslutter.ventPåOgVelgRevurderingBehandling();
        var bekreftelse = beslutter.hentAksjonspunktbekreftelse(FatterVedtakBekreftelse.class);
        bekreftelse.godkjennAksjonspunkter(beslutter.hentAksjonspunktSomSkalTilTotrinnsBehandling());
        beslutter.fattVedtakOgVentTilAvsluttetBehandling(bekreftelse);
        assertThat(beslutter.valgtBehandling.behandlingsresultat.getKonsekvenserForYtelsen())
                .as("Konsekvenser for ytelsen")
                .contains(KonsekvensForYtelsen.ENDRING_I_UTTAK);

        saksbehandler.hentFagsak(saksnummerFar);
        saksbehandler.ventPåOgVelgRevurderingBehandling();
        assertThat(saksbehandler.valgtBehandling.behandlingsresultat.getKonsekvenserForYtelsen())
                .as("Konsekvenser for ytelsen")
                .contains(KonsekvensForYtelsen.ENDRING_I_UTTAK);
    }

    @Test
    @DisplayName("Koblet sak mor søker etter far og sniker i køen")
    @Description("Far søker. Blir satt på vent pga for tidlig søknad. Mor søker og får innvilget. Oppretter manuell " +
                 "revurdering på mor. ")
    void KobletSakMorSøkerEtterFar() {
        var familie = new Familie("84", fordel);
        var fødselsdato = LocalDate.now().minusDays(15);
        behandleSøknadForFarSattPåVent(familie, fødselsdato);
        var saksnummerMor = behandleSøknadForMorUregistrert(familie, fødselsdato);

        saksbehandler.hentFagsak(saksnummerMor);

        saksbehandler.opprettBehandlingRevurdering(BehandlingÅrsakType.RE_OPPLYSNINGER_OM_FORDELING);
        saksbehandler.ventPåOgVelgRevurderingBehandling();

        saksbehandler.bekreftAksjonspunktbekreftelserer(
                saksbehandler.hentAksjonspunktbekreftelse(KontrollerManueltOpprettetRevurdering.class),
                saksbehandler.hentAksjonspunktbekreftelse(ForeslåVedtakManueltBekreftelse.class));
        saksbehandler.ventTilAvsluttetBehandling();
    }

    @Step("Behandle søknad for mor uregistrert")
    private long behandleSøknadForMorUregistrert(Familie familie, LocalDate fødselsdato) {
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

    private long behandleSøknadForMorUtenOverlapp(Familie familie, LocalDate fødselsdato) {
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

    private long behandleSøknadForFarUtenOverlapp(Familie familie, LocalDate fødselsdato) {
        return behandleSøknadForFarUtenOverlapp(familie, fødselsdato, fødselsdato.plusWeeks(10).plusDays(1), null);
    }

    private long behandleSøknadForFarUtenOverlapp(Familie familie, LocalDate fødselsdato, LocalDate startdato, Long bruksaksnr) {
        var saksnummer = sendInnSøknadOgInntektFar(familie, fødselsdato, startdato, bruksaksnr);
        behandleFerdigSøknadForFarUtenOverlapp(saksnummer);
        return saksnummer;

    }
    private void behandleFerdigSøknadForFarUtenOverlapp(Long saksnummer) {
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

    private void sendInnEndringssøknadforMor(Familie familie, long saksnummerMor) {
        // TODO: Matcher ikke scenario!
        var fødselsdato = LocalDate.now().minusMonths(4);
        var fordeling = no.nav.foreldrepenger.autotest.dokumentgenerator.foreldrepengesoknad.json.erketyper.FordelingErketyper.fordelingMorHappyCase(fødselsdato);
        var søknad = SøknadEndringErketyper.lagEndringssøknadFødsel(fødselsdato, BrukerRolle.MOR, fordeling, saksnummerMor);
        familie.mor().søk(søknad.build());
    }

    private void sendInnEndringssøknadforMorMedEndretUttak(Familie familie, long saksnummerMor) {
        // TODO: Matcher ikke scenario!
        var fødselsdato = LocalDate.now().minusMonths(4);
        var fordeling = no.nav.foreldrepenger.autotest.dokumentgenerator.foreldrepengesoknad.json.erketyper.FordelingErketyper.fordelingMorHappyCaseLong(fødselsdato);
        var søknad = SøknadEndringErketyper.lagEndringssøknadFødsel(fødselsdato, BrukerRolle.MOR, fordeling, saksnummerMor);
        familie.mor().søk(søknad.build());
    }

    private long behandleSøknadForFarSattPåVent(Familie familie, LocalDate fødselsdato) {
        var saksnummer = sendInnSøknadOgInntektFar(familie, fødselsdato, fødselsdato.plusWeeks(10).plusDays(1), null);

        saksbehandler.hentFagsak(saksnummer);
        assertThat(saksbehandler.valgtBehandling.erSattPåVent())
                .as("Behandling satt på vent (Behandling ble ikke satt på vent selv om far har søkt for tidlig)")
                .isTrue();

        return saksnummer;
    }


    private long sendInnSøknadOgInntektMor(Familie familie, LocalDate fødselsdato) {
        var saksnummer = sendInnSøknadMor(familie, fødselsdato);
        sendInnInntektsmeldingMor(familie, fødselsdato, saksnummer);
        return saksnummer;
    }

    private long sendInnSøknadMor(Familie familie, LocalDate fødselsdato) {
        var mor = familie.mor();
        var søknad = SøknadForeldrepengerErketyper.lagSøknadForeldrepengerFødsel(fødselsdato, BrukerRolle.MOR)
                .medFordeling(no.nav.foreldrepenger.autotest.dokumentgenerator.foreldrepengesoknad.json.erketyper.FordelingErketyper.fordelingMorHappyCase(fødselsdato))
                .medAnnenForelder(lagNorskAnnenforeldre(familie.far()));
        return mor.søk(søknad.build());
    }

    private long sendInnSøknadMorMedAksjonspunkt(Familie familie) {
        var mor = familie.mor();
        var fødselsdato = familie.barn().fødselsdato();
        var fpstartdatoMor = fødselsdato.minusWeeks(3);
        var fordelingMor = generiskFordeling(
                UttaksperioderErketyper.uttaksperiode(StønadskontoType.FORELDREPENGER_FØR_FØDSEL, fpstartdatoMor, fødselsdato.minusDays(1)),
                UttaksperioderErketyper.utsettelsesperiode(UtsettelsesÅrsak.SYKDOM, fødselsdato,fødselsdato.plusWeeks(6).minusDays(1)));
        var søknad = SøknadForeldrepengerErketyper.lagSøknadForeldrepengerFødsel(fødselsdato, BrukerRolle.MOR)
                .medFordeling(fordelingMor)
                .medAnnenForelder(lagNorskAnnenforeldre(familie.far()));
        return mor.søk(søknad.build());
    }

    private void sendInnInntektsmeldingMor(Familie familie, LocalDate fødselsdato, Long saksnummer) {
        familie.mor().arbeidsgivere().sendDefaultInntektsmeldingerFP(saksnummer, fødselsdato.minusWeeks(3));
    }

    private long sendInnSøknadOgInntektFar(Familie familie, LocalDate fødselsdato, LocalDate startDatoForeldrepenger, Long bruksaksnr) {
        var saksnummer = sendInnSøknadFar(familie, fødselsdato, startDatoForeldrepenger, bruksaksnr);
        familie.far().arbeidsgivere().sendDefaultInntektsmeldingerFP(saksnummer, startDatoForeldrepenger);
        return saksnummer;
    }

    private long sendInnSøknadFar(Familie familie, LocalDate fødselsdato, LocalDate startDatoForeldrepenger, Long bruksaksnr) {
        var far = familie.far();
        var fordeling = generiskFordeling(
                UttaksperioderErketyper.uttaksperiode(StønadskontoType.FEDREKVOTE, startDatoForeldrepenger, startDatoForeldrepenger.plusWeeks(2)));
        var søknad = SøknadForeldrepengerErketyper.lagSøknadForeldrepengerFødsel(fødselsdato, BrukerRolle.FAR)
                .medFordeling(fordeling)
                .medAnnenForelder(lagNorskAnnenforeldre(familie.mor()));
        return bruksaksnr != null ? far.søk(søknad.build(), bruksaksnr) : far.søk(søknad.build());
    }
}
