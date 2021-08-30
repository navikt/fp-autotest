package no.nav.foreldrepenger.autotest.fpsak.foreldrepenger;

import static no.nav.foreldrepenger.autotest.domain.foreldrepenger.Stønadskonto.FELLESPERIODE;
import static no.nav.foreldrepenger.autotest.domain.foreldrepenger.Stønadskonto.FORELDREPENGER_FØR_FØDSEL;
import static no.nav.foreldrepenger.autotest.domain.foreldrepenger.Stønadskonto.MØDREKVOTE;
import static no.nav.foreldrepenger.autotest.erketyper.FordelingErketyper.generiskFordeling;
import static no.nav.foreldrepenger.autotest.erketyper.InntektsmeldingForeldrepengeErketyper.lagInntektsmelding;
import static no.nav.foreldrepenger.autotest.erketyper.SøknadEndringErketyper.lagEndringssøknad;
import static no.nav.foreldrepenger.autotest.erketyper.SøknadForeldrepengerErketyper.lagSøknadForeldrepengerFødsel;
import static no.nav.foreldrepenger.autotest.erketyper.UttaksperioderErketyper.graderingsperiodeArbeidstaker;
import static no.nav.foreldrepenger.autotest.erketyper.UttaksperioderErketyper.utsettelsesperiode;
import static no.nav.foreldrepenger.autotest.erketyper.UttaksperioderErketyper.uttaksperiode;
import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.qameta.allure.Description;
import no.nav.foreldrepenger.autotest.base.ForeldrepengerTestBase;
import no.nav.foreldrepenger.autotest.dokumentgenerator.foreldrepengesoknad.SøkersRolle;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.BehandlingResultatType;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.BehandlingÅrsakType;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.KonsekvensForYtelsen;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.PeriodeResultatType;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.SøknadUtsettelseÅrsak;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.UttakUtsettelseÅrsak;
import no.nav.foreldrepenger.autotest.erketyper.FordelingErketyper;
import no.nav.foreldrepenger.autotest.erketyper.InntektsmeldingForeldrepengeErketyper;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.FastsettUttaksperioderManueltBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.FatterVedtakBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.ForeslåVedtakBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.avklarfakta.AvklarFaktaUttakBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.uttak.UttakResultatPeriode;
import no.nav.foreldrepenger.vtp.testmodell.dokument.modell.koder.DokumenttypeId;
import no.nav.vedtak.felles.xml.soeknad.uttak.v3.Fordeling;

@Tag("fpsak")
@Tag("foreldrepenger")
class SammenhengendeUttak extends ForeldrepengerTestBase {

    private static final Logger logger = LoggerFactory.getLogger(SammenhengendeUttak.class);

    @Test
    @DisplayName("Utsettelse av forskjellige årsaker")
    @Description("Mor søker fødsel med mange utsettelseperioder. Hensikten er å sjekke at alle årsaker fungerer." +
            "Kun arbeid (og ferie) skal oppgis i IM. Verifiserer på 0 trekkdager for perioder med utsettelse. " +
            "Kun perioder som krever dokumentasjon skal bli manuelt behandlet i fakta om uttak. Ingen AP i uttak.")
    void utsettelse_med_avvik() {
        var testscenario = opprettTestscenario("600");

        var søkerAktørId = testscenario.personopplysninger().søkerAktørIdent();
        var fødsel = testscenario.personopplysninger().fødselsdato();
        var fpStartdato = fødsel.minusWeeks(3);

        var fordeling = new Fordeling();
        fordeling.setAnnenForelderErInformert(true);
        var perioder = fordeling.getPerioder();
        perioder.add(uttaksperiode(FORELDREPENGER_FØR_FØDSEL, fpStartdato, fødsel.minusDays(1)));
        perioder.add(uttaksperiode(MØDREKVOTE, fødsel, fødsel.plusWeeks(6).minusDays(1)));
        perioder.add(utsettelsesperiode(SøknadUtsettelseÅrsak.INSTITUSJON_BARN, fødsel.plusWeeks(6),
                fødsel.plusWeeks(9).minusDays(1)));
        perioder.add(utsettelsesperiode(SøknadUtsettelseÅrsak.INSTITUSJON_SØKER, fødsel.plusWeeks(9),
                fødsel.plusWeeks(12).minusDays(1)));
        perioder.add(utsettelsesperiode(SøknadUtsettelseÅrsak.SYKDOM, fødsel.plusWeeks(12),
                fødsel.plusWeeks(15).minusDays(1)));
        perioder.add(utsettelsesperiode(SøknadUtsettelseÅrsak.ARBEID, fødsel.plusWeeks(15),
                fødsel.plusWeeks(16).minusDays(1)));
        perioder.add(utsettelsesperiode(SøknadUtsettelseÅrsak.HV_OVELSE, fødsel.plusWeeks(16),
                fødsel.plusWeeks(17).minusDays(1)));
        perioder.add(utsettelsesperiode(SøknadUtsettelseÅrsak.NAV_TILTAK, fødsel.plusWeeks(17),
                fødsel.plusWeeks(18).minusDays(1)));
        perioder.add(uttaksperiode(FELLESPERIODE, fødsel.plusWeeks(18),
                fødsel.plusWeeks(21).minusDays(1)));

        // sender inn søknad
        var søknad = lagSøknadForeldrepengerFødsel(fødsel, søkerAktørId, SøkersRolle.MOR)
                .medFordeling(fordeling)
                .medMottattDato(fpStartdato.minusWeeks(3));
        var saksnummer = fordel.sendInnSøknad(søknad.build(), testscenario,
                DokumenttypeId.SØKNAD_FORELDREPENGER_FØDSEL);
        var inntektsmeldinger = lagInntektsmelding(
                testscenario.scenariodataDto().inntektskomponentModell().inntektsperioder().get(0).beløp(),
                testscenario.personopplysninger().søkerIdent(),
                fpStartdato,
                testscenario.scenariodataDto().arbeidsforholdModell().arbeidsforhold().get(0)
                        .arbeidsgiverOrgnr());
        inntektsmeldinger.medUtsettelse(SøknadUtsettelseÅrsak.ARBEID.getKode(), fødsel.plusWeeks(15),
                fødsel.plusWeeks(18).minusDays(1));
        fordel.sendInnInntektsmelding(
                inntektsmeldinger,
                testscenario.personopplysninger().søkerAktørIdent(),
                testscenario.personopplysninger().søkerIdent(),
                saksnummer);

        saksbehandler.hentFagsak(saksnummer);
        var avklarFaktaUttakPerioder = saksbehandler
                .hentAksjonspunktbekreftelse(AvklarFaktaUttakBekreftelse.AvklarFaktaUttakPerioder.class)
                .godkjennPeriode(fødsel.plusWeeks(6), fødsel.plusWeeks(9).minusDays(1))
                .godkjennPeriode(fødsel.plusWeeks(9), fødsel.plusWeeks(12).minusDays(1))
                .godkjennPeriode(fødsel.plusWeeks(12), fødsel.plusWeeks(15).minusDays(1))
                .godkjennPeriode(fødsel.plusWeeks(16), fødsel.plusWeeks(17).minusDays(1))
                .godkjennPeriode(fødsel.plusWeeks(17), fødsel.plusWeeks(18).minusDays(1));
        saksbehandler.bekreftAksjonspunkt(avklarFaktaUttakPerioder);
        saksbehandler.bekreftAksjonspunktMedDefaultVerdier(ForeslåVedtakBekreftelse.class);

        beslutter.hentFagsak(saksnummer);
        var bekreftelse = beslutter.hentAksjonspunktbekreftelse(FatterVedtakBekreftelse.class)
                .godkjennAksjonspunkter(beslutter.hentAksjonspunktSomSkalTilTotrinnsBehandling());
        beslutter.fattVedtakOgVentTilAvsluttetBehandling(bekreftelse);

        assertThat(beslutter.valgtBehandling.hentUttaksperioder())
                .as("Uttaksperioder")
                .hasSize(9);
        assertThat(beslutter.valgtBehandling.hentUttaksperiode(2).getAktiviteter().get(0).getTrekkdagerDesimaler())
                .as("Antall trekkdager for aktivitet i uttaksperiode")
                .isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(beslutter.valgtBehandling.hentUttaksperiode(3).getAktiviteter().get(0).getTrekkdagerDesimaler())
                .as("Antall trekkdager for aktivitet i uttaksperiode")
                .isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(beslutter.valgtBehandling.hentUttaksperiode(4).getAktiviteter().get(0).getTrekkdagerDesimaler())
                .as("Antall trekkdager for aktivitet i uttaksperiode")
                .isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(beslutter.valgtBehandling.hentUttaksperiode(5).getAktiviteter().get(0).getTrekkdagerDesimaler())
                .as("Antall trekkdager for aktivitet i uttaksperiode")
                .isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(beslutter.valgtBehandling.hentUttaksperiode(6).getAktiviteter().get(0).getTrekkdagerDesimaler())
                .as("Antall trekkdager for aktivitet i uttaksperiode")
                .isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(beslutter.valgtBehandling.hentUttaksperiode(7).getAktiviteter().get(0).getTrekkdagerDesimaler())
                .as("Antall trekkdager for aktivitet i uttaksperiode")
                .isEqualByComparingTo(BigDecimal.ZERO);
    }



    @Test
    @DisplayName("Endringssøknad med utsettelse")
    @Description("Førstegangsbehandling til positivt vedtak. Endringssøknad med utsettelse fra bruker. Vedtak fortsatt løpende.")
    void endringssøknadMedUtsettelse() {
        var testscenario = opprettTestscenario("600");

        var søkerAktørIdent = testscenario.personopplysninger().søkerAktørIdent();
        var søkerIdent = testscenario.personopplysninger().søkerIdent();
        var fødselsdato = testscenario.personopplysninger().fødselsdato();
        var fpStartdato = fødselsdato.minusWeeks(3);

        // Opprette perioder mor søker om
        var opprinneligFordeling = FordelingErketyper.fordelingMorHappyCaseLong(fødselsdato);
        var søknad = lagSøknadForeldrepengerFødsel(fødselsdato, søkerAktørIdent, SøkersRolle.MOR)
                .medFordeling(opprinneligFordeling)
                .medMottattDato(fpStartdato.minusWeeks(3));
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
        assertThat(saksbehandler.valgtBehandling.hentBehandlingsresultat())
                .as("Behandlingsresultat")
                .isEqualTo(BehandlingResultatType.INNVILGET);
        assertThat(saksbehandler.valgtBehandling.getUttakResultatPerioder().getPerioderSøker())
                .as("Antall uttaksperioder")
                .hasSize(4);

        var utsettelseFom = fødselsdato.plusWeeks(16);
        var fordelingUtsettelseEndring = generiskFordeling(
                utsettelsesperiode(SøknadUtsettelseÅrsak.ARBEID, utsettelseFom, utsettelseFom.plusWeeks(2).minusDays(1)),
                uttaksperiode(FELLESPERIODE, utsettelseFom.plusWeeks(2), utsettelseFom.plusWeeks(16).minusDays(1)));

        var endretSøknad = lagEndringssøknad(søkerAktørIdent, SøkersRolle.MOR,
                fordelingUtsettelseEndring, saksnummer)
                .medMottattDato(utsettelseFom.minusWeeks(3));
        var saksnummerE = fordel.sendInnSøknad(endretSøknad.build(), søkerAktørIdent, søkerIdent,
                DokumenttypeId.FORELDREPENGER_ENDRING_SØKNAD, saksnummer);

        saksbehandler.hentFagsak(saksnummerE);
        saksbehandler.ventPåOgVelgRevurderingBehandling();
        saksbehandler.ventTilAvsluttetBehandling();
        saksbehandler.ventTilFagsakAvsluttet();
        assertThat(saksbehandler.valgtBehandling.hentBehandlingsresultat())
                .as("Behandlingsresultat")
                .isEqualTo(BehandlingResultatType.FORELDREPENGER_ENDRET);
        assertThat(saksbehandler.valgtBehandling.behandlingsresultat.getKonsekvenserForYtelsen())
                .as("Konsekvenser for ytelse")
                .contains(KonsekvensForYtelsen.ENDRING_I_UTTAK);

        // Verifisering av uttak
        var UttaksPerioderForSøker = saksbehandler.valgtBehandling.getUttakResultatPerioder().getPerioderSøker();
        assertThat(UttaksPerioderForSøker)
                .as("Antall uttaksperioder for søker")
                .hasSize(6);
        for (UttakResultatPeriode periode : UttaksPerioderForSøker) {
            assertThat(periode.getAktiviteter())
                    .as("Antall aktiviteter for hver uttaksperiode")
                    .hasSize(1);
        }
        assertThat(UttaksPerioderForSøker.get(4).getUtsettelseType())
                .as("Uttaksutsettelesårsak")
                .isEqualTo(UttakUtsettelseÅrsak.ARBEID);

        // Verifisering tilkjent ytelse
        var tilkjentYtelsePerioder = saksbehandler.valgtBehandling.getBeregningResultatForeldrepenger();
        assertThat(tilkjentYtelsePerioder.getPerioder())
                .as("Antall perioder i tilkjent ytelse")
                .hasSize(6);
        assertThat(tilkjentYtelsePerioder.getPerioder().get(0).getDagsats())
                .as("Dagsats i tilkjent ytelse periode")
                .isPositive();
        assertThat(tilkjentYtelsePerioder.getPerioder().get(1).getDagsats())
                .as("Dagsats i tilkjent ytelse periode")
                .isPositive();
        assertThat(tilkjentYtelsePerioder.getPerioder().get(2).getDagsats())
                .as("Dagsats i tilkjent ytelse periode")
                .isPositive();
        assertThat(tilkjentYtelsePerioder.getPerioder().get(3).getDagsats())
                .as("Dagsats i tilkjent ytelse periode")
                .isPositive();
        assertThat(tilkjentYtelsePerioder.getPerioder().get(4).getDagsats())
                .as("Dagsats i tilkjent ytelse periode")
                .isZero();
        assertThat(tilkjentYtelsePerioder.getPerioder().get(5).getDagsats())
                .as("Dagsats i tilkjent ytelse periode")
                .isPositive();
        assertThat(saksbehandler.verifiserUtbetaltDagsatsMedRefusjonGårTilKorrektPartForAllePerioder(0))
                .as("Forventer at hele summen utbetales til søker, og derfor ingenting til arbeidsgiver!")
                .isTrue();
    }

    @Test
    @DisplayName("Mor endringssøknad med aksjonspunkt i uttak")
    @Description("Mor endringssøknad med aksjonspunkt i uttak. Søker utsettelse tilbake i tid for å få aksjonspunkt." +
            "Saksbehandler avslår utsettelsen. Mor har også arbeid med arbeidsforholdId i inntektsmelding")
    void endringssøknad_med_aksjonspunkt_i_uttak() {
        var testscenario = opprettTestscenario("600");
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
                .medFordeling(fordeling)
                .medMottattDato(fpStartdato.minusWeeks(3));

        var saksnummer = fordel.sendInnSøknad(søknad.build(), testscenario,
                DokumenttypeId.SØKNAD_FORELDREPENGER_FØDSEL);
        var arbeidsforholdId = testscenario.scenariodataDto().arbeidsforholdModell().arbeidsforhold().get(0)
                .arbeidsforholdId();
        var im = lagInntektsmelding(inntekt, fnrSøker, fpStartdato, orgNrSøker)
                .medArbeidsforholdId(arbeidsforholdId);
        fordel.sendInnInntektsmelding(im, aktørIdSøker, fnrSøker, saksnummer);

        saksbehandler.hentFagsak(saksnummer);
        saksbehandler.ventTilAvsluttetBehandling();

        var startUtsettelse = fødselsdato.plusWeeks(6);
        var fordelingEndring = generiskFordeling(
                utsettelsesperiode(SøknadUtsettelseÅrsak.ARBEID, startUtsettelse,
                        startUtsettelse.plusWeeks(4).minusDays(1)));

        // TODO: Bekreft at det er endringssøknad med mottatt dato frem i tid som er tanken her. 4 uker etter endring ok?
        var søknadE = lagEndringssøknad(aktørIdSøker, SøkersRolle.MOR, fordelingEndring, saksnummer)
                .medMottattDato(startUtsettelse.plusWeeks(2));
        var saksnummerE = fordel.sendInnSøknad(søknadE.build(), aktørIdSøker, fnrSøker,
                DokumenttypeId.FORELDREPENGER_ENDRING_SØKNAD, saksnummer);

        saksbehandler.hentFagsak(saksnummerE);
        saksbehandler.ventPåOgVelgRevurderingBehandling();

        var fastsettUttaksperioderManueltBekreftelse = saksbehandler
                .hentAksjonspunktbekreftelse(FastsettUttaksperioderManueltBekreftelse.class)
                .avslåManuellePerioder();
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
        saksbehandler.ventTilAvsluttetBehandling();
        saksbehandler.ventTilFagsakAvsluttet();
        assertThat(beslutter.valgtBehandling.hentBehandlingsresultat())
                .as("Behandlingsresultat")
                .isEqualTo(BehandlingResultatType.FORELDREPENGER_ENDRET);
        assertThat(saksbehandler.valgtBehandling.behandlingsresultat.getKonsekvenserForYtelsen())
                .as("Konsekvenser for ytelse")
                .contains(KonsekvensForYtelsen.ENDRING_I_UTTAK);

        var UttaksPerioderForSøker = saksbehandler.valgtBehandling.getUttakResultatPerioder().getPerioderSøker();
        assertThat(UttaksPerioderForSøker)
                .as("Antall uttaksperioder")
                .hasSize(4);
        assertThat(UttaksPerioderForSøker.get(2).getUtsettelseType())
                .as("Uttaks utsettelsesårsak for periode 3")
                .isEqualTo(UttakUtsettelseÅrsak.ARBEID);
        assertThat(UttaksPerioderForSøker.get(2).getPeriodeResultatType())
                .as("Perioderesultatstype for periode 3")
                .isEqualTo(PeriodeResultatType.AVSLÅTT);
        assertThat(UttaksPerioderForSøker.get(3).getUtsettelseType())
                .as("Uttaks utsettelsesårsak for periode 3")
                .isEqualTo(UttakUtsettelseÅrsak.ARBEID);
        assertThat(UttaksPerioderForSøker.get(3).getPeriodeResultatType())
                .as("Perioderesultatstype for periode 3")
                .isEqualTo(PeriodeResultatType.INNVILGET);
    }

    @Test
    @DisplayName("Utsettelser og gradering fra førstegangsbehandling skal ikke gå til manuell behandling")
    @Description("Utsettelser og gradering fra førstegangsbehandling skal ikke gå til manuell behandling hvis innenfor søknadsfrist." +
            "Førstegangsbehandling avslutter med utsettelse. Søker sender inn endringssøknad hvor en tar ut 1 uke etter utsettelsen.")
    void utsettelser_og_gradering_fra_førstegangsbehandling_skal_ikke_gå_til_manuell_behandling_ved_endringssøknad() {
        var testscenario = opprettTestscenario("600");
        var aktørIdSøker = testscenario.personopplysninger().søkerAktørIdent();
        var fødselsdato = testscenario.personopplysninger().fødselsdato();
        var orgnummer = testscenario.scenariodataDto().arbeidsforholdModell().arbeidsforhold().get(0).arbeidsgiverOrgnr();
        var fordeling = generiskFordeling(
                uttaksperiode(FORELDREPENGER_FØR_FØDSEL, fødselsdato.minusWeeks(3), fødselsdato.minusDays(1)),
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

        assertThat(saksbehandler.hentAvslåtteUttaksperioder())
                .as("Avslåtte uttaksperioder")
                .isEmpty();

        var utsettelseStart = fødselsdato.plusWeeks(15);
        var fordelingEndringssøknad = generiskFordeling(
                uttaksperiode(FELLESPERIODE, utsettelseStart, utsettelseStart.plusWeeks(1).minusDays(1)));
        var søknadE = lagEndringssøknad(testscenario.personopplysninger().søkerAktørIdent(), SøkersRolle.MOR,
                fordelingEndringssøknad, saksnummer)
                .medMottattDato(utsettelseStart.minusWeeks(3));
        fordel.sendInnSøknad(søknadE.build(), testscenario, DokumenttypeId.FORELDREPENGER_ENDRING_SØKNAD, saksnummer);

        saksbehandler.velgSisteBehandling();
        saksbehandler.ventTilAvsluttetBehandling();
        assertThat(saksbehandler.hentAvslåtteUttaksperioder())
                .as("Avslåtte uttaksperioder")
                .isEmpty();

        // Manuell behandling for å få endringssdato satt til første uttaksdag
        saksbehandler.opprettBehandlingRevurdering(BehandlingÅrsakType.RE_OPPLYSNINGER_OM_FORDELING);
        saksbehandler.velgSisteBehandling();
        assertThat(saksbehandler.hentAvslåtteUttaksperioder())
                .as("Avslåtte uttaksperioder")
                .isEmpty();
    }
}



