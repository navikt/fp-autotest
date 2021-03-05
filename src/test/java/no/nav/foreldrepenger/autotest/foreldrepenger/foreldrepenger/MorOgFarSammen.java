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

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.qameta.allure.Description;
import io.qameta.allure.Step;
import no.nav.foreldrepenger.autotest.base.ForeldrepengerTestBase;
import no.nav.foreldrepenger.autotest.dokumentgenerator.foreldrepengesoknad.SøkersRolle;
import no.nav.foreldrepenger.autotest.dokumentgenerator.foreldrepengesoknad.builders.EndringssøknadBuilder;
import no.nav.foreldrepenger.autotest.dokumentgenerator.foreldrepengesoknad.builders.ForeldrepengerBuilder;
import no.nav.foreldrepenger.autotest.dokumentgenerator.inntektsmelding.builders.InntektsmeldingBuilder;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.Avslagsårsak;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.BehandlingResultatType;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.BehandlingÅrsakType;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.FagsakStatus;
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
import no.nav.foreldrepenger.vtp.kontrakter.TestscenarioDto;
import no.nav.foreldrepenger.vtp.testmodell.dokument.modell.koder.DokumenttypeId;
import no.nav.vedtak.felles.xml.soeknad.uttak.v3.Fordeling;

@Tag("fpsak")
@Tag("foreldrepenger")
@Tag("fluoritt")
public class MorOgFarSammen extends ForeldrepengerTestBase {

    private static final Logger logger = LoggerFactory.getLogger(MorOgFarSammen.class);

    @Test
    @DisplayName("Mor og far koblet sak, kant til kant")
    @Description("Mor søker, får AP slik at behandling stopper opp. Far sender søknad og blir satt på vent. Behandler " +
            "ferdig mor sin søknad (positivt vedtak). Behandler far sin søknad (positivt vedtak). Ingen overlapp. " +
            "Verifiserer at sakene er koblet og at det ikke opprettes revurdering berørt sak.")
    public void morOgFar_fødsel_ettArbeidsforholdHver_kobletsak_kantTilKant() {
        TestscenarioDto testscenario = opprettTestscenario("82");
        String morIdent = testscenario.personopplysninger().søkerIdent();
        String morAktørId = testscenario.personopplysninger().søkerAktørIdent();
        String farIdent = testscenario.personopplysninger().annenpartIdent();
        String farAktørId = testscenario.personopplysninger().annenpartAktørIdent();
        LocalDate fødselsdato = testscenario.personopplysninger().fødselsdato();
        LocalDate fpstartdatoMor = fødselsdato.minusWeeks(3);
        LocalDate fpstartdatoFar = fødselsdato.plusWeeks(6);

        long saksnummerMor = sendInnSøknadMorMedAksjonspunkt(testscenario, fødselsdato);
        List<InntektsmeldingBuilder> inntektsmeldingerMor = makeInntektsmeldingFromTestscenario(testscenario, fpstartdatoMor);
        fordel.sendInnInntektsmeldinger(inntektsmeldingerMor, morAktørId, morIdent, saksnummerMor);
        saksbehandler.hentFagsak(saksnummerMor);
        verifiserLikhet(saksbehandler.valgtFagsak.status(), FagsakStatus.UNDER_BEHANDLING, "Fagsakstatus sak mor");
        long saksnummerFar = sendInnSøknadFar(testscenario, fødselsdato, fpstartdatoFar);
        saksbehandler.hentFagsak(saksnummerFar);
        debugLoggBehandlingsliste(saksbehandler.behandlinger);
        verifiserLikhet(saksbehandler.valgtFagsak.status(), FagsakStatus.UNDER_BEHANDLING, "Fagsakstatus sak far");
        verifiser(saksbehandler.valgtBehandling.erSattPåVent(), "Behandlingen er ikke på vent.");

        // Behandle ferdig mor sin sak
        saksbehandler.hentFagsak(saksnummerMor);
        debugLoggBehandlingsliste("mors behandlinger", saksbehandler.behandlinger);
        FastsettUttaksperioderManueltBekreftelse fastsettUttaksperioderManueltBekreftelse = saksbehandler
                .hentAksjonspunktbekreftelse(FastsettUttaksperioderManueltBekreftelse.class)
                .innvilgManuellePerioder();
        saksbehandler.bekreftAksjonspunkt(fastsettUttaksperioderManueltBekreftelse);
        saksbehandler.bekreftAksjonspunktMedDefaultVerdier(ForeslåVedtakBekreftelse.class);
        beslutter.hentFagsak(saksnummerMor);
        var bekreftelse = beslutter.hentAksjonspunktbekreftelse(FatterVedtakBekreftelse.class);
        bekreftelse.godkjennAksjonspunkter(beslutter.hentAksjonspunktSomSkalTilTotrinnsBehandling());
        beslutter.fattVedtakOgVentTilAvsluttetBehandling(bekreftelse);
        beslutter.ventTilFagsakLøpende();
        verifiserLikhet(saksbehandler.valgtBehandling.hentBehandlingsresultat(), BehandlingResultatType.INNVILGET);
        debugFritekst("Ferdig med behandling mor");

        // Behandle ferdig far sin sak
        List<InntektsmeldingBuilder> inntektsmeldingerFar = makeInntektsmeldingFromTestscenarioMedIdent(testscenario,
                farIdent, fpstartdatoFar, true);
        fordel.sendInnInntektsmeldinger(inntektsmeldingerFar, farAktørId, farIdent, saksnummerFar);
        saksbehandler.hentFagsak(saksnummerFar);
        verifiser(saksbehandler.sakErKobletTilAnnenpart(), "Saken er ikke koblet til en annen behandling");
        saksbehandler.ventTilAvsluttetBehandling();
        verifiserLikhet(saksbehandler.valgtBehandling.hentBehandlingsresultat(), BehandlingResultatType.INNVILGET);
        debugFritekst("Ferdig med behandling far");

        // Verifisere at det ikke er blitt opprettet revurdering berørt sak på mor
        saksbehandler.hentFagsak(saksnummerMor);
        verifiser(saksbehandler.behandlinger.size() == 1, "Mor har for mange behandlinger.");
    }

    @Test
    @DisplayName("Mor og far koblet sak, kant til kant. Automatisk innvilget utsettelse")
    @Description("Mor søker utsettelse pga arbeid og får automatisk innvilget, far søker kant i kant med mor og automatisk innvilges")
    public void morOgFar_fødsel_ettArbeidsforholdHver_kobletsak_kantTilKant_med_automatisk_utsettelse() {
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
        verifiserLikhet(saksbehandler.valgtBehandling.hentBehandlingsresultat(), BehandlingResultatType.INNVILGET);

        var fordelingFar = generiskFordeling(
                utsettelsesperiode(SøknadUtsettelseÅrsak.ARBEID, fødselsdato.plusWeeks(12),
                        fødselsdato.plusWeeks(13).minusDays(1)),
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
        verifiserLikhet(saksbehandler.valgtBehandling.hentBehandlingsresultat(), BehandlingResultatType.INNVILGET);
    }

    @Test
    @DisplayName("Far og mor søker fødsel med overlappende uttaksperiode")
    @Description("Mor søker og får innvilget. Far søker med to uker overlapp med mor (stjeling). Far får innvilget. " +
            "Berørt sak opprettet mor. Siste periode blir spittet i to og siste del blir avlsått. Det opprettes ikke" +
            "berørt sak på far.")
    public void farOgMor_fødsel_ettArbeidsforholdHver_overlappendePeriode() {
        TestscenarioDto testscenario = opprettTestscenario("82");
        String morIdent = testscenario.personopplysninger().søkerIdent();
        String morAktørId = testscenario.personopplysninger().søkerAktørIdent();
        String farIdent = testscenario.personopplysninger().annenpartIdent();
        String farAktørId = testscenario.personopplysninger().annenpartAktørIdent();
        LocalDate fødselsdato = testscenario.personopplysninger().fødselsdato();
        LocalDate fpstartdatoMor = fødselsdato.minusWeeks(3);
        LocalDate fpStartdatoFar = fødselsdato.plusWeeks(8);
        // MOR
        Fordeling fordelingMor = generiskFordeling(
                uttaksperiode(FORELDREPENGER_FØR_FØDSEL, fødselsdato.minusWeeks(3), fødselsdato.minusDays(1)),
                uttaksperiode(MØDREKVOTE, fødselsdato, fødselsdato.plusWeeks(10).minusDays(1)));
        ForeldrepengerBuilder søknadMor = lagSøknadForeldrepengerFødsel(fødselsdato, morAktørId, SøkersRolle.MOR)
                .medFordeling(fordelingMor)
                .medAnnenForelder(farAktørId);
        long saksnummerMor = fordel.sendInnSøknad(søknadMor.build(), morAktørId, morIdent,
                DokumenttypeId.SØKNAD_FORELDREPENGER_FØDSEL);
        List<InntektsmeldingBuilder> inntektsmeldingerMor = makeInntektsmeldingFromTestscenario(testscenario, fpstartdatoMor);
        fordel.sendInnInntektsmeldinger(inntektsmeldingerMor, morAktørId, morIdent, saksnummerMor);
        saksbehandler.hentFagsak(saksnummerMor);
        saksbehandler.ventTilAvsluttetBehandling();
        debugFritekst("Ferdig med første behandling mor");
        verifiserLikhet(saksbehandler.valgtBehandling.hentBehandlingsresultat(), BehandlingResultatType.INNVILGET);
        verifiser(saksbehandler.valgtBehandling.getUttakResultatPerioder().getPerioderSøker().size() == 3,
                "Antall perioder for mor er ikke 3.");
        verifiser(saksbehandler.valgtBehandling.getSaldoer().getStonadskontoer().size() == 4,
                "Feil antall stønadskontoer.");
        // FAR
        Fordeling fordelingFar  = generiskFordeling(
                uttaksperiode(FEDREKVOTE, fødselsdato.plusWeeks(8), fødselsdato.plusWeeks(12)));
        ForeldrepengerBuilder søknadFar = lagSøknadForeldrepengerFødsel(fødselsdato, farAktørId, SøkersRolle.FAR)
                .medFordeling(fordelingFar)
                .medAnnenForelder(morAktørId);
        long saksnummerFar = fordel.sendInnSøknad(søknadFar.build(), farAktørId, farIdent,
                DokumenttypeId.SØKNAD_FORELDREPENGER_FØDSEL);
        List<InntektsmeldingBuilder> inntektsmeldingerFar = makeInntektsmeldingFromTestscenarioMedIdent(testscenario,
                farIdent, fpStartdatoFar, true);
        fordel.sendInnInntektsmeldinger(inntektsmeldingerFar, farAktørId, farIdent, saksnummerFar);
        saksbehandler.hentFagsak(saksnummerFar);
        saksbehandler.ventTilAvsluttetBehandling();
        debugFritekst("Ferdig med første behandling til far");
        verifiserLikhet(saksbehandler.valgtBehandling.hentBehandlingsresultat(), BehandlingResultatType.INNVILGET);
        verifiser(saksbehandler.sakErKobletTilAnnenpart(), "Saken er ikke koblet til mor sin behandling");
        verifiser(saksbehandler.valgtBehandling.hentUttaksperioder().size() == 2, "Antall perioder er ikke 2.");
        verifiser(saksbehandler.valgtBehandling.getSaldoer().getStonadskontoer().size() == 4,
                "Feil antall stønadskontoer.");
        // Revurdering berørt sak mor
        saksbehandler.hentFagsak(saksnummerMor);
        saksbehandler.ventPåOgVelgRevurderingBehandling();
        debugFritekst("Revurdering berørt sak opprettet på mor.");
        verifiser(saksbehandler.sakErKobletTilAnnenpart(), "Saken er ikke koblet til en far sin behandling");
        verifiser(saksbehandler.valgtBehandling.hentUttaksperioder().size() == 4, "Feil i splitting av mors perioder.");
        verifiser(saksbehandler.valgtBehandling.getSaldoer().getStonadskontoer().get(FEDREKVOTE).getSaldo() > 0,
                "Feil i stønadsdager fedrekvote.");
        verifiser(saksbehandler.valgtBehandling.getSaldoer().getStonadskontoer().get(MØDREKVOTE).getSaldo() > 0,
                "Feil i stønadsdager mødrekvote.");
        verifiser(saksbehandler.valgtBehandling.getSaldoer().getStonadskontoer().get(FELLESPERIODE).getSaldo() == 80,
                "Feil i stønadsdager fellesperiode.");
        verifiser(saksbehandler.valgtBehandling.getSaldoer().getStonadskontoer().get(FORELDREPENGER_FØR_FØDSEL)
                .getSaldo() >= 0, "Feil i stønadsdager FØR_FØDSEL.");
        // verifiser ikke berørt sak far
        saksbehandler.hentFagsak(saksnummerFar);
        verifiser(saksbehandler.behandlinger.size() == 1, "Feil antall behandlinger i fagsak til far.");

    }

    @Test
    @DisplayName("Koblet sak endringssøknad ingen endring")
    @Description("Sender inn søknad mor. Sender inn søknad far uten overlapp. Sender inn endringssøknad mor som er lik " +
                 "førstegangsbehandlingen. Verifiserer at det ikke blir berørt sak på far.")
    public void KobletSakIngenEndring() {
        TestscenarioDto testscenario = opprettTestscenario("84");
        long saksnummerMor = behandleSøknadForMorUtenOverlapp(testscenario, LocalDate.now().minusMonths(4));
        long saksnummerFar = behandleSøknadForFarUtenOverlapp(testscenario, LocalDate.now().minusMonths(4));

        saksbehandler.hentFagsak(saksnummerMor);
        verifiser(!saksbehandler.harRevurderingBehandling(),
                "Mor har fått revurdering uten endringssøknad eller endring i behandling");

        sendInnEndringssøknadforMor(testscenario, saksnummerMor);

        saksbehandler.hentFagsak(saksnummerMor);
        saksbehandler.ventPåOgVelgRevurderingBehandling();

        VurderSoknadsfristForeldrepengerBekreftelse vurderSoknadsfristForeldrepengerBekreftelse = saksbehandler
                .hentAksjonspunktbekreftelse(VurderSoknadsfristForeldrepengerBekreftelse.class);
        vurderSoknadsfristForeldrepengerBekreftelse.bekreftHarGyldigGrunn(LocalDate.now().minusMonths(4));
        saksbehandler.bekreftAksjonspunkt(vurderSoknadsfristForeldrepengerBekreftelse);

        saksbehandler.bekreftAksjonspunktMedDefaultVerdier(ForeslåVedtakBekreftelse.class);


        beslutter.hentFagsak(saksnummerMor);
        beslutter.ventPåOgVelgRevurderingBehandling();
        var bekreftelse = beslutter.hentAksjonspunktbekreftelse(FatterVedtakBekreftelse.class);
        bekreftelse.godkjennAksjonspunkter(beslutter.hentAksjonspunktSomSkalTilTotrinnsBehandling());
        beslutter.fattVedtakOgVentTilAvsluttetBehandling(bekreftelse);
        verifiserLikhet(beslutter.valgtBehandling.hentBehandlingsresultat(), BehandlingResultatType.INGEN_ENDRING);

        saksbehandler.hentFagsak(saksnummerFar);
        verifiser(!saksbehandler.harRevurderingBehandling(),
                "Fars behandling fikk revurdering selv uten endringer i mors behandling av endringssøknaden");
    }

    @Test
    @DisplayName("Mor får revurdering fra endringssøknad vedtak opphører")
    @Description("Mor får revurdering fra endringssøknad vedtak opphører - far får revurdering")
    public void BerørtSakOpphør() {
        TestscenarioDto testscenario = opprettTestscenario("84");
        long saksnummerMor = behandleSøknadForMorUtenOverlapp(testscenario, LocalDate.now().minusMonths(4));
        long saksnummerFar = behandleSøknadForFarUtenOverlapp(testscenario, LocalDate.now().minusMonths(4));

        sendInnEndringssøknadforMor(testscenario, saksnummerMor);

        overstyrer.hentFagsak(saksnummerMor);
        overstyrer.ventPåOgVelgRevurderingBehandling();

        OverstyrFodselsvilkaaret overstyr = new OverstyrFodselsvilkaaret();
        overstyr.avvis(Avslagsårsak.SØKER_ER_FAR);
        overstyr.setBegrunnelse("avvist");
        overstyrer.overstyr(overstyr);

        VurderSoknadsfristForeldrepengerBekreftelse vurderSoknadsfristForeldrepengerBekreftelse = overstyrer
                .hentAksjonspunktbekreftelse(VurderSoknadsfristForeldrepengerBekreftelse.class);
        vurderSoknadsfristForeldrepengerBekreftelse.bekreftHarGyldigGrunn(LocalDate.now().minusMonths(4));
        overstyrer.bekreftAksjonspunkt(vurderSoknadsfristForeldrepengerBekreftelse);

        overstyrer.bekreftAksjonspunktMedDefaultVerdier(ForeslåVedtakBekreftelse.class);

        beslutter.hentFagsak(saksnummerMor);
        beslutter.ventPåOgVelgRevurderingBehandling();
        var bekreftelse = beslutter.hentAksjonspunktbekreftelse(FatterVedtakBekreftelse.class);
        bekreftelse.godkjennAksjonspunkter(beslutter.hentAksjonspunktSomSkalTilTotrinnsBehandling());
        beslutter.fattVedtakOgVentTilAvsluttetBehandling(bekreftelse);
        verifiserLikhet(beslutter.valgtBehandling.hentBehandlingsresultat(), BehandlingResultatType.OPPHØR);

        saksbehandler.hentFagsak(saksnummerFar);
        saksbehandler.ventPåOgVelgRevurderingBehandling();
        verifiserLikhet(saksbehandler.valgtBehandling.hentBehandlingsresultat(), BehandlingResultatType.INGEN_ENDRING);
    }

    @Test
    @DisplayName("Mor får revurdering fra endringssøknad endring av uttak")
    @Description("Mor får revurdering fra endringssøknad endring av uttak - fører til revurdering hos far")
    public void BerørtSakEndringAvUttak() {
        TestscenarioDto testscenario = opprettTestscenario("84");
        long saksnummerMor = behandleSøknadForMorUtenOverlapp(testscenario, LocalDate.now().minusMonths(4));
        long saksnummerFar = behandleSøknadForFarUtenOverlapp(testscenario, LocalDate.now().minusMonths(4));
        sendInnEndringssøknadforMorMedEndretUttak(testscenario, saksnummerMor);

        saksbehandler.hentFagsak(saksnummerMor);

        saksbehandler.ventPåOgVelgRevurderingBehandling();

        var vurderSoknadsfristForeldrepengerBekreftelse = saksbehandler
                .hentAksjonspunktbekreftelse(VurderSoknadsfristForeldrepengerBekreftelse.class);
        vurderSoknadsfristForeldrepengerBekreftelse.bekreftHarGyldigGrunn(LocalDate.now().minusMonths(4));
        saksbehandler.bekreftAksjonspunkt(vurderSoknadsfristForeldrepengerBekreftelse);

        logger.debug("Date start: " + LocalDate.now().minusMonths(4).plusWeeks(6).plusDays(1));
        logger.debug("Date start: " + LocalDate.now().minusMonths(4).plusWeeks(10).minusDays(2));

        saksbehandler.bekreftAksjonspunktMedDefaultVerdier(ForeslåVedtakBekreftelse.class);


        beslutter.hentFagsak(saksnummerMor);
        beslutter.ventPåOgVelgRevurderingBehandling();
        var bekreftelse = beslutter.hentAksjonspunktbekreftelse(FatterVedtakBekreftelse.class);
        bekreftelse.godkjennAksjonspunkter(beslutter.hentAksjonspunktSomSkalTilTotrinnsBehandling());
        beslutter.fattVedtakOgVentTilAvsluttetBehandling(bekreftelse);
        verifiserLikhet(beslutter.valgtBehandling.behandlingsresultat.getKonsekvenserForYtelsen().get(0).kode,
                "ENDRING_I_UTTAK");

        saksbehandler.hentFagsak(saksnummerFar);
        saksbehandler.ventPåOgVelgRevurderingBehandling();
        verifiserLikhet(saksbehandler.valgtBehandling.behandlingsresultat.getKonsekvenserForYtelsen().get(0).kode,
                "ENDRING_I_UTTAK");
    }

    @Test
    @DisplayName("Koblet sak mor søker etter far og sniker i køen")
    @Description("Far søker. Blir satt på vent pga for tidlig søknad. Mor søker og får innvilget. Oppretter manuell " +
                 "revurdering på mor. ")
    public void KobletSakMorSøkerEtterFar() {
        TestscenarioDto testscenario = opprettTestscenario("84");
        LocalDate fødselsdato = LocalDate.now().minusDays(15);
        behandleSøknadForFarSattPåVent(testscenario, fødselsdato);
        long saksnummerMor = behandleSøknadForMorUregistrert(testscenario, fødselsdato);

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
        long saksnummer = sendInnSøknadOgInntektMor(testscenario, fødselsdato);

        saksbehandler.hentFagsak(saksnummer);

        VurderManglendeFodselBekreftelse vurderManglendeFodselBekreftelse = saksbehandler
                .hentAksjonspunktbekreftelse(VurderManglendeFodselBekreftelse.class);
        vurderManglendeFodselBekreftelse.bekreftDokumentasjonForeligger(1, fødselsdato);
        saksbehandler.bekreftAksjonspunkt(vurderManglendeFodselBekreftelse);

        saksbehandler.bekreftAksjonspunktMedDefaultVerdier(ForeslåVedtakBekreftelse.class);


        beslutter.hentFagsak(saksnummer);
        FatterVedtakBekreftelse bekreftelse = beslutter.hentAksjonspunktbekreftelse(FatterVedtakBekreftelse.class);
        bekreftelse.godkjennAksjonspunkt(saksbehandler.hentAksjonspunkt(AksjonspunktKoder.SJEKK_MANGLENDE_FØDSEL));
        beslutter.fattVedtakOgVentTilAvsluttetBehandling(bekreftelse);

        return saksnummer;
    }

    @Step("Behandle søknad for mor uten overlapp")
    private long behandleSøknadForMorUtenOverlapp(TestscenarioDto testscenario, LocalDate fødselsdato) {
        long saksnummer = sendInnSøknadOgInntektMor(testscenario, fødselsdato);

        saksbehandler.hentFagsak(saksnummer);

        VurderManglendeFodselBekreftelse vurderManglendeFodselBekreftelse = saksbehandler
                .hentAksjonspunktbekreftelse(VurderManglendeFodselBekreftelse.class);
        vurderManglendeFodselBekreftelse.bekreftDokumentasjonForeligger(1, fødselsdato);
        saksbehandler.bekreftAksjonspunkt(vurderManglendeFodselBekreftelse);

        VurderSoknadsfristForeldrepengerBekreftelse vurderSoknadsfristForeldrepengerBekreftelse = saksbehandler
                .hentAksjonspunktbekreftelse(VurderSoknadsfristForeldrepengerBekreftelse.class);
        vurderSoknadsfristForeldrepengerBekreftelse.bekreftHarGyldigGrunn(LocalDate.now().minusMonths(4));
        saksbehandler.bekreftAksjonspunkt(vurderSoknadsfristForeldrepengerBekreftelse);

        saksbehandler.bekreftAksjonspunktMedDefaultVerdier(ForeslåVedtakBekreftelse.class);

        beslutter.hentFagsak(saksnummer);
        var bekreftelse = beslutter.hentAksjonspunktbekreftelse(FatterVedtakBekreftelse.class);
        bekreftelse.godkjennAksjonspunkter(beslutter.hentAksjonspunktSomSkalTilTotrinnsBehandling());
        beslutter.fattVedtakOgVentTilAvsluttetBehandling(bekreftelse);
        verifiserLikhet(saksbehandler.valgtBehandling.hentBehandlingsresultat(), BehandlingResultatType.INNVILGET);

        return saksnummer;
    }

    @Step("Behandle søknad for far uten overlapp")
    private long behandleSøknadForFarUtenOverlapp(TestscenarioDto testscenario, LocalDate fødselsdato) {
        long saksnummer = sendInnSøknadOgInntektFar(testscenario, fødselsdato, fødselsdato.plusWeeks(10).plusDays(1));

        saksbehandler.hentFagsak(saksnummer);

        verifiser(saksbehandler.sakErKobletTilAnnenpart(), "Saken er ikke koblet til en annen behandling");

        VurderManglendeFodselBekreftelse vurderManglendeFodselBekreftelse = saksbehandler
                .hentAksjonspunktbekreftelse(VurderManglendeFodselBekreftelse.class);
        vurderManglendeFodselBekreftelse
                .bekreftDokumentasjonForeligger(1, LocalDate.now().minusMonths(4));
        saksbehandler.bekreftAksjonspunkt(vurderManglendeFodselBekreftelse);

        if (saksbehandler.harAksjonspunkt("5020"))
            saksbehandler.bekreftAksjonspunktMedDefaultVerdier(AvklarBrukerBosattBekreftelse.class);

        saksbehandler.bekreftAksjonspunktMedDefaultVerdier(ForeslåVedtakBekreftelse.class);

        beslutter.hentFagsak(saksnummer);
        var bekreftelse = beslutter.hentAksjonspunktbekreftelse(FatterVedtakBekreftelse.class);
        bekreftelse.godkjennAksjonspunkter(beslutter.hentAksjonspunktSomSkalTilTotrinnsBehandling());
        beslutter.fattVedtakOgVentTilAvsluttetBehandling(bekreftelse);
        verifiserLikhet(saksbehandler.valgtBehandling.hentBehandlingsresultat(), BehandlingResultatType.INNVILGET);

        return saksnummer;
    }

    @Step("Send inn endringssøknad for mor")
    private void sendInnEndringssøknadforMor(TestscenarioDto testscenario, long saksnummerMor) {
        String søkerAktørid = testscenario.personopplysninger().søkerAktørIdent();
        String søkerIdent = testscenario.personopplysninger().søkerIdent();
        LocalDate fødselsdato = LocalDate.now().minusMonths(4);
        Fordeling fordeling = FordelingErketyper.fordelingMorHappyCase(fødselsdato);
        EndringssøknadBuilder søknad = lagEndringssøknad(søkerAktørid, SøkersRolle.MOR, fordeling, saksnummerMor);
        fordel.sendInnSøknad(søknad.build(), søkerAktørid, søkerIdent, DokumenttypeId.FORELDREPENGER_ENDRING_SØKNAD,
                saksnummerMor);
    }

    @Step("Send inn endringssøknad for mor med endret uttak")
    private void sendInnEndringssøknadforMorMedEndretUttak(TestscenarioDto testscenario, long saksnummerMor) {
        String søkerAktørid = testscenario.personopplysninger().søkerAktørIdent();
        String søkerIdent = testscenario.personopplysninger().søkerIdent();
        LocalDate fødselsdato = LocalDate.now().minusMonths(4);
        Fordeling fordeling = FordelingErketyper.fordelingMorHappyCaseLong(fødselsdato);
        EndringssøknadBuilder søknad = lagEndringssøknad(søkerAktørid, SøkersRolle.MOR, fordeling, saksnummerMor);
        fordel.sendInnSøknad(søknad.build(), søkerAktørid, søkerIdent, DokumenttypeId.FORELDREPENGER_ENDRING_SØKNAD,
                saksnummerMor);
    }

    @Step("Behandle søknad for far satt på vent")
    private long behandleSøknadForFarSattPåVent(TestscenarioDto testscenario, LocalDate fødselsdato) {
        long saksnummer = sendInnSøknadOgInntektFar(testscenario, fødselsdato, fødselsdato.plusWeeks(10).plusDays(1));

        saksbehandler.hentFagsak(saksnummer);
        verifiser(saksbehandler.valgtBehandling.erSattPåVent(),
                "Behandling ble ikke satt på vent selv om far har søkt for tidlig");

        return saksnummer;
    }

    @Step("Send inn søknad og inntekt mor")
    private long sendInnSøknadOgInntektMor(TestscenarioDto testscenario, LocalDate fødselsdato) {
        long saksnummer = sendInnSøknadMor(testscenario, fødselsdato);
        sendInnInntektsmeldingMor(testscenario, fødselsdato, saksnummer);
        return saksnummer;
    }

    @Step("Send inn søknad mor: fødsel funnet sted mor med far")
    private long sendInnSøknadMor(TestscenarioDto testscenario, LocalDate fødselsdato) {
        String søkerAktørid = testscenario.personopplysninger().søkerAktørIdent();
        String søkerIdent = testscenario.personopplysninger().søkerIdent();
        String annenPartAktørid = testscenario.personopplysninger().annenpartAktørIdent();
        ForeldrepengerBuilder søknad = lagSøknadForeldrepengerFødsel(fødselsdato, søkerAktørid, SøkersRolle.MOR)
                .medFordeling(FordelingErketyper.fordelingMorHappyCase(fødselsdato))
                .medAnnenForelder(annenPartAktørid);
        return fordel.sendInnSøknad(søknad.build(), søkerAktørid, søkerIdent,
                DokumenttypeId.SØKNAD_FORELDREPENGER_FØDSEL);
    }

    @Step("Send inn søknad mor med aksjonspunkt")
    private long sendInnSøknadMorMedAksjonspunkt(TestscenarioDto testscenario, LocalDate fødselsdato) {
        String søkerAktørid = testscenario.personopplysninger().søkerAktørIdent();
        String søkerIdent = testscenario.personopplysninger().søkerIdent();
        String annenPartAktørid = testscenario.personopplysninger().annenpartAktørIdent();
        LocalDate fpStartdato = fødselsdato.minusWeeks(3);

        Fordeling fordeling = generiskFordeling(
                uttaksperiode(FORELDREPENGER_FØR_FØDSEL, fpStartdato, fødselsdato.minusDays(1)),
                utsettelsesperiode(SøknadUtsettelseÅrsak.ARBEID, fødselsdato,fødselsdato.plusWeeks(6).minusDays(1)));
        ForeldrepengerBuilder søknad = lagSøknadForeldrepengerFødsel(fødselsdato, søkerAktørid, SøkersRolle.MOR)
                .medFordeling(fordeling)
                .medAnnenForelder(annenPartAktørid);
        return fordel.sendInnSøknad(søknad.build(), søkerAktørid, søkerIdent,
                DokumenttypeId.SØKNAD_FORELDREPENGER_FØDSEL);
    }

    @Step("Send inn inntektsmelding mor")
    private long sendInnInntektsmeldingMor(TestscenarioDto testscenario, LocalDate fødselsdato, Long saksnummer) {
        String søkerAktørid = testscenario.personopplysninger().søkerAktørIdent();
        String søkerIdent = testscenario.personopplysninger().søkerIdent();
        LocalDate startDatoForeldrepenger = fødselsdato.minusWeeks(3);

        List<Integer> inntekter = sorterteInntektsbeløp(testscenario);
        String orgnr = testscenario.scenariodataAnnenpartDto().arbeidsforholdModell().arbeidsforhold().get(0)
                .arbeidsgiverOrgnr();

        InntektsmeldingBuilder inntektsmelding = lagInntektsmelding(
                inntekter.get(0),
                søkerIdent,
                startDatoForeldrepenger,
                orgnr);
        return fordel.sendInnInntektsmelding(inntektsmelding, søkerAktørid, søkerIdent, saksnummer);
    }

    @Step("Send inn søknad og inntektsmelding far")
    private long sendInnSøknadOgInntektFar(TestscenarioDto testscenario, LocalDate fødselsdato,
            LocalDate startDatoForeldrepenger) {
        long saksnummer = sendInnSøknadFar(testscenario, fødselsdato, startDatoForeldrepenger);
        sendInnInntektsmeldingFar(testscenario, fødselsdato, startDatoForeldrepenger, saksnummer);
        return saksnummer;
    }

    @Step("Send inn søknad far")
    private long sendInnSøknadFar(TestscenarioDto testscenario, LocalDate fødselsdato,
            LocalDate startDatoForeldrepenger) {
        String søkerAktørid = testscenario.personopplysninger().annenpartAktørIdent();
        String søkerIdent = testscenario.personopplysninger().annenpartIdent();
        String annenPartAktørid = testscenario.personopplysninger().søkerAktørIdent();

        Fordeling fordeling = generiskFordeling(
                uttaksperiode(FEDREKVOTE, startDatoForeldrepenger, startDatoForeldrepenger.plusWeeks(2)));
        ForeldrepengerBuilder søknad = lagSøknadForeldrepengerFødsel(fødselsdato, søkerAktørid, SøkersRolle.FAR)
                .medFordeling(fordeling)
                .medAnnenForelder(annenPartAktørid);
        return fordel.sendInnSøknad(søknad.build(), søkerAktørid, søkerIdent,
                DokumenttypeId.SØKNAD_FORELDREPENGER_FØDSEL);
    }

    @Step("Send inn inntektsmelding far")
    private long sendInnInntektsmeldingFar(TestscenarioDto testscenario, LocalDate fødselsdato,
            LocalDate startDatoForeldrepenger, Long saksnummer) {
        String søkerAktørid = testscenario.personopplysninger().annenpartAktørIdent();
        String søkerIdent = testscenario.personopplysninger().annenpartIdent();

        List<Integer> inntekter = sorterteInntektsbeløp(testscenario);
        String orgnr = testscenario.scenariodataDto().arbeidsforholdModell().arbeidsforhold().get(0)
                .arbeidsgiverOrgnr();

        InntektsmeldingBuilder inntektsmelding = lagInntektsmelding(
                inntekter.get(0),
                søkerIdent,
                startDatoForeldrepenger,
                orgnr);
        return fordel.sendInnInntektsmelding(inntektsmelding, søkerAktørid, søkerIdent, saksnummer);
    }

}
