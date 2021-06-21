package no.nav.foreldrepenger.autotest.fpsak.foreldrepenger;

import static no.nav.foreldrepenger.autotest.domain.foreldrepenger.Stønadskonto.FELLESPERIODE;
import static no.nav.foreldrepenger.autotest.domain.foreldrepenger.Stønadskonto.FORELDREPENGER_FØR_FØDSEL;
import static no.nav.foreldrepenger.autotest.domain.foreldrepenger.Stønadskonto.MØDREKVOTE;
import static no.nav.foreldrepenger.autotest.erketyper.FordelingErketyper.generiskFordeling;
import static no.nav.foreldrepenger.autotest.erketyper.InntektsmeldingForeldrepengeErketyper.lagInntektsmelding;
import static no.nav.foreldrepenger.autotest.erketyper.InntektsmeldingForeldrepengeErketyper.makeInntektsmeldingFromTestscenario;
import static no.nav.foreldrepenger.autotest.erketyper.SøknadForeldrepengerErketyper.lagSøknadForeldrepengerTermin;
import static no.nav.foreldrepenger.autotest.erketyper.UttaksperioderErketyper.graderingsperiodeArbeidstaker;
import static no.nav.foreldrepenger.autotest.erketyper.UttaksperioderErketyper.uttaksperiode;
import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import io.qameta.allure.Description;
import no.nav.foreldrepenger.autotest.base.ForeldrepengerTestBase;
import no.nav.foreldrepenger.autotest.dokumentgenerator.foreldrepengesoknad.SøkersRolle;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.BehandlingResultatType;
import no.nav.foreldrepenger.autotest.domain.foreldrepenger.PeriodeResultatType;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.FatterVedtakBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.ForeslåVedtakBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.avklarfakta.AvklarArbeidsforholdBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.AksjonspunktKoder;
import no.nav.foreldrepenger.autotest.util.testscenario.modell.Orgnummer;
import no.nav.foreldrepenger.vtp.testmodell.dokument.modell.koder.DokumenttypeId;

@Tag("fpsak")
@Tag("foreldrepenger")
class Termin extends ForeldrepengerTestBase {

    @Test
    @DisplayName("Mor søker med ett arbeidsforhold. Inntektmelding innsendt før søknad")
    @Description("Mor med ett arbeidsforhold sender inn inntektsmelding før søknad. " +
            "Forventer at vedtak bli fattet og det blir bare opprettet en behandling")
    void MorSøkerMedEttArbeidsforholdInntektsmeldingFørSøknad() {
        var testscenario = opprettTestscenario("55");
        var termindato = LocalDate.now().plusWeeks(3);
        var startDatoForeldrepenger = termindato.minusWeeks(3);
        var aktørID = testscenario.personopplysninger().søkerAktørIdent();

        var inntektsmeldinger = makeInntektsmeldingFromTestscenario(testscenario,
                startDatoForeldrepenger);
        var saksnummer = fordel.sendInnInntektsmeldinger(inntektsmeldinger, testscenario);

        saksbehandler.hentFagsak(saksnummer);
        saksbehandler.hentAksjonspunkt(AksjonspunktKoder.VENT_PÅ_SØKNAD);
        var behandlinger = saksbehandler.hentAlleBehandlingerForFagsak(saksnummer);
        assertThat(behandlinger)
                .as("Antall behandlinger")
                .hasSize(1);

        var søknad = lagSøknadForeldrepengerTermin(termindato, aktørID, SøkersRolle.MOR);
        fordel.sendInnSøknad(søknad.build(), testscenario, DokumenttypeId.SØKNAD_FORELDREPENGER_FØDSEL, saksnummer);
        saksbehandler.ventTilAvsluttetBehandling();

        assertThat(saksbehandler.valgtBehandling.hentBehandlingsresultat())
                .as("Behandlingsresultat")
                .isEqualTo(BehandlingResultatType.INNVILGET);
        assertThat(saksbehandler.hentAlleBehandlingerForFagsak(saksnummer))
                .as("Antall behandlinger")
                .hasSize(1);
    }

    @Test
    @DisplayName("Mor søker sak behandlet før inntektsmelding mottatt")
    @Description("Mor søker og saken blir behandlet før inntektsmelding er mottat basert på data fra " +
            "inntektskomponenten, så mottas inntektsmeldingen")
    void MorSøkerMedEttArbeidsforholdInntektsmeldingPåGjennopptattSøknad() {
        var testscenario = opprettTestscenario("55");
        var termindato = LocalDate.now().minusWeeks(1);
        var startDatoForeldrepenger = termindato.minusWeeks(3);
        var søkerIdent = testscenario.personopplysninger().søkerIdent();

        var aktørID = testscenario.personopplysninger().søkerAktørIdent();
        var søknad = lagSøknadForeldrepengerTermin(termindato, aktørID, SøkersRolle.MOR);
        var saksnummer = fordel.sendInnSøknad(søknad.build(), testscenario,
                DokumenttypeId.SØKNAD_FORELDREPENGER_FØDSEL);

        saksbehandler.hentFagsak(saksnummer);
        assertThat(saksbehandler.valgtBehandling.erSattPåVent())
                .as("Behandling er satt på vent (Behandling er ikke satt på vent etter uten inntektsmelding)")
                .isTrue();

        saksbehandler.hentAksjonspunkt(AksjonspunktKoder.AUTO_VENTER_PÅ_KOMPLETT_SØKNAD);
        saksbehandler.gjenopptaBehandling();
        saksbehandler.hentAksjonspunkt(AksjonspunktKoder.AUTO_VENT_ETTERLYST_INNTEKTSMELDING_KODE);
        saksbehandler.gjenopptaBehandling();

        var ab = saksbehandler
                .hentAksjonspunktbekreftelse(AvklarArbeidsforholdBekreftelse.class)
                .bekreftArbeidsforholdErAktivt(new Orgnummer("910909088"), true);
        saksbehandler.bekreftAksjonspunkt(ab);

        saksbehandler.hentAksjonspunkt(AksjonspunktKoder.VURDER_FAKTA_FOR_ATFL_SN);

        var inntektsmeldinger = lagInntektsmelding(
                testscenario.scenariodataDto().inntektskomponentModell().inntektsperioder().get(0).beløp(),
                søkerIdent,
                startDatoForeldrepenger,
                testscenario.scenariodataDto().arbeidsforholdModell().arbeidsforhold().get(0).arbeidsgiverOrgnr());
        fordel.sendInnInntektsmelding(
                inntektsmeldinger,
                testscenario.personopplysninger().søkerAktørIdent(),
                søkerIdent,
                saksnummer);

        saksbehandler.hentFagsak(saksnummer);
        saksbehandler.bekreftAksjonspunktMedDefaultVerdier(ForeslåVedtakBekreftelse.class);

        beslutter.hentFagsak(saksnummer);
        var fatterVedtakBekreftelse = beslutter.hentAksjonspunktbekreftelse(FatterVedtakBekreftelse.class);
        fatterVedtakBekreftelse.godkjennAksjonspunkter(beslutter.hentAksjonspunktSomSkalTilTotrinnsBehandling());
        beslutter.fattVedtakOgVentTilAvsluttetBehandling(fatterVedtakBekreftelse);
        assertThat(beslutter.valgtBehandling.hentBehandlingsresultat())
                .as("Behandlingsresultat")
                .isEqualTo(BehandlingResultatType.INNVILGET);
    }

    @Test
    @DisplayName("Mor søker termin med avvik i gradering")
    @Description("Mor med to arbeidsforhold søker termin. Søknad inneholder gradering. En periode som er forflyttet i" +
            "fht IM, en periode som har feil graderingsprosent i fht IM, en periode som har feil orgnr i fht IM og " +
            "en periode som er ok.")
    void morSøkerTerminEttArbeidsforhold_avvikIGradering() {
        var testscenario = opprettTestscenario("77");

        var termindato = LocalDate.now().plusWeeks(6);
        var fpstartdato = termindato.minusWeeks(3);
        // 138 - 40%
        var orgnr1 = testscenario.scenariodataDto().arbeidsforholdModell().arbeidsforhold().get(0)
                .arbeidsgiverOrgnr();
        // 200 - 60%
        var orgnr2 = testscenario.scenariodataDto().arbeidsforholdModell().arbeidsforhold().get(1)
                .arbeidsgiverOrgnr();
        var fordeling = generiskFordeling(
                uttaksperiode(FORELDREPENGER_FØR_FØDSEL, fpstartdato, fpstartdato.plusWeeks(3).minusDays(1)),
                uttaksperiode(MØDREKVOTE, termindato, termindato.plusWeeks(6).minusDays(1)),
                graderingsperiodeArbeidstaker(MØDREKVOTE, termindato.plusWeeks(6),
                        termindato.plusWeeks(9).minusDays(1), orgnr2, 40),
                uttaksperiode(MØDREKVOTE, termindato.plusWeeks(9), termindato.plusWeeks(12).minusDays(1)),
                graderingsperiodeArbeidstaker(MØDREKVOTE, termindato.plusWeeks(12),
                        termindato.plusWeeks(15).minusDays(1), orgnr1, 10),
                graderingsperiodeArbeidstaker(FELLESPERIODE, termindato.plusWeeks(15),
                        termindato.plusWeeks(18).minusDays(1), orgnr2, 20),
                graderingsperiodeArbeidstaker(FELLESPERIODE, termindato.plusWeeks(18),
                        termindato.plusWeeks(21).minusDays(1), orgnr1, 30));

        var aktørID = testscenario.personopplysninger().søkerAktørIdent();
        var søknad = lagSøknadForeldrepengerTermin(termindato, aktørID, SøkersRolle.MOR)
                .medFordeling(fordeling);
        var saksnummer = fordel.sendInnSøknad(søknad.build(), testscenario,
                DokumenttypeId.SØKNAD_FORELDREPENGER_FØDSEL);

        var inntektsmeldinger = makeInntektsmeldingFromTestscenario(testscenario, fpstartdato);
        fordel.sendInnInntektsmeldinger(inntektsmeldinger, testscenario, saksnummer);

        saksbehandler.hentFagsak(saksnummer);
        saksbehandler.ventTilAvsluttetBehandling();
        assertThat(saksbehandler.valgtBehandling.hentBehandlingsresultat())
                .as("Behandlingsresultat")
                .isEqualTo(BehandlingResultatType.INNVILGET);
        var resultatPerioder = saksbehandler.valgtBehandling.getUttakResultatPerioder()
                .getPerioderSøker();
        assertThat(resultatPerioder)
                .as("Antall uttaksperioder")
                .hasSize(7);
        assertThat(resultatPerioder)
                .as("Alle perioderresultatstype for uttaksperiodene skal være INNVILGET, men er ikke det")
                .allMatch(p -> p.getPeriodeResultatType().equals(PeriodeResultatType.INNVILGET));
        assertThat(resultatPerioder.get(2).getGraderingInnvilget())
                .as("Gradering innvilget")
                .isTrue();
        assertThat(resultatPerioder.get(2).getGradertArbeidsprosent())
                .as("Graderingsprosent i periode")
                .isEqualByComparingTo(BigDecimal.valueOf(40));
        assertThat(resultatPerioder.get(4).getGraderingInnvilget())
                .as("Gradering innvilget")
                .isTrue();
        assertThat(resultatPerioder.get(4).getGradertArbeidsprosent())
                .as("Graderingsprosent i periode")
                .isEqualByComparingTo(BigDecimal.valueOf(10));
        assertThat(resultatPerioder.get(5).getGraderingInnvilget())
                .as("Gradering innvilget")
                .isTrue();
        assertThat(resultatPerioder.get(5).getGradertArbeidsprosent())
                .as("Graderingsprosent i periode")
                .isEqualByComparingTo(BigDecimal.valueOf(20));
        assertThat(resultatPerioder.get(6).getGraderingInnvilget())
                .as("Gradering innvilget")
                .isTrue();
        assertThat(resultatPerioder.get(6).getGradertArbeidsprosent())
                .as("Graderingsprosent i periode")
                .isEqualByComparingTo(BigDecimal.valueOf(30));
    }

    @Test
    @DisplayName("Mor søker termin uten FPFF")
    @Description("Mor søker termin uten periode for foreldrepenger før fødsel. Skjæringstidspunkt skal være 3 uker før termindato.")
    void morSokerTerminUtenFPFFperiode() {
        var testscenario = opprettTestscenario("55");
        var termindato = LocalDate.now().plusWeeks(3);
        var fordeling = generiskFordeling(
                uttaksperiode(MØDREKVOTE, termindato, termindato.plusWeeks(15).minusDays(1)));
        var aktørID = testscenario.personopplysninger().søkerAktørIdent();
        var søknad = lagSøknadForeldrepengerTermin(termindato, aktørID, SøkersRolle.MOR)
                .medFordeling(fordeling);
        var saksnummer = fordel.sendInnSøknad(søknad.build(), testscenario,
                DokumenttypeId.SØKNAD_FORELDREPENGER_FØDSEL);

        var inntektsmeldinger = lagInntektsmelding(
                testscenario.scenariodataDto().inntektskomponentModell().inntektsperioder().get(0).beløp(),
                testscenario.personopplysninger().søkerIdent(),
                termindato,
                testscenario.scenariodataDto().arbeidsforholdModell().arbeidsforhold().get(0).arbeidsgiverOrgnr());
        fordel.sendInnInntektsmelding(
                inntektsmeldinger,
                testscenario.personopplysninger().søkerAktørIdent(),
                testscenario.personopplysninger().søkerIdent(),
                saksnummer);

        saksbehandler.hentFagsak(saksnummer);
        saksbehandler.ventTilAvsluttetBehandling();
        assertThat(saksbehandler.valgtBehandling.hentBehandlingsresultat())
                .as("Behandlingsresultat")
                .isEqualTo(BehandlingResultatType.INNVILGET);
        var resultatPerioder = saksbehandler.valgtBehandling.hentUttaksperioder();
        assertThat(resultatPerioder)
                .as("Antall uttaksperioder")
                .hasSize(2);
        assertThat(resultatPerioder.get(0).getPeriodeResultatType())
                .as("Perioderesultatstype for uttaksperiode")
                .isEqualTo(PeriodeResultatType.INNVILGET);
        assertThat(resultatPerioder.get(0).getAktiviteter().get(0).getStønadskontoType())
                .as("Stønadskontotype for første uttaksperiode i første aktivitet")
                .isEqualTo(MØDREKVOTE);
        assertThat(resultatPerioder.get(1).getPeriodeResultatType())
                .as("Perioderesultatstype for uttaksperiode")
                .isEqualTo(PeriodeResultatType.INNVILGET);
        assertThat(resultatPerioder.get(1).getAktiviteter().get(0).getStønadskontoType())
                .as("Stønadskontotype for uttaksperiode i aktivitet")
                .isEqualTo(MØDREKVOTE);
        var skjaeringstidspunkt = termindato.minusWeeks(3);
        assertThat(saksbehandler.valgtBehandling.behandlingsresultat.getSkjæringstidspunkt().getDato())
                .as("Skjæringstidspunkt")
                .isEqualTo(skjaeringstidspunkt);
    }
}
