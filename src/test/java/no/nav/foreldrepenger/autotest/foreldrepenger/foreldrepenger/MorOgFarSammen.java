package no.nav.foreldrepenger.autotest.foreldrepenger.foreldrepenger;

import static no.nav.foreldrepenger.autotest.domain.foreldrepenger.Stønadskonto.FEDREKVOTE;
import static no.nav.foreldrepenger.autotest.domain.foreldrepenger.Stønadskonto.FELLESPERIODE;
import static no.nav.foreldrepenger.autotest.domain.foreldrepenger.Stønadskonto.FORELDREPENGER_FØR_FØDSEL;
import static no.nav.foreldrepenger.autotest.domain.foreldrepenger.Stønadskonto.MØDREKVOTE;
import static no.nav.foreldrepenger.autotest.erketyper.FordelingErketyper.generiskFordeling;
import static no.nav.foreldrepenger.autotest.erketyper.InntektsmeldingForeldrepengeErketyper.lagInntektsmelding;
import static no.nav.foreldrepenger.autotest.erketyper.InntektsmeldingForeldrepengeErketyper.makeInntektsmeldingFromTestscenario;
import static no.nav.foreldrepenger.autotest.erketyper.InntektsmeldingForeldrepengeErketyper.makeInntektsmeldingFromTestscenarioMedIdent;
import static no.nav.foreldrepenger.autotest.erketyper.SøknadEndringErketyper.lagEndringssøknad;
import static no.nav.foreldrepenger.autotest.erketyper.SøknadForeldrepengerErketyper.lagSøknadForeldrepengerFødsel;
import static no.nav.foreldrepenger.autotest.erketyper.UttaksperioderErketyper.utsettelsesperiode;
import static no.nav.foreldrepenger.autotest.erketyper.UttaksperioderErketyper.uttaksperiode;
import static no.nav.foreldrepenger.autotest.util.AllureHelper.debugFritekst;
import static no.nav.foreldrepenger.autotest.util.AllureHelper.debugLoggBehandlingsliste;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.qameta.allure.Description;
import io.qameta.allure.Step;
import no.nav.foreldrepenger.autotest.base.ForeldrepengerTestBase;
import no.nav.foreldrepenger.autotest.dokumentgenerator.foreldrepengesoknad.SøkersRolle;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.Avslagsårsak;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.BehandlingResultatType;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.BehandlingÅrsakType;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.FagsakStatus;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.KonsekvensForYtelsen;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.SøknadUtsettelseÅrsak;
import no.nav.foreldrepenger.autotest.erketyper.FordelingErketyper;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.FastsettUttaksperioderManueltBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.FatterVedtakBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.ForeslåVedtakBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.ForeslåVedtakManueltBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.KontrollerManueltOpprettetRevurdering;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.VurderManglendeFodselBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.VurderSoknadsfristForeldrepengerBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.avklarfakta.AvklarBrukerBosattBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.overstyr.OverstyrFodselsvilkaaret;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.AksjonspunktKoder;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.BehandlingÅrsak;
import no.nav.foreldrepenger.vtp.kontrakter.TestscenarioDto;
import no.nav.foreldrepenger.vtp.testmodell.dokument.modell.koder.DokumenttypeId;

@Tag("fpsak")
@Tag("foreldrepenger")
@Tag("fluoritt")
class MorOgFarSammen extends ForeldrepengerTestBase {

    private static final Logger logger = LoggerFactory.getLogger(MorOgFarSammen.class);

    @Test
    @DisplayName("Mor og far koblet sak, kant til kant")
    @Description("Mor søker, får AP slik at behandling stopper opp. Far sender søknad og blir satt på vent. Behandler " +
            "ferdig mor sin søknad (positivt vedtak). Behandler far sin søknad (positivt vedtak). Ingen overlapp. " +
            "Verifiserer at sakene er koblet og at det ikke opprettes revurdering berørt sak.")
    void morOgFar_fødsel_ettArbeidsforholdHver_kobletsak_kantTilKant() {
        var testscenario = opprettTestscenario("82");
        var morIdent = testscenario.personopplysninger().søkerIdent();
        var morAktørId = testscenario.personopplysninger().søkerAktørIdent();
        var farIdent = testscenario.personopplysninger().annenpartIdent();
        var farAktørId = testscenario.personopplysninger().annenpartAktørIdent();
        var fødselsdato = testscenario.personopplysninger().fødselsdato();
        var fpstartdatoMor = fødselsdato.minusWeeks(3);
        var fpstartdatoFar = fødselsdato.plusWeeks(6);

        var saksnummerMor = sendInnSøknadMorMedAksjonspunkt(testscenario, fødselsdato);
        var inntektsmeldingerMor = makeInntektsmeldingFromTestscenario(testscenario, fpstartdatoMor);
        fordel.sendInnInntektsmeldinger(inntektsmeldingerMor, morAktørId, morIdent, saksnummerMor);
        saksbehandler.hentFagsak(saksnummerMor);
        assertThat(saksbehandler.valgtFagsak.status())
                .as("Fagsak stauts")
                .isEqualTo(FagsakStatus.UNDER_BEHANDLING);
        var saksnummerFar = sendInnSøknadFar(testscenario, fødselsdato, fpstartdatoFar);
        saksbehandler.hentFagsak(saksnummerFar);
        debugLoggBehandlingsliste(saksbehandler.behandlinger);
        assertThat(saksbehandler.valgtFagsak.status())
                .as("Fagsak stauts")
                .isEqualTo(FagsakStatus.UNDER_BEHANDLING);
        assertThat(saksbehandler.valgtBehandling.erSattPåVent())
                .as("Behandling er satt på vent")
                .isTrue();

        // Behandle ferdig mor sin sak
        saksbehandler.hentFagsak(saksnummerMor);
        debugLoggBehandlingsliste("mors behandlinger", saksbehandler.behandlinger);
        var fastsettUttaksperioderManueltBekreftelse = saksbehandler
                .hentAksjonspunktbekreftelse(FastsettUttaksperioderManueltBekreftelse.class)
                .innvilgManuellePerioder();
        saksbehandler.bekreftAksjonspunkt(fastsettUttaksperioderManueltBekreftelse);
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
        var inntektsmeldingerFar = makeInntektsmeldingFromTestscenarioMedIdent(testscenario,
                farIdent, fpstartdatoFar, true);
        fordel.sendInnInntektsmeldinger(inntektsmeldingerFar, farAktørId, farIdent, saksnummerFar);
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
    @Disabled("Venter på at endring er gjort i fpsak")
    void far_skal_ikke_miste_perioder_til_mor_ved_sniking() {
        var testscenario = opprettTestscenario("82");
        var morIdent = testscenario.personopplysninger().søkerIdent();
        var morAktørId = testscenario.personopplysninger().søkerAktørIdent();
        var farIdent = testscenario.personopplysninger().annenpartIdent();
        var farAktørId = testscenario.personopplysninger().annenpartAktørIdent();
        var fødselsdato = testscenario.personopplysninger().fødselsdato();

        var morSøknad = lagSøknadForeldrepengerFødsel(fødselsdato, morAktørId, SøkersRolle.MOR).medFordeling(
                FordelingErketyper.generiskFordeling(
                        uttaksperiode(MØDREKVOTE, fødselsdato, fødselsdato.plusWeeks(6).minusDays(1)),
                        uttaksperiode(FELLESPERIODE, fødselsdato.plusWeeks(6), fødselsdato.plusWeeks(7))))
                .medAnnenForelder(farAktørId)
                .medMottattDato(fødselsdato);
        var morSaksnummer = fordel.sendInnSøknad(morSøknad.build(), morAktørId, morIdent,
                DokumenttypeId.SØKNAD_FORELDREPENGER_FØDSEL);
        var imMor = lagInntektsmelding(
                testscenario.scenariodataDto().inntektskomponentModell().inntektsperioder().get(0).beløp(), morIdent,
                fødselsdato,
                testscenario.scenariodataDto().arbeidsforholdModell().arbeidsforhold().get(0).arbeidsgiverOrgnr());
        fordel.sendInnInntektsmelding(imMor, morAktørId, morIdent, morSaksnummer);
        saksbehandler.hentFagsak(morSaksnummer);
        saksbehandler.ventTilAvsluttetBehandling();

        var farSøknad = lagSøknadForeldrepengerFødsel(fødselsdato, farAktørId, SøkersRolle.FAR)
                .medFordeling(FordelingErketyper.generiskFordeling(
                        uttaksperiode(FEDREKVOTE, fødselsdato.plusWeeks(6), fødselsdato.plusWeeks(7))))
                .medAnnenForelder(morAktørId)
                .medMottattDato(fødselsdato.plusWeeks(1));

        var farSaksnummer = fordel.sendInnSøknad(farSøknad.build(), farAktørId, farIdent,
                DokumenttypeId.SØKNAD_FORELDREPENGER_FØDSEL);

        //Endringssøknad til mor sniker i køen når fars behandling venter på inntektsmelding
        var endringssøknadMor = lagEndringssøknad(morAktørId, SøkersRolle.MOR,
                generiskFordeling(uttaksperiode(FELLESPERIODE, fødselsdato.plusWeeks(7).plusDays(1), fødselsdato.plusWeeks(8))),
                morSaksnummer)
                .medMottattDato(fødselsdato.plusWeeks(2));
        fordel.sendInnSøknad(endringssøknadMor.build(), morAktørId, morIdent,
                DokumenttypeId.FORELDREPENGER_ENDRING_SØKNAD, morSaksnummer);

        saksbehandler.ventPåOgVelgRevurderingBehandling();
        saksbehandler.ventTilAvsluttetBehandling();

        var imFar = lagInntektsmelding(
                testscenario.scenariodataAnnenpartDto().inntektskomponentModell().inntektsperioder().get(0).beløp(), farIdent,
                fødselsdato.plusWeeks(6),
                testscenario.scenariodataAnnenpartDto().arbeidsforholdModell().arbeidsforhold().get(0).arbeidsgiverOrgnr());

        fordel.sendInnInntektsmelding(imFar, farAktørId, farIdent, farSaksnummer);

        saksbehandler.hentFagsak(farSaksnummer);
        saksbehandler.ventTilAvsluttetBehandling();
        assertThat(saksbehandler.hentAvslåtteUttaksperioder()).isEmpty();

        var endringssøknadMor2 = lagEndringssøknad(morAktørId, SøkersRolle.MOR,
                generiskFordeling(uttaksperiode(FELLESPERIODE, fødselsdato.plusWeeks(6), fødselsdato.plusWeeks(7))),
                morSaksnummer)
                .medMottattDato(fødselsdato.plusWeeks(2));
        fordel.sendInnSøknad(endringssøknadMor2.build(), morAktørId, morIdent,
                DokumenttypeId.FORELDREPENGER_ENDRING_SØKNAD, morSaksnummer);
        saksbehandler.hentFagsak(morSaksnummer);
        saksbehandler.ventTilAvsluttetBehandling();

        assertThat(saksbehandler.hentAvslåtteUttaksperioder()).isEmpty();
    }

    @Test
    @DisplayName("Mor og far koblet sak, kant til kant. Automatisk innvilget utsettelse")
    @Description("Mor søker utsettelse pga arbeid og får automatisk innvilget, far søker kant i kant med mor og automatisk innvilges")
    void morOgFar_fødsel_ettArbeidsforholdHver_kobletsak_kantTilKant_med_automatisk_utsettelse() {
        var testscenario = opprettTestscenario("140");
        var morIdent = testscenario.personopplysninger().søkerIdent();
        var morAktørId = testscenario.personopplysninger().søkerAktørIdent();
        var farIdent = testscenario.personopplysninger().annenpartIdent();
        var farAktørId = testscenario.personopplysninger().annenpartAktørIdent();
        var fødselsdato = testscenario.personopplysninger().fødselsdato();
        var fpstartdatoMor = fødselsdato.minusWeeks(3);

        var fordelingMor = generiskFordeling(
                uttaksperiode(FORELDREPENGER_FØR_FØDSEL, fødselsdato.minusWeeks(3), fødselsdato.minusDays(1)),
                uttaksperiode(MØDREKVOTE, fødselsdato, fødselsdato.plusWeeks(10).minusDays(1)),
                utsettelsesperiode(SøknadUtsettelseÅrsak.ARBEID, fødselsdato.plusWeeks(10),
                        fødselsdato.plusWeeks(11).minusDays(1)),
                uttaksperiode(FELLESPERIODE, fødselsdato.plusWeeks(11), fødselsdato.plusWeeks(12).minusDays(1)));
        var søknadMor = lagSøknadForeldrepengerFødsel(fødselsdato, morAktørId, SøkersRolle.MOR)
                .medFordeling(fordelingMor)
                .medAnnenForelder(farAktørId);

        var saksnummerMor = fordel.sendInnSøknad(søknadMor.build(), morAktørId, morIdent,
                DokumenttypeId.SØKNAD_FORELDREPENGER_FØDSEL);

        var inntektsmeldingerMor = makeInntektsmeldingFromTestscenario(testscenario, fpstartdatoMor);
        fordel.sendInnInntektsmeldinger(inntektsmeldingerMor, morAktørId, morIdent, saksnummerMor);
        saksbehandler.hentFagsak(saksnummerMor);
        saksbehandler.ventTilAvsluttetBehandling();
        assertThat(saksbehandler.valgtBehandling.hentBehandlingsresultat())
                .as("Behandlingsresultat")
                .isEqualTo(BehandlingResultatType.INNVILGET);

        var fordelingFar = generiskFordeling(
                utsettelsesperiode(SøknadUtsettelseÅrsak.ARBEID, fødselsdato.plusWeeks(12), fødselsdato.plusWeeks(13).minusDays(1)),
                uttaksperiode(FEDREKVOTE, fødselsdato.plusWeeks(13), fødselsdato.plusWeeks(15).minusDays(1)));
        var søknadFar = lagSøknadForeldrepengerFødsel(fødselsdato, farAktørId, SøkersRolle.FAR)
                .medFordeling(fordelingFar)
                .medAnnenForelder(morAktørId);
        var saksnummerFar = fordel.sendInnSøknad(søknadFar.build(), farAktørId, farIdent,
                DokumenttypeId.SØKNAD_FORELDREPENGER_FØDSEL);

        var inntektsmeldingerFar = makeInntektsmeldingFromTestscenarioMedIdent(testscenario, farIdent,
                fødselsdato.plusWeeks(12), true);
        fordel.sendInnInntektsmeldinger(inntektsmeldingerFar, farAktørId, farIdent, saksnummerFar);
        saksbehandler.hentFagsak(saksnummerFar);
        saksbehandler.ventTilAvsluttetBehandling();
        assertThat(saksbehandler.valgtBehandling.hentBehandlingsresultat())
                .as("Behandlingsresultat")
                .isEqualTo(BehandlingResultatType.INNVILGET);
    }

    @Test
    @DisplayName("Far og mor søker fødsel med overlappende uttaksperiode")
    @Description("Mor søker og får innvilget. Far søker med to uker overlapp med mor (stjeling). Far får innvilget. " +
            "Berørt sak opprettet mor. Siste periode blir spittet i to og siste del blir avlsått. Det opprettes ikke" +
            "berørt sak på far.")
    void farOgMor_fødsel_ettArbeidsforholdHver_overlappendePeriode() {
        var testscenario = opprettTestscenario("82");
        var morIdent = testscenario.personopplysninger().søkerIdent();
        var morAktørId = testscenario.personopplysninger().søkerAktørIdent();
        var farIdent = testscenario.personopplysninger().annenpartIdent();
        var farAktørId = testscenario.personopplysninger().annenpartAktørIdent();
        var fødselsdato = testscenario.personopplysninger().fødselsdato();
        var fpstartdatoMor = fødselsdato.minusWeeks(3);
        var fpStartdatoFar = fødselsdato.plusWeeks(8);
        // MOR
        var fordelingMor = generiskFordeling(
                uttaksperiode(FORELDREPENGER_FØR_FØDSEL, fødselsdato.minusWeeks(3), fødselsdato.minusDays(1)),
                uttaksperiode(MØDREKVOTE, fødselsdato, fødselsdato.plusWeeks(10).minusDays(1)));
        var søknadMor = lagSøknadForeldrepengerFødsel(fødselsdato, morAktørId, SøkersRolle.MOR)
                .medFordeling(fordelingMor)
                .medAnnenForelder(farAktørId);
        var saksnummerMor = fordel.sendInnSøknad(søknadMor.build(), morAktørId, morIdent,
                DokumenttypeId.SØKNAD_FORELDREPENGER_FØDSEL);
        var inntektsmeldingerMor = makeInntektsmeldingFromTestscenario(testscenario, fpstartdatoMor);
        fordel.sendInnInntektsmeldinger(inntektsmeldingerMor, morAktørId, morIdent, saksnummerMor);
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
        var fordelingFar  = generiskFordeling(
                uttaksperiode(FEDREKVOTE, fødselsdato.plusWeeks(8), fødselsdato.plusWeeks(12)));
        var søknadFar = lagSøknadForeldrepengerFødsel(fødselsdato, farAktørId, SøkersRolle.FAR)
                .medFordeling(fordelingFar)
                .medAnnenForelder(morAktørId);
        var saksnummerFar = fordel.sendInnSøknad(søknadFar.build(), farAktørId, farIdent,
                DokumenttypeId.SØKNAD_FORELDREPENGER_FØDSEL);
        var inntektsmeldingerFar = makeInntektsmeldingFromTestscenarioMedIdent(testscenario,
                farIdent, fpStartdatoFar, true);
        fordel.sendInnInntektsmeldinger(inntektsmeldingerFar, farAktørId, farIdent, saksnummerFar);
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
        assertThat(saksbehandler.valgtBehandling.getSaldoer().getStonadskontoer().get(FEDREKVOTE).getSaldo())
                .as("Saldo fro stønadskonto FEDREKVOTE")
                .isPositive();
        assertThat(saksbehandler.valgtBehandling.getSaldoer().getStonadskontoer().get(MØDREKVOTE).getSaldo())
                .as("Saldo fro stønadskonto MØDREKVOTE")
                .isPositive();
        assertThat(saksbehandler.valgtBehandling.getSaldoer().getStonadskontoer().get(FELLESPERIODE).getSaldo())
                .as("Saldo fro stønadskonto FELLESPERIODE")
                .isEqualTo(80);
        assertThat(saksbehandler.valgtBehandling.getSaldoer().getStonadskontoer().get(FORELDREPENGER_FØR_FØDSEL).getSaldo())
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
        var testscenario = opprettTestscenario("84");
        var saksnummerMor = behandleSøknadForMorUtenOverlapp(testscenario, LocalDate.now().minusMonths(4));
        var saksnummerFar = behandleSøknadForFarUtenOverlapp(testscenario, LocalDate.now().minusMonths(4));

        saksbehandler.hentFagsak(saksnummerMor);
        assertThat(saksbehandler.harRevurderingBehandling())
                .as("Har revurdert behandling")
                .isFalse();

        sendInnEndringssøknadforMor(testscenario, saksnummerMor);

        saksbehandler.hentFagsak(saksnummerMor);
        saksbehandler.ventPåOgVelgRevurderingBehandling();

        var vurderSoknadsfristForeldrepengerBekreftelse = saksbehandler
                .hentAksjonspunktbekreftelse(VurderSoknadsfristForeldrepengerBekreftelse.class)
                .bekreftHarGyldigGrunn(LocalDate.now().minusMonths(4));
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
    @DisplayName("Mor får revurdering fra endringssøknad vedtak opphører")
    @Description("Mor får revurdering fra endringssøknad vedtak opphører - far får revurdering")
    void BerørtSakOpphør() {
        var testscenario = opprettTestscenario("84");
        var saksnummerMor = behandleSøknadForMorUtenOverlapp(testscenario, LocalDate.now().minusMonths(4));
        var saksnummerFar = behandleSøknadForFarUtenOverlapp(testscenario, LocalDate.now().minusMonths(4));

        sendInnEndringssøknadforMor(testscenario, saksnummerMor);

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
        var testscenario = opprettTestscenario("84");
        var saksnummerMor = behandleSøknadForMorUtenOverlapp(testscenario, LocalDate.now().minusMonths(4));
        var saksnummerFar = behandleSøknadForFarUtenOverlapp(testscenario, LocalDate.now().minusMonths(4));
        sendInnEndringssøknadforMorMedEndretUttak(testscenario, saksnummerMor);

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
        var testscenario = opprettTestscenario("84");
        var fødselsdato = LocalDate.now().minusDays(15);
        behandleSøknadForFarSattPåVent(testscenario, fødselsdato);
        var saksnummerMor = behandleSøknadForMorUregistrert(testscenario, fødselsdato);

        saksbehandler.hentFagsak(saksnummerMor);

        saksbehandler.opprettBehandlingRevurdering(BehandlingÅrsakType.RE_OPPLYSNINGER_OM_FORDELING);
        saksbehandler.ventPåOgVelgRevurderingBehandling();

        saksbehandler.bekreftAksjonspunktbekreftelserer(
                saksbehandler.hentAksjonspunktbekreftelse(KontrollerManueltOpprettetRevurdering.class),
                saksbehandler.hentAksjonspunktbekreftelse(ForeslåVedtakManueltBekreftelse.class));
        saksbehandler.ventTilAvsluttetBehandling();
    }

    @Step("Behandle søknad for mor uregistrert")
    private long behandleSøknadForMorUregistrert(TestscenarioDto testscenario, LocalDate fødselsdato) {
        var saksnummer = sendInnSøknadOgInntektMor(testscenario, fødselsdato);

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

    @Step("Behandle søknad for mor uten overlapp")
    private long behandleSøknadForMorUtenOverlapp(TestscenarioDto testscenario, LocalDate fødselsdato) {
        var saksnummer = sendInnSøknadOgInntektMor(testscenario, fødselsdato);

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

    @Step("Behandle søknad for far uten overlapp")
    private long behandleSøknadForFarUtenOverlapp(TestscenarioDto testscenario, LocalDate fødselsdato) {
        var saksnummer = sendInnSøknadOgInntektFar(testscenario, fødselsdato, fødselsdato.plusWeeks(10).plusDays(1));

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

        return saksnummer;
    }

    @Step("Send inn endringssøknad for mor")
    private void sendInnEndringssøknadforMor(TestscenarioDto testscenario, long saksnummerMor) {
        var søkerAktørid = testscenario.personopplysninger().søkerAktørIdent();
        var søkerIdent = testscenario.personopplysninger().søkerIdent();
        var fødselsdato = LocalDate.now().minusMonths(4);
        var fordeling = FordelingErketyper.fordelingMorHappyCase(fødselsdato);
        var søknad = lagEndringssøknad(søkerAktørid, SøkersRolle.MOR, fordeling, saksnummerMor);
        fordel.sendInnSøknad(søknad.build(), søkerAktørid, søkerIdent, DokumenttypeId.FORELDREPENGER_ENDRING_SØKNAD,
                saksnummerMor);
    }

    @Step("Send inn endringssøknad for mor med endret uttak")
    private void sendInnEndringssøknadforMorMedEndretUttak(TestscenarioDto testscenario, long saksnummerMor) {
        var søkerAktørid = testscenario.personopplysninger().søkerAktørIdent();
        var søkerIdent = testscenario.personopplysninger().søkerIdent();
        var fødselsdato = LocalDate.now().minusMonths(4);
        var fordeling = FordelingErketyper.fordelingMorHappyCaseLong(fødselsdato);
        var søknad = lagEndringssøknad(søkerAktørid, SøkersRolle.MOR, fordeling, saksnummerMor);
        fordel.sendInnSøknad(søknad.build(), søkerAktørid, søkerIdent, DokumenttypeId.FORELDREPENGER_ENDRING_SØKNAD,
                saksnummerMor);
    }

    @Step("Behandle søknad for far satt på vent")
    private long behandleSøknadForFarSattPåVent(TestscenarioDto testscenario, LocalDate fødselsdato) {
        var saksnummer = sendInnSøknadOgInntektFar(testscenario, fødselsdato, fødselsdato.plusWeeks(10).plusDays(1));

        saksbehandler.hentFagsak(saksnummer);
        assertThat(saksbehandler.valgtBehandling.erSattPåVent())
                .as("Behandling satt på vent (Behandling ble ikke satt på vent selv om far har søkt for tidlig)")
                .isTrue();

        return saksnummer;
    }

    @Step("Send inn søknad og inntekt mor")
    private long sendInnSøknadOgInntektMor(TestscenarioDto testscenario, LocalDate fødselsdato) {
        var saksnummer = sendInnSøknadMor(testscenario, fødselsdato);
        sendInnInntektsmeldingMor(testscenario, fødselsdato, saksnummer);
        return saksnummer;
    }

    @Step("Send inn søknad mor: fødsel funnet sted mor med far")
    private long sendInnSøknadMor(TestscenarioDto testscenario, LocalDate fødselsdato) {
        var søkerAktørid = testscenario.personopplysninger().søkerAktørIdent();
        var søkerIdent = testscenario.personopplysninger().søkerIdent();
        var annenPartAktørid = testscenario.personopplysninger().annenpartAktørIdent();
        var søknad = lagSøknadForeldrepengerFødsel(fødselsdato, søkerAktørid, SøkersRolle.MOR)
                .medFordeling(FordelingErketyper.fordelingMorHappyCase(fødselsdato))
                .medAnnenForelder(annenPartAktørid);
        return fordel.sendInnSøknad(søknad.build(), søkerAktørid, søkerIdent,
                DokumenttypeId.SØKNAD_FORELDREPENGER_FØDSEL);
    }

    @Step("Send inn søknad mor med aksjonspunkt")
    private long sendInnSøknadMorMedAksjonspunkt(TestscenarioDto testscenario, LocalDate fødselsdato) {
        var søkerAktørid = testscenario.personopplysninger().søkerAktørIdent();
        var søkerIdent = testscenario.personopplysninger().søkerIdent();
        var annenPartAktørid = testscenario.personopplysninger().annenpartAktørIdent();
        var fpStartdato = fødselsdato.minusWeeks(3);

        var fordeling = generiskFordeling(
                uttaksperiode(FORELDREPENGER_FØR_FØDSEL, fpStartdato, fødselsdato.minusDays(1)),
                utsettelsesperiode(SøknadUtsettelseÅrsak.ARBEID, fødselsdato,fødselsdato.plusWeeks(6).minusDays(1)));
        var søknad = lagSøknadForeldrepengerFødsel(fødselsdato, søkerAktørid, SøkersRolle.MOR)
                .medFordeling(fordeling)
                .medAnnenForelder(annenPartAktørid);
        return fordel.sendInnSøknad(søknad.build(), søkerAktørid, søkerIdent,
                DokumenttypeId.SØKNAD_FORELDREPENGER_FØDSEL);
    }

    @Step("Send inn inntektsmelding mor")
    private long sendInnInntektsmeldingMor(TestscenarioDto testscenario, LocalDate fødselsdato, Long saksnummer) {
        var søkerAktørid = testscenario.personopplysninger().søkerAktørIdent();
        var søkerIdent = testscenario.personopplysninger().søkerIdent();
        var startDatoForeldrepenger = fødselsdato.minusWeeks(3);

        var inntekter = sorterteInntektsbeløp(testscenario);
        var orgnr = testscenario.scenariodataAnnenpartDto().arbeidsforholdModell().arbeidsforhold().get(0)
                .arbeidsgiverOrgnr();

        var inntektsmelding = lagInntektsmelding(
                inntekter.get(0),
                søkerIdent,
                startDatoForeldrepenger,
                orgnr);
        return fordel.sendInnInntektsmelding(inntektsmelding, søkerAktørid, søkerIdent, saksnummer);
    }

    @Step("Send inn søknad og inntektsmelding far")
    private long sendInnSøknadOgInntektFar(TestscenarioDto testscenario, LocalDate fødselsdato,
            LocalDate startDatoForeldrepenger) {
        var saksnummer = sendInnSøknadFar(testscenario, fødselsdato, startDatoForeldrepenger);
        sendInnInntektsmeldingFar(testscenario, fødselsdato, startDatoForeldrepenger, saksnummer);
        return saksnummer;
    }

    @Step("Send inn søknad far")
    private long sendInnSøknadFar(TestscenarioDto testscenario, LocalDate fødselsdato,
            LocalDate startDatoForeldrepenger) {
        var søkerAktørid = testscenario.personopplysninger().annenpartAktørIdent();
        var søkerIdent = testscenario.personopplysninger().annenpartIdent();
        var annenPartAktørid = testscenario.personopplysninger().søkerAktørIdent();

        var fordeling = generiskFordeling(
                uttaksperiode(FEDREKVOTE, startDatoForeldrepenger, startDatoForeldrepenger.plusWeeks(2)));
        var søknad = lagSøknadForeldrepengerFødsel(fødselsdato, søkerAktørid, SøkersRolle.FAR)
                .medFordeling(fordeling)
                .medAnnenForelder(annenPartAktørid);
        return fordel.sendInnSøknad(søknad.build(), søkerAktørid, søkerIdent,
                DokumenttypeId.SØKNAD_FORELDREPENGER_FØDSEL);
    }

    @Step("Send inn inntektsmelding far")
    private long sendInnInntektsmeldingFar(TestscenarioDto testscenario, LocalDate fødselsdato,
            LocalDate startDatoForeldrepenger, Long saksnummer) {
        var søkerAktørid = testscenario.personopplysninger().annenpartAktørIdent();
        var søkerIdent = testscenario.personopplysninger().annenpartIdent();

        var inntekter = sorterteInntektsbeløp(testscenario);
        var orgnr = testscenario.scenariodataDto().arbeidsforholdModell().arbeidsforhold().get(0)
                .arbeidsgiverOrgnr();

        var inntektsmelding = lagInntektsmelding(
                inntekter.get(0),
                søkerIdent,
                startDatoForeldrepenger,
                orgnr);
        return fordel.sendInnInntektsmelding(inntektsmelding, søkerAktørid, søkerIdent, saksnummer);
    }

}
