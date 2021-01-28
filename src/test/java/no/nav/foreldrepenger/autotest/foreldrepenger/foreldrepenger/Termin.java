package no.nav.foreldrepenger.autotest.foreldrepenger.foreldrepenger;

import static no.nav.foreldrepenger.autotest.domain.foreldrepenger.Stønadskonto.FELLESPERIODE;
import static no.nav.foreldrepenger.autotest.domain.foreldrepenger.Stønadskonto.FORELDREPENGER_FØR_FØDSEL;
import static no.nav.foreldrepenger.autotest.domain.foreldrepenger.Stønadskonto.MØDREKVOTE;
import static no.nav.foreldrepenger.autotest.erketyper.FordelingErketyper.generiskFordeling;
import static no.nav.foreldrepenger.autotest.erketyper.InntektsmeldingForeldrepengeErketyper.lagInntektsmelding;
import static no.nav.foreldrepenger.autotest.erketyper.InntektsmeldingForeldrepengeErketyper.makeInntektsmeldingFromTestscenario;
import static no.nav.foreldrepenger.autotest.erketyper.SøknadForeldrepengerErketyper.lagSøknadForeldrepengerTermin;
import static no.nav.foreldrepenger.autotest.erketyper.UttaksperioderErketyper.graderingsperiodeArbeidstaker;
import static no.nav.foreldrepenger.autotest.erketyper.UttaksperioderErketyper.uttaksperiode;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import io.qameta.allure.Description;
import no.nav.foreldrepenger.autotest.aktoerer.Aktoer;
import no.nav.foreldrepenger.autotest.aktoerer.Aktoer.Rolle;
import no.nav.foreldrepenger.autotest.base.ForeldrepengerTestBase;
import no.nav.foreldrepenger.autotest.dokumentgenerator.foreldrepengesoknad.SøkersRolle;
import no.nav.foreldrepenger.autotest.dokumentgenerator.foreldrepengesoknad.builders.ForeldrepengerBuilder;
import no.nav.foreldrepenger.autotest.dokumentgenerator.inntektsmelding.builders.InntektsmeldingBuilder;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.FatterVedtakBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.ForeslåVedtakBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.avklarfakta.AvklarArbeidsforholdBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.AksjonspunktKoder;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.Behandling;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.uttak.UttakResultatPeriode;
import no.nav.foreldrepenger.vtp.kontrakter.TestscenarioDto;
import no.nav.foreldrepenger.vtp.testmodell.dokument.modell.koder.DokumenttypeId;

@Execution(ExecutionMode.CONCURRENT)
@Tag("fpsak")
@Tag("foreldrepenger")
public class Termin extends ForeldrepengerTestBase {

    @Test
    @DisplayName("Mor søker med ett arbeidsforhold. Inntektmelding innsendt før søknad")
    @Description("Mor med ett arbeidsforhold sender inn inntektsmelding før søknad. " +
            "Forventer at vedtak bli fattet og det blir bare opprettet en behandling")
    public void MorSøkerMedEttArbeidsforholdInntektsmeldingFørSøknad() {
        TestscenarioDto testscenario = opprettTestscenario("55");
        LocalDate termindato = LocalDate.now().plusWeeks(3);
        LocalDate startDatoForeldrepenger = termindato.minusWeeks(3);
        String aktørID = testscenario.personopplysninger().søkerAktørIdent();

        fordel.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        List<InntektsmeldingBuilder> inntektsmeldinger = makeInntektsmeldingFromTestscenario(testscenario,
                startDatoForeldrepenger);
        Long saksnummer = fordel.sendInnInntektsmeldinger(inntektsmeldinger, testscenario);

        saksbehandler.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        saksbehandler.hentFagsak(saksnummer);
        saksbehandler.hentAksjonspunkt(AksjonspunktKoder.VENT_PÅ_SØKNAD);
        List<Behandling> behandlinger = saksbehandler.hentAlleBehandlingerForFagsak(saksnummer);
        verifiserLikhet(behandlinger.size(),1, "Antall behandlinger er ikke 1");

        ForeldrepengerBuilder søknad = lagSøknadForeldrepengerTermin(termindato, aktørID, SøkersRolle.MOR);
        fordel.sendInnSøknad(søknad.build(), testscenario, DokumenttypeId.FOEDSELSSOKNAD_FORELDREPENGER, saksnummer);
        saksbehandler.ventTilAvsluttetBehandling();

        verifiserLikhet(saksbehandler.valgtBehandling.hentBehandlingsresultat(), "INNVILGET");
        behandlinger = saksbehandler.hentAlleBehandlingerForFagsak(saksnummer);
        verifiserLikhet(behandlinger.size(),1, "Antall behandlinger er ikke 1");
    }

    @Test
    @DisplayName("Mor søker sak behandlet før inntektsmelding mottatt")
    @Description("Mor søker og saken blir behandlet før inntektsmelding er mottat basert på data fra " +
            "inntektskomponenten, så mottas inntektsmeldingen")
    public void MorSøkerMedEttArbeidsforholdInntektsmeldingPåGjennopptattSøknad() {
        TestscenarioDto testscenario = opprettTestscenario("55");
        LocalDate termindato = LocalDate.now().minusWeeks(1);
        LocalDate startDatoForeldrepenger = termindato.minusWeeks(3);
        String søkerIdent = testscenario.personopplysninger().søkerIdent();

        fordel.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        String aktørID = testscenario.personopplysninger().søkerAktørIdent();
        ForeldrepengerBuilder søknad = lagSøknadForeldrepengerTermin(termindato, aktørID, SøkersRolle.MOR);
        long saksnummer = fordel.sendInnSøknad(søknad.build(), testscenario,
                DokumenttypeId.FOEDSELSSOKNAD_FORELDREPENGER);

        saksbehandler.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        saksbehandler.hentFagsak(saksnummer);
        verifiser(saksbehandler.valgtBehandling.erSattPåVent(),
                "Behandling er ikke satt på vent etter uten inntektsmelding");

        saksbehandler.hentAksjonspunkt(AksjonspunktKoder.AUTO_VENTER_PÅ_KOMPLETT_SØKNAD);
        saksbehandler.gjenopptaBehandling();
        saksbehandler.hentAksjonspunkt(AksjonspunktKoder.AUTO_VENT_ETTERLYST_INNTEKTSMELDING_KODE);
        saksbehandler.gjenopptaBehandling();

        var ab = saksbehandler.hentAksjonspunktbekreftelse(AvklarArbeidsforholdBekreftelse.class)
                .bekreftArbeidsforholdErAktivt("910909088", true);
        saksbehandler.bekreftAksjonspunkt(ab);

        saksbehandler.hentAksjonspunkt(AksjonspunktKoder.VURDER_FAKTA_FOR_ATFL_SN);

        InntektsmeldingBuilder inntektsmeldinger = lagInntektsmelding(
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

        beslutter.erLoggetInnMedRolle(Aktoer.Rolle.BESLUTTER);
        beslutter.hentFagsak(saksnummer);
        var fatterVedtakBekreftelse = beslutter.hentAksjonspunktbekreftelse(FatterVedtakBekreftelse.class);
        fatterVedtakBekreftelse.godkjennAksjonspunkter(beslutter.hentAksjonspunktSomSkalTilTotrinnsBehandling());
        beslutter.fattVedtakOgVentTilAvsluttetBehandling(fatterVedtakBekreftelse);
        verifiserLikhet(saksbehandler.valgtBehandling.hentBehandlingsresultat(), "INNVILGET");
    }

    @Test
    @DisplayName("Mor søker termin med avvik i gradering")
    @Description("Mor med to arbeidsforhold søker termin. Søknad inneholder gradering. En periode som er forflyttet i" +
            "fht IM, en periode som har feil graderingsprosent i fht IM, en periode som har feil orgnr i fht IM og " +
            "en periode som er ok.")
    public void morSøkerTerminEttArbeidsforhold_avvikIGradering() {
        TestscenarioDto testscenario = opprettTestscenario("77");

        LocalDate termindato = LocalDate.now().plusWeeks(6);
        LocalDate fpstartdato = termindato.minusWeeks(3);
        // 138 - 40%
        String orgnr1 = testscenario.scenariodataDto().arbeidsforholdModell().arbeidsforhold().get(0)
                .arbeidsgiverOrgnr();
        // 200 - 60%
        String orgnr2 = testscenario.scenariodataDto().arbeidsforholdModell().arbeidsforhold().get(1)
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

        fordel.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        String aktørID = testscenario.personopplysninger().søkerAktørIdent();
        ForeldrepengerBuilder søknad = lagSøknadForeldrepengerTermin(termindato, aktørID, SøkersRolle.MOR)
                .medFordeling(fordeling);
        long saksnummer = fordel.sendInnSøknad(søknad.build(), testscenario,
                DokumenttypeId.FOEDSELSSOKNAD_FORELDREPENGER);

        List<InntektsmeldingBuilder> inntektsmeldinger = makeInntektsmeldingFromTestscenario(testscenario, fpstartdato);
        fordel.sendInnInntektsmeldinger(inntektsmeldinger, testscenario, saksnummer);

        saksbehandler.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        saksbehandler.hentFagsak(saksnummer);
        saksbehandler.ventTilAvsluttetBehandling();
        verifiserLikhet(saksbehandler.valgtBehandling.hentBehandlingsresultat(), "INNVILGET");
        List<UttakResultatPeriode> resultatPerioder = saksbehandler.valgtBehandling.getUttakResultatPerioder()
                .getPerioderSøker();
        verifiser(resultatPerioder.size() == 7, "Antall perioder er ikke 7.");
        verifiser(resultatPerioder.get(0).getPeriodeResultatType().kode.equals("INNVILGET"),
                "Perioden er ikke automatisk innvilget.");
        verifiser(resultatPerioder.get(1).getPeriodeResultatType().kode.equals("INNVILGET"),
                "Perioden er ikke automatisk innvilget.");
        verifiser(resultatPerioder.get(2).getPeriodeResultatType().kode.equals("INNVILGET"),
                "Perioden er ikke automatisk innvilget.");
        verifiser(resultatPerioder.get(3).getPeriodeResultatType().kode.equals("INNVILGET"),
                "Perioden er ikke automatisk innvilget.");
        verifiser(resultatPerioder.get(4).getPeriodeResultatType().kode.equals("INNVILGET"),
                "Perioden er ikke automatisk innvilget.");
        verifiser(resultatPerioder.get(5).getPeriodeResultatType().kode.equals("INNVILGET"),
                "Perioden er ikke automatisk innvilget.");
        verifiser(resultatPerioder.get(6).getPeriodeResultatType().kode.equals("INNVILGET"),
                "Perioden er ikke automatisk innvilget.");
        verifiser(resultatPerioder.get(2).getGraderingInnvilget().equals(true), "Gradering ikke innvilget");
        verifiser(resultatPerioder.get(4).getGraderingInnvilget().equals(true), "Gradering ikke innvilget");
        verifiser(resultatPerioder.get(5).getGraderingInnvilget().equals(true), "Gradering ikke innvilget");
        verifiser(resultatPerioder.get(6).getGraderingInnvilget().equals(true), "Gradering ikke innvilget");
        verifiser(resultatPerioder.get(2).getGradertArbeidsprosent().compareTo(BigDecimal.valueOf(40)) == 0,
                "Feil graderingsprosent");
        verifiser(resultatPerioder.get(4).getGradertArbeidsprosent().compareTo(BigDecimal.valueOf(10)) == 0,
                "Feil graderingsprosent");
        verifiser(resultatPerioder.get(5).getGradertArbeidsprosent().compareTo(BigDecimal.valueOf(20)) == 0,
                "Feil graderingsprosent");
        verifiser(resultatPerioder.get(6).getGradertArbeidsprosent().compareTo(BigDecimal.valueOf(30)) == 0,
                "Feil graderingsprosent");
    }

    @Test
    @DisplayName("Mor søker termin uten FPFF")
    @Description("Mor søker termin uten periode for foreldrepenger før fødsel. Skjæringstidspunkt skal være 3 uker før termindato.")
    public void morSokerTerminUtenFPFFperiode() {
        TestscenarioDto testscenario = opprettTestscenario("55");
        LocalDate termindato = LocalDate.now().plusWeeks(3);

        var fordeling = generiskFordeling(
                uttaksperiode(MØDREKVOTE, termindato, termindato.plusWeeks(15).minusDays(1)));

        fordel.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        String aktørID = testscenario.personopplysninger().søkerAktørIdent();
        ForeldrepengerBuilder søknad = lagSøknadForeldrepengerTermin(termindato, aktørID, SøkersRolle.MOR)
                .medFordeling(fordeling);

        long saksnummer = fordel.sendInnSøknad(søknad.build(), testscenario,
                DokumenttypeId.FOEDSELSSOKNAD_FORELDREPENGER);

        InntektsmeldingBuilder inntektsmeldinger = lagInntektsmelding(
                testscenario.scenariodataDto().inntektskomponentModell().inntektsperioder().get(0).beløp(),
                testscenario.personopplysninger().søkerIdent(),
                termindato,
                testscenario.scenariodataDto().arbeidsforholdModell().arbeidsforhold().get(0).arbeidsgiverOrgnr());
        fordel.sendInnInntektsmelding(
                inntektsmeldinger,
                testscenario.personopplysninger().søkerAktørIdent(),
                testscenario.personopplysninger().søkerIdent(),
                saksnummer);

        saksbehandler.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        saksbehandler.hentFagsak(saksnummer);
        saksbehandler.ventTilAvsluttetBehandling();
        verifiserLikhet(saksbehandler.valgtBehandling.hentBehandlingsresultat(), "INNVILGET");
        List<UttakResultatPeriode> resultatPerioder = saksbehandler.valgtBehandling.hentUttaksperioder();
        verifiser(resultatPerioder.size() == 2, "Det er ikke blitt opprettet riktig antall perioder.");
        verifiser(resultatPerioder.get(0).getPeriodeResultatType().kode.equals("INNVILGET"),
                "Perioden søkt for skal være innvilget.");
        verifiser(resultatPerioder.get(0).getAktiviteter().get(0).getStønadskontoType().equals(MØDREKVOTE),
                "Feil stønadskontotype.");
        verifiser(resultatPerioder.get(1).getPeriodeResultatType().kode.equals("INNVILGET"),
                "Perioden søkt for skal være innvilget.");
        verifiser(resultatPerioder.get(1).getAktiviteter().get(0).getStønadskontoType().equals(MØDREKVOTE),
                "Feil stønadskontotype.");
        LocalDate skjaeringstidspunkt = termindato.minusWeeks(3);
        verifiser(saksbehandler.valgtBehandling.behandlingsresultat.getSkjæringstidspunkt().getDato()
                .equals(skjaeringstidspunkt), "Mismatch på skjæringstidspunkt.");
    }

}
