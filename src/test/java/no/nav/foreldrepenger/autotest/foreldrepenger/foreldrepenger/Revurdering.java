package no.nav.foreldrepenger.autotest.foreldrepenger.foreldrepenger;

import static no.nav.foreldrepenger.autotest.domain.foreldrepenger.Stønadskonto.FELLESPERIODE;
import static no.nav.foreldrepenger.autotest.domain.foreldrepenger.Stønadskonto.FORELDREPENGER_FØR_FØDSEL;
import static no.nav.foreldrepenger.autotest.domain.foreldrepenger.Stønadskonto.MØDREKVOTE;
import static no.nav.foreldrepenger.autotest.erketyper.FordelingErketyper.fordelingEndringssøknadGradering;
import static no.nav.foreldrepenger.autotest.erketyper.FordelingErketyper.generiskFordeling;
import static no.nav.foreldrepenger.autotest.erketyper.InntektsmeldingForeldrepengeErketyper.lagInntektsmelding;
import static no.nav.foreldrepenger.autotest.erketyper.SøknadEndringErketyper.lagEndringssøknad;
import static no.nav.foreldrepenger.autotest.erketyper.SøknadForeldrepengerErketyper.lagSøknadForeldrepengerFødsel;
import static no.nav.foreldrepenger.autotest.erketyper.UttaksperioderErketyper.graderingsperiodeArbeidstaker;
import static no.nav.foreldrepenger.autotest.erketyper.UttaksperioderErketyper.utsettelsesperiode;
import static no.nav.foreldrepenger.autotest.erketyper.UttaksperioderErketyper.uttaksperiode;
import static no.nav.foreldrepenger.autotest.util.AllureHelper.debugFritekst;
import static no.nav.foreldrepenger.autotest.util.AllureHelper.debugLoggBehandling;

import java.time.LocalDate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.qameta.allure.Description;
import no.nav.foreldrepenger.autotest.base.ForeldrepengerTestBase;
import no.nav.foreldrepenger.autotest.dokumentgenerator.foreldrepengesoknad.SøkersRolle;
import no.nav.foreldrepenger.autotest.dokumentgenerator.foreldrepengesoknad.builders.EndringssøknadBuilder;
import no.nav.foreldrepenger.autotest.dokumentgenerator.foreldrepengesoknad.builders.ForeldrepengerBuilder;
import no.nav.foreldrepenger.autotest.dokumentgenerator.inntektsmelding.builders.InntektsmeldingBuilder;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.Avslagsårsak;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.BehandlingResultatType;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.BehandlingStatus;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.BehandlingÅrsakType;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.FagsakStatus;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.KonsekvensForYtelsen;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.PeriodeResultatType;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.SøknadUtsettelseÅrsak;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.UttakUtsettelseÅrsak;
import no.nav.foreldrepenger.autotest.erketyper.FordelingErketyper;
import no.nav.foreldrepenger.autotest.erketyper.InntektsmeldingForeldrepengeErketyper;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.FastsettUttaksperioderManueltBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.FatterVedtakBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.ForeslåVedtakBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.ForeslåVedtakManueltBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.KontrollerManueltOpprettetRevurdering;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.VurderSoknadsfristForeldrepengerBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.overstyr.OverstyrMedlemskapsvilkaaret;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.beregning.BeregningsresultatMedUttaksplan;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.uttak.UttakResultatPeriode;
import no.nav.foreldrepenger.autotest.klienter.fpsak.historikk.dto.HistorikkinnslagType;
import no.nav.foreldrepenger.autotest.util.AllureHelper;
import no.nav.foreldrepenger.vtp.kontrakter.TestscenarioDto;
import no.nav.foreldrepenger.vtp.testmodell.dokument.modell.koder.DokumenttypeId;
import no.nav.vedtak.felles.xml.soeknad.uttak.v3.Fordeling;

@Execution(ExecutionMode.CONCURRENT)
@Tag("fpsak")
@Tag("foreldrepenger")
@Tag("fluoritt")
public class Revurdering extends ForeldrepengerTestBase {

    private static final Logger logger = LoggerFactory.getLogger(Revurdering.class);

    @Test
    @DisplayName("Revurdering opprettet manuelt av saksbehandler.")
    @Description("Førstegangsbehandling til positivt vedtak. Saksbehandler oppretter revurdering manuelt. " +
            "Overstyrer medlemskap. Vedtaket opphører.")
    public void opprettRevurderingManuelt() {
        TestscenarioDto testscenario = opprettTestscenario("50");
        String søkerAktørIdent = testscenario.personopplysninger().søkerAktørIdent();
        LocalDate fødselsdato = testscenario.personopplysninger().fødselsdato();
        LocalDate fpStartdato = fødselsdato.minusWeeks(3);

        ForeldrepengerBuilder søknad = lagSøknadForeldrepengerFødsel(fødselsdato, søkerAktørIdent, SøkersRolle.MOR);
        long saksnummer = fordel.sendInnSøknad(søknad.build(), testscenario,
                DokumenttypeId.SØKNAD_FORELDREPENGER_FØDSEL);

        InntektsmeldingBuilder inntektsmeldinger = lagInntektsmelding(
                testscenario.scenariodataDto().inntektskomponentModell().inntektsperioder().get(0).beløp(),
                testscenario.personopplysninger().søkerIdent(),
                fpStartdato,
                testscenario.scenariodataDto().arbeidsforholdModell().arbeidsforhold().get(0).arbeidsgiverOrgnr());
        fordel.sendInnInntektsmelding(
                inntektsmeldinger,
                testscenario.personopplysninger().søkerAktørIdent(),
                testscenario.personopplysninger().søkerIdent(),
                saksnummer);

        saksbehandler.hentFagsak(saksnummer);
        AllureHelper.debugLoggBehandlingsliste(saksbehandler.behandlinger);
        saksbehandler.ventPåOgVelgFørstegangsbehandling();
        saksbehandler.ventTilAvsluttetBehandling();
        verifiserLikhet(saksbehandler.valgtBehandling.hentBehandlingsresultat(), BehandlingResultatType.INNVILGET);
        saksbehandler.opprettBehandlingRevurdering(BehandlingÅrsakType.RE_OPPLYSNINGER_OM_MEDLEMSKAP);

        overstyrer.hentFagsak(saksnummer);
        overstyrer.ventPåOgVelgRevurderingBehandling();
        OverstyrMedlemskapsvilkaaret overstyrMedlemskapsvilkaaret = new OverstyrMedlemskapsvilkaaret();
        overstyrMedlemskapsvilkaaret
                .avvis(Avslagsårsak.SØKER_ER_IKKE_MEDLEM)
                .setBegrunnelse("avvist");
        overstyrer.overstyr(overstyrMedlemskapsvilkaaret);
        overstyrer.bekreftAksjonspunktMedDefaultVerdier(KontrollerManueltOpprettetRevurdering.class);
        overstyrer.bekreftAksjonspunktMedDefaultVerdier(ForeslåVedtakBekreftelse.class);
        verifiserLikhet(overstyrer.valgtBehandling.behandlingsresultat.getType(), BehandlingResultatType.OPPHØR, "Behandlingsresultat");
        verifiserLikhet(overstyrer.valgtBehandling.behandlingsresultat.getType(), BehandlingResultatType.OPPHØR, "Behandlingsresultat");

        beslutter.hentFagsak(saksnummer);
        beslutter.ventPåOgVelgRevurderingBehandling();
        var bekreftelse = beslutter.hentAksjonspunktbekreftelse(FatterVedtakBekreftelse.class);
        bekreftelse.godkjennAksjonspunkter(beslutter.hentAksjonspunktSomSkalTilTotrinnsBehandling());
        beslutter.fattVedtakOgVentTilAvsluttetBehandling(bekreftelse);
        beslutter.ventTilFagsakAvsluttet();

        verifiserLikhet(beslutter.valgtBehandling.behandlingsresultat.getType(), BehandlingResultatType.OPPHØR, "Behandlingsresultat");
        verifiserLikhet(beslutter.valgtBehandling.hentAvslagsarsak(),Avslagsårsak.SØKER_ER_IKKE_MEDLEM, "Avslagsårsak");
        verifiserLikhet(beslutter.valgtBehandling.status, BehandlingStatus.AVSLUTTET, "Behandlingsstatus");
        logger.info("Status på sak: {}", beslutter.valgtFagsak.status().getKode());
    }

    @Test
    @DisplayName("Endringssøknad med ekstra uttaksperiode.")
    @Description("Førstegangsbehandling til positivt vedtak. Søker sender inn endringsøknad. Endring i uttak. Vedtak fortsatt løpende.")
    public void endringssøknad() {
        TestscenarioDto testscenario = opprettTestscenario("50");

        // Førstegangssøknad
        String søkerAktørIdent = testscenario.personopplysninger().søkerAktørIdent();
        String søkerIdent = testscenario.personopplysninger().søkerIdent();
        LocalDate fødselsdato = testscenario.personopplysninger().fødselsdato();
        LocalDate fpStartdato = fødselsdato.minusWeeks(3);

        ForeldrepengerBuilder søknad = lagSøknadForeldrepengerFødsel(fødselsdato, søkerAktørIdent, SøkersRolle.MOR);
        Long saksnummer = fordel.sendInnSøknad(søknad.build(), testscenario,
                DokumenttypeId.SØKNAD_FORELDREPENGER_FØDSEL);
        InntektsmeldingBuilder inntektsmeldinger = lagInntektsmelding(
                testscenario.scenariodataDto().inntektskomponentModell().inntektsperioder().get(0).beløp(),
                testscenario.personopplysninger().søkerIdent(),
                fpStartdato,
                testscenario.scenariodataDto().arbeidsforholdModell().arbeidsforhold().get(0).arbeidsgiverOrgnr());
        fordel.sendInnInntektsmelding(
                inntektsmeldinger,
                testscenario.personopplysninger().søkerAktørIdent(),
                testscenario.personopplysninger().søkerIdent(),
                saksnummer);

        saksbehandler.hentFagsak(saksnummer);
        saksbehandler.ventPåOgVelgFørstegangsbehandling();
        saksbehandler.ventTilHistorikkinnslag(HistorikkinnslagType.VEDLEGG_MOTTATT);
        debugLoggBehandling(saksbehandler.valgtBehandling);
        saksbehandler.ventTilAvsluttetBehandling();
        verifiserLikhet(saksbehandler.behandlinger.size(), 1, "Antall behandlinger");
        verifiserLikhet(saksbehandler.valgtBehandling.type.getKode(), "BT-002", "Behandlingstype");
        saksbehandler.ventTilAvsluttetBehandling();
        debugFritekst("Ferdig med første behandling");

        // Endringssøknad
        Fordeling fordeling = generiskFordeling(
                uttaksperiode(FELLESPERIODE, fødselsdato.plusWeeks(8), fødselsdato.plusWeeks(10).minusDays(1)));
        EndringssøknadBuilder søknadE = lagEndringssøknad(søkerAktørIdent, SøkersRolle.MOR, fordeling, saksnummer);
        long saksnummerE = fordel.sendInnSøknad(søknadE.build(), søkerAktørIdent, søkerIdent,
                DokumenttypeId.FORELDREPENGER_ENDRING_SØKNAD, saksnummer);

        saksbehandler.hentFagsak(saksnummerE);
        saksbehandler.ventPåOgVelgRevurderingBehandling();
        saksbehandler.ventTilAvsluttetBehandling();
        verifiser(saksbehandler.valgtBehandling.behandlingsresultat.toString().equals("FORELDREPENGER_ENDRET"),
                "Behandlingsresultat er ikke 'Foreldrepenger er endret'");
        verifiserLikhet(saksbehandler.valgtBehandling.behandlingsresultat.getKonsekvenserForYtelsen().get(0),
                KonsekvensForYtelsen.ENDRING_I_UTTAK, "konsekvensForYtelsen");
        saksbehandler.hentFagsak(saksnummerE);
        verifiserLikhet(saksbehandler.valgtFagsak.status(), FagsakStatus.LØPENDE, "FagsakStatus");

    }

    @Test
    @DisplayName("Endringssøknad med utsettelse")
    @Description("Førstegangsbehandling til positivt vedtak. Endringssøknad med utsettelse fra bruker. Vedtak fortsatt løpende.")
    public void endringssøknadMedUtsettelse() {
        TestscenarioDto testscenario = opprettTestscenario("50");

        var søkerAktørIdent = testscenario.personopplysninger().søkerAktørIdent();
        var søkerIdent = testscenario.personopplysninger().søkerIdent();
        var fødselsdato = testscenario.personopplysninger().fødselsdato();
        var fpStartdato = fødselsdato.minusWeeks(3);

        // Opprette perioder mor søker om
        Fordeling opprinneligFordeling = FordelingErketyper.fordelingMorHappyCaseLong(fødselsdato);
        var søknad = lagSøknadForeldrepengerFødsel(fødselsdato, søkerAktørIdent, SøkersRolle.MOR)
                .medFordeling(opprinneligFordeling);
        var saksnummer = fordel.sendInnSøknad(søknad.build(), testscenario,
                DokumenttypeId.SØKNAD_FORELDREPENGER_FØDSEL);

        var inntektsmeldinger = lagInntektsmelding(
                testscenario.scenariodataDto().inntektskomponentModell().inntektsperioder().get(0).beløp(),
                testscenario.personopplysninger().søkerIdent(),
                fpStartdato,
                testscenario.scenariodataDto().arbeidsforholdModell().arbeidsforhold().get(0).arbeidsgiverOrgnr());
        fordel.sendInnInntektsmelding(
                inntektsmeldinger,
                testscenario.personopplysninger().søkerAktørIdent(),
                testscenario.personopplysninger().søkerIdent(),
                saksnummer);
        saksbehandler.hentFagsak(saksnummer);
        saksbehandler.ventPåOgVelgFørstegangsbehandling();
        saksbehandler.ventTilAvsluttetBehandling();
        verifiserLikhet(saksbehandler.valgtBehandling.hentBehandlingsresultat(), BehandlingResultatType.INNVILGET);
        verifiser(saksbehandler.valgtBehandling.getUttakResultatPerioder().getPerioderSøker().size() == 4,
                "Feil antall perioder.");

        LocalDate utsettelseFom = fødselsdato.plusWeeks(16);
        LocalDate utsettelseTom = fødselsdato.plusWeeks(18).minusDays(1);
        Fordeling fordelingUtsettelseEndring = FordelingErketyper.fordelingEndringssøknadUtsettelseOgForskyEksisterndePerioder(opprinneligFordeling,
                SøknadUtsettelseÅrsak.ARBEID, utsettelseFom, utsettelseTom);
        EndringssøknadBuilder endretSøknad = lagEndringssøknad(søkerAktørIdent, SøkersRolle.MOR,
                fordelingUtsettelseEndring, saksnummer);
        long saksnummerE = fordel.sendInnSøknad(endretSøknad.build(), søkerAktørIdent, søkerIdent,
                DokumenttypeId.FORELDREPENGER_ENDRING_SØKNAD, saksnummer);

        saksbehandler.hentFagsak(saksnummerE);
        saksbehandler.ventPåOgVelgRevurderingBehandling();
        saksbehandler.ventTilAvsluttetBehandling();
        verifiserLikhet(saksbehandler.valgtFagsak.status(), FagsakStatus.LØPENDE, "FagsakStatus");
        verifiserLikhet(saksbehandler.valgtBehandling.hentBehandlingsresultat(), BehandlingResultatType.FORELDREPENGER_ENDRET);
        verifiserLikhet(saksbehandler.valgtBehandling.behandlingsresultat.getKonsekvenserForYtelsen().get(0),
                KonsekvensForYtelsen.ENDRING_I_UTTAK);

        // Verifisering av uttak
        var UttaksPerioderForSøker = saksbehandler.valgtBehandling.getUttakResultatPerioder().getPerioderSøker();
        verifiserLikhet(UttaksPerioderForSøker.size(), 6,"Feil antall perioder.");
        for (UttakResultatPeriode periode : UttaksPerioderForSøker) {
            verifiserLikhet(periode.getAktiviteter().size(), 1, "Periode har mer enn én aktivitet");
        }
        verifiserLikhet(UttaksPerioderForSøker.get(4).getUtsettelseType(), UttakUtsettelseÅrsak.ARBEID,
                "Feil i utsettelsetype eller periode.");

        // Verifisering tilkjent ytelse
        BeregningsresultatMedUttaksplan tilkjentYtelsePerioder = saksbehandler.valgtBehandling
                .getBeregningResultatForeldrepenger();
        verifiserLikhet(tilkjentYtelsePerioder.getPerioder().size(), 6,"Feil antall perioder i tilkjentytesle.");
        verifiser(tilkjentYtelsePerioder.getPerioder().get(0).getDagsats() > 0,
                "Forventes en dagsats på større en null for periode #1 i tilkjent ytelse");
        verifiser(tilkjentYtelsePerioder.getPerioder().get(1).getDagsats() > 0,
                "Forventes en dagsats på større en null for periode #2 i tilkjent ytelse");
        verifiser(tilkjentYtelsePerioder.getPerioder().get(2).getDagsats() > 0,
                "Forventes en dagsats på større en null for periode #3 i tilkjent ytelse");
        verifiser(tilkjentYtelsePerioder.getPerioder().get(3).getDagsats() > 0,
                "Forventes en dagsats på større en null for periode #4 i tilkjent ytelse");
        verifiser(tilkjentYtelsePerioder.getPerioder().get(4).getDagsats() == 0,
                "Siden perioden er usettelse så forventes det 0 i dagsats.");
        verifiser(tilkjentYtelsePerioder.getPerioder().get(5).getDagsats() > 0,
                "Forventes en dagsats på større en null for periode #6 i tilkjent ytelse");
        verifiser(saksbehandler.verifiserUtbetaltDagsatsMedRefusjonGårTilKorrektPartForAllePerioder(0),
                "Forventer at hele summen utbetales til søker, og derfor ingenting til arbeidsgiver!");
    }

    @Test
    @DisplayName("Endringssøknad med gradering")
    @Description("Førstegangsbehandling til positivt vedtak. Endringssøknad med gradering fra bruker. Vedtak fortsatt løpende.")
    public void endringssøknadMedGradering() {
        TestscenarioDto testscenario = opprettTestscenario("50");

        String søkerAktørIdent = testscenario.personopplysninger().søkerAktørIdent();
        String søkerIdent = testscenario.personopplysninger().søkerIdent();
        LocalDate fødselsdato = testscenario.personopplysninger().fødselsdato();
        LocalDate fpStartdato = fødselsdato.minusWeeks(3);
        String orgnr = testscenario.scenariodataDto().arbeidsforholdModell().arbeidsforhold().get(0)
                .arbeidsgiverOrgnr();

        ForeldrepengerBuilder søknad = lagSøknadForeldrepengerFødsel(fødselsdato, søkerAktørIdent, SøkersRolle.MOR);
        Long saksnummer = fordel.sendInnSøknad(søknad.build(), testscenario,
                DokumenttypeId.SØKNAD_FORELDREPENGER_FØDSEL);

        InntektsmeldingBuilder inntektsmeldinger = lagInntektsmelding(
                testscenario.scenariodataDto().inntektskomponentModell().inntektsperioder().get(0).beløp(),
                testscenario.personopplysninger().søkerIdent(),
                fpStartdato,
                testscenario.scenariodataDto().arbeidsforholdModell().arbeidsforhold().get(0).arbeidsgiverOrgnr());
        fordel.sendInnInntektsmelding(
                inntektsmeldinger,
                testscenario.personopplysninger().søkerAktørIdent(),
                testscenario.personopplysninger().søkerIdent(),
                saksnummer);
        saksbehandler.hentFagsak(saksnummer);
        saksbehandler.ventPåOgVelgFørstegangsbehandling();
        saksbehandler.ventTilAvsluttetBehandling();
        verifiserLikhet(saksbehandler.valgtBehandling.getUttakResultatPerioder().getPerioderSøker().size(), 4,
                "Feil antall perioder i uttak.");

        // Endringssøknad
        LocalDate graderingFom = fødselsdato.plusWeeks(20);
        LocalDate graderingTom = fødselsdato.plusWeeks(23).minusDays(1);
        Fordeling fordelingGradering = fordelingEndringssøknadGradering(FELLESPERIODE, graderingFom, graderingTom,
                orgnr, 40);
        EndringssøknadBuilder endretSøknad = lagEndringssøknad(søkerAktørIdent, SøkersRolle.MOR,
                fordelingGradering, saksnummer);
        long saksnummerE = fordel.sendInnSøknad(endretSøknad.build(), søkerAktørIdent, søkerIdent,
                DokumenttypeId.FORELDREPENGER_ENDRING_SØKNAD, saksnummer);

        saksbehandler.hentFagsak(saksnummerE);
        saksbehandler.ventPåOgVelgRevurderingBehandling();
        saksbehandler.ventTilAvsluttetBehandling();
        verifiserLikhet(saksbehandler.valgtFagsak.status(), FagsakStatus.LØPENDE, "FagsakStatus");
        verifiserLikhet(saksbehandler.valgtBehandling.behandlingsresultat.getType(), BehandlingResultatType.FORELDREPENGER_ENDRET);
        verifiserLikhet(saksbehandler.valgtBehandling.behandlingsresultat.getKonsekvenserForYtelsen().get(0),
                KonsekvensForYtelsen.ENDRING_I_UTTAK);
        verifiserLikhet(saksbehandler.valgtBehandling.getUttakResultatPerioder().getPerioderSøker().size(), 5,
                "Feil antall perioder i uttak.");
        for (UttakResultatPeriode periode : saksbehandler.valgtBehandling.getUttakResultatPerioder()
                .getPerioderSøker()) {
            verifiserLikhet(periode.getAktiviteter().size(), 1, "Periode har mer enn én aktivitet");
        }
        verifiserLikhet(saksbehandler.valgtBehandling.getUttakResultatPerioder().getPerioderSøker().get(4)
                .getGraderingInnvilget(), true, "Feil graderingsperiode.");

    }

    @Test
    @DisplayName("Mor endringssøknad med aksjonspunkt i uttak")
    @Description("Mor endringssøknad med aksjonspunkt i uttak. Søker utsettelse tilbake i tid for å få aksjonspunkt." +
            "Saksbehandler avslår utsettelsen. Mor har også arbeid med arbeidsforholdId i inntektsmelding")
    public void endringssøknad_med_aksjonspunkt_i_uttak() {
        var testscenario = opprettTestscenario("140");
        var aktørIdSøker = testscenario.personopplysninger().søkerAktørIdent();
        var fnrSøker = testscenario.personopplysninger().søkerIdent();
        var inntekt = testscenario.scenariodataDto().inntektskomponentModell().inntektsperioder().get(0).beløp();
        var orgNrSøker = testscenario.scenariodataDto().arbeidsforholdModell().arbeidsforhold().get(0).arbeidsgiverOrgnr();
        var fødselsdato = testscenario.personopplysninger().fødselsdato();
        var fpStartdato = fødselsdato.minusWeeks(3);
        var fordeling = generiskFordeling(
                uttaksperiode(FORELDREPENGER_FØR_FØDSEL, fødselsdato.minusWeeks(3), fødselsdato.minusDays(1)),
                uttaksperiode(MØDREKVOTE, fødselsdato, fødselsdato.plusWeeks(6).minusDays(1)));
        var søknad = lagSøknadForeldrepengerFødsel(fødselsdato, aktørIdSøker, SøkersRolle.MOR)
                .medFordeling(fordeling);

        var saksnummer = fordel.sendInnSøknad(søknad.build(), testscenario,
                DokumenttypeId.SØKNAD_FORELDREPENGER_FØDSEL);
        var arbeidsforholdId = testscenario.scenariodataDto().arbeidsforholdModell().arbeidsforhold().get(0)
                .arbeidsforholdId();
        var im = lagInntektsmelding(inntekt, fnrSøker, fpStartdato, orgNrSøker)
                .medArbeidsforholdId(arbeidsforholdId);
        fordel.sendInnInntektsmelding(im, aktørIdSøker, fnrSøker, saksnummer);

        saksbehandler.hentFagsak(saksnummer);
        saksbehandler.ventTilAvsluttetBehandling();

        var fordelingEndring = generiskFordeling(
                utsettelsesperiode(SøknadUtsettelseÅrsak.ARBEID, fødselsdato.plusWeeks(6),
                        fødselsdato.plusWeeks(10).minusDays(1)));

        var søknadE = lagEndringssøknad(aktørIdSøker, SøkersRolle.MOR, fordelingEndring, saksnummer);
        var saksnummerE = fordel.sendInnSøknad(søknadE.build(), aktørIdSøker, fnrSøker,
                DokumenttypeId.FORELDREPENGER_ENDRING_SØKNAD, saksnummer);

        saksbehandler.hentFagsak(saksnummerE);
        saksbehandler.ventPåOgVelgRevurderingBehandling();

        var fastsettUttaksperioderManueltBekreftelse = saksbehandler
                .hentAksjonspunktbekreftelse(FastsettUttaksperioderManueltBekreftelse.class);
        fastsettUttaksperioderManueltBekreftelse.avslåManuellePerioder();
        saksbehandler.bekreftAksjonspunkt(fastsettUttaksperioderManueltBekreftelse);

        saksbehandler.bekreftAksjonspunktMedDefaultVerdier(ForeslåVedtakBekreftelse.class);

        // Behandle totrinnskontroll
        beslutter.hentFagsak(saksnummerE);
        beslutter.ventPåOgVelgRevurderingBehandling();
        var bekreftelse = beslutter.hentAksjonspunktbekreftelse(FatterVedtakBekreftelse.class);
        bekreftelse.godkjennAksjonspunkter(beslutter.hentAksjonspunktSomSkalTilTotrinnsBehandling());
        beslutter.fattVedtakOgVentTilAvsluttetBehandling(bekreftelse);

        saksbehandler.hentFagsak(saksnummerE);
        saksbehandler.ventPåOgVelgRevurderingBehandling();
        verifiserLikhet(saksbehandler.valgtFagsak.status(), FagsakStatus.LØPENDE, "FagsakStatus");
        verifiserLikhet(saksbehandler.valgtBehandling.behandlingsresultat.getType(), BehandlingResultatType.FORELDREPENGER_ENDRET);
        verifiserLikhet(saksbehandler.valgtBehandling.behandlingsresultat.getKonsekvenserForYtelsen().get(0),
                KonsekvensForYtelsen.ENDRING_I_UTTAK);

        var UttaksPerioderForSøker = saksbehandler.valgtBehandling.getUttakResultatPerioder().getPerioderSøker();
        verifiserLikhet(UttaksPerioderForSøker.size(), 4,"Feil antall perioder i uttak.");
        verifiserLikhet(UttaksPerioderForSøker.get(2).getUtsettelseType(), UttakUtsettelseÅrsak.ARBEID,
                "Feil utsettelsesårsak for uttaksperidoe #2.");
        verifiserLikhet(UttaksPerioderForSøker.get(2).getPeriodeResultatType(), PeriodeResultatType.AVSLÅTT,
                "Forventer at uttaksperioden tilbake i tid blir avslått..");
        verifiserLikhet(UttaksPerioderForSøker.get(3).getUtsettelseType(), UttakUtsettelseÅrsak.ARBEID,
                "Feil utsettelsesårsak for uttaksperidoe #3.");
        verifiserLikhet(UttaksPerioderForSøker.get(3).getPeriodeResultatType(),PeriodeResultatType.INNVILGET,
                "Forventer at uttaksperioden tilbake i tid blir avslått..");

    }

    @Test
    @DisplayName("Ikke få avslåg på innvilget perioder pga søknadsfrist")
    @Description("Ikke få avslåg på innvilget perioder pga søknadsfrist.")
    public void ikke_avslag_pa_innvilget_perioder_pga_søknadsfrist_i_revurdering() {
        var testscenario = opprettTestscenario("74");
        var aktørIdSøker = testscenario.personopplysninger().søkerAktørIdent();
        var fødselsdato = testscenario.personopplysninger().fødselsdato();
        var fpStartdato = fødselsdato.minusWeeks(3);
        var fordeling = generiskFordeling(
                uttaksperiode(FORELDREPENGER_FØR_FØDSEL, fpStartdato, fødselsdato.minusDays(1)),
                uttaksperiode(MØDREKVOTE, fødselsdato, fødselsdato.plusWeeks(13).minusDays(1)));
        var søknad = lagSøknadForeldrepengerFødsel(fødselsdato, aktørIdSøker, SøkersRolle.MOR)
                .medFordeling(fordeling)
                .medMottattDato(fødselsdato.plusWeeks(9));
        var saksnummer = fordel.sendInnSøknad(søknad.build(), testscenario,
                DokumenttypeId.SØKNAD_FORELDREPENGER_FØDSEL);

        var im = InntektsmeldingForeldrepengeErketyper
                .makeInntektsmeldingFromTestscenario(testscenario, fpStartdato).get(0);
        fordel.sendInnInntektsmelding(im, testscenario, saksnummer);

        saksbehandler.hentFagsak(saksnummer);
        saksbehandler.ventTilAvsluttetBehandling();

        // Sender endringssøknad for å gi fagsaken en ny søknad mottatt dato
        var fordelingEndringssøknad = generiskFordeling(
                uttaksperiode(FELLESPERIODE, fødselsdato.plusWeeks(13), fødselsdato.plusWeeks(14).minusDays(1)));
        var søknadE = lagEndringssøknad(aktørIdSøker, SøkersRolle.MOR, fordelingEndringssøknad, saksnummer)
                .medMottattDato(fødselsdato.plusWeeks(10));
        fordel.sendInnSøknad(søknadE.build(), testscenario, DokumenttypeId.FORELDREPENGER_ENDRING_SØKNAD, saksnummer);

        saksbehandler.velgSisteBehandling();
        saksbehandler.ventTilAvsluttetBehandling();

        // Manuell behandling for å få endringssdato satt til første uttaksdag
        saksbehandler.opprettBehandlingRevurdering(BehandlingÅrsakType.RE_OPPLYSNINGER_OM_FORDELING);
        saksbehandler.velgSisteBehandling();
        saksbehandler.bekreftAksjonspunktbekreftelserer(
                saksbehandler.hentAksjonspunktbekreftelse(KontrollerManueltOpprettetRevurdering.class),
                saksbehandler.hentAksjonspunktbekreftelse(ForeslåVedtakManueltBekreftelse.class));
        saksbehandler.ventTilAvsluttetBehandling();

        var allePerioderInnvilget = saksbehandler.valgtBehandling.hentUttaksperioder().stream()
                .allMatch(p -> p.getPeriodeResultatType().equals(PeriodeResultatType.INNVILGET));
        verifiser(allePerioderInnvilget, "Forventer at alle uttaksperioder er innvilget");
    }

    @Test
    @DisplayName("Fortsatt få avslag på avslåtte perioder pga søknadsfrist i neste revurdering")
    @Description("Fortsatt få avslag på avslåtte perioder pga søknadsfrist i neste revurdering. Bruker papirsøknad for å kunne sette mottatt dato tilbake i tid")
    public void fortsatt_tape_avslåtte_perioder_pga_søknadsfrist_i_revurdering() {
        var testscenario = opprettTestscenario("74");
        var aktørIdSøker = testscenario.personopplysninger().søkerAktørIdent();
        var fødselsdato = testscenario.personopplysninger().fødselsdato();
        var fpStartdato = fødselsdato.minusWeeks(3);
        var fordeling = generiskFordeling(
                uttaksperiode(FORELDREPENGER_FØR_FØDSEL, fpStartdato, fødselsdato.minusDays(1)),
                uttaksperiode(MØDREKVOTE, fødselsdato, fødselsdato.plusWeeks(13).minusDays(1)));
        var søknad = lagSøknadForeldrepengerFødsel(fødselsdato, aktørIdSøker, SøkersRolle.MOR)
                .medFordeling(fordeling)
                //Ikke alle periodene skal avlås pga søknadsfrist
                .medMottattDato(fødselsdato.plusWeeks(18));
        var saksnummer = fordel.sendInnSøknad(søknad.build(), testscenario,
                DokumenttypeId.SØKNAD_FORELDREPENGER_FØDSEL);

        var im = InntektsmeldingForeldrepengeErketyper
                .makeInntektsmeldingFromTestscenario(testscenario, fpStartdato).get(0);
        fordel.sendInnInntektsmelding(im, testscenario, saksnummer);

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
        verifiser(saksbehandler.hentAvslåtteUttaksperioder().size() > 1, "Forventer avslåtte uttaksperioder");

        var fordelingEndringssøknad = generiskFordeling(
                uttaksperiode(FELLESPERIODE, fødselsdato.plusWeeks(13), fødselsdato.plusWeeks(12).plusWeeks(2)));
        var søknadE = lagEndringssøknad(testscenario.personopplysninger().søkerAktørIdent(), SøkersRolle.MOR,
                fordelingEndringssøknad, saksnummer);
        fordel.sendInnSøknad(søknadE.build(), testscenario, DokumenttypeId.FORELDREPENGER_ENDRING_SØKNAD, saksnummer);

        saksbehandler.velgSisteBehandling();
        saksbehandler.ventTilAvsluttetBehandling();

        // Manuell behandling for å få endringssdato satt til første uttaksdag
        saksbehandler.opprettBehandlingRevurdering(BehandlingÅrsakType.RE_OPPLYSNINGER_OM_FORDELING);
        saksbehandler.velgSisteBehandling();

        verifiser(saksbehandler.hentAvslåtteUttaksperioder().size() > 1, "Forventer avslåtte uttaksperioder");
    }

    @Test
    @DisplayName("Utsettelser og gradering fra førstegangsbehandling skal ikke gå til manuell behandling")
    @Description("Utsettelser og gradering fra førstegangsbehandling skal ikke gå til manuell behandling hvis innenfor søknadsfrist."
            + " Bruker papirsøknad for å kunne sette mottatt dato tilbake i tid")
    public void utsettelser_og_gradering_fra_førstegangsbehandling_skal_ikke_gå_til_manuell_behandling_ved_endringssøknad() {
        var testscenario = opprettTestscenario("56");
        var aktørIdSøker = testscenario.personopplysninger().søkerAktørIdent();
        var fødselsdato = testscenario.personopplysninger().fødselsdato();
        var orgnummer = testscenario.scenariodataDto().arbeidsforholdModell().arbeidsforhold().get(0).arbeidsgiverOrgnr();
        var fordeling = generiskFordeling(
                uttaksperiode(MØDREKVOTE, fødselsdato, fødselsdato.plusWeeks(6).minusDays(1)),
                graderingsperiodeArbeidstaker(MØDREKVOTE, fødselsdato.plusWeeks(6), fødselsdato.plusWeeks(12).minusDays(1),
                        orgnummer, 50),
                utsettelsesperiode(SøknadUtsettelseÅrsak.ARBEID, fødselsdato.plusWeeks(12), fødselsdato.plusWeeks(15)));

        var søknad = lagSøknadForeldrepengerFødsel(fødselsdato, aktørIdSøker, SøkersRolle.MOR)
                .medFordeling(fordeling)
                .medMottattDato(fødselsdato);
        var saksnummer = fordel.sendInnSøknad(søknad.build(), testscenario,
                DokumenttypeId.SØKNAD_FORELDREPENGER_FØDSEL);

        var im = InntektsmeldingForeldrepengeErketyper
                .makeInntektsmeldingFromTestscenario(testscenario, fødselsdato);
        fordel.sendInnInntektsmeldinger(im, testscenario, saksnummer);

        saksbehandler.hentFagsak(saksnummer);
        saksbehandler.ventTilAvsluttetBehandling();

        verifiser(saksbehandler.hentAvslåtteUttaksperioder().isEmpty(),
                "Forventer at alle uttaksperioder er innvilget");

        var fordelingEndringssøknad = generiskFordeling(
                uttaksperiode(FELLESPERIODE, fødselsdato.plusWeeks(15).plusDays(1), fødselsdato.plusWeeks(16)));
        var søknadE = lagEndringssøknad(testscenario.personopplysninger().søkerAktørIdent(), SøkersRolle.MOR,
                fordelingEndringssøknad, saksnummer);
        fordel.sendInnSøknad(søknadE.build(), testscenario, DokumenttypeId.FORELDREPENGER_ENDRING_SØKNAD, saksnummer);

        saksbehandler.velgSisteBehandling();
        saksbehandler.ventTilAvsluttetBehandling();
        verifiser(saksbehandler.hentAvslåtteUttaksperioder().isEmpty(),
                "Forventer at alle uttaksperioder er innvilget");

        // Manuell behandling for å få endringssdato satt til første uttaksdag
        saksbehandler.opprettBehandlingRevurdering(BehandlingÅrsakType.RE_OPPLYSNINGER_OM_FORDELING);
        saksbehandler.velgSisteBehandling();
        verifiser(saksbehandler.hentAvslåtteUttaksperioder().isEmpty(),
                "Forventer at alle uttaksperioder er innvilget");
    }
}



