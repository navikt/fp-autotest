package no.nav.foreldrepenger.autotest.foreldrepenger.foreldrepenger;

import static no.nav.foreldrepenger.vtp.dokumentgenerator.foreldrepengesoknad.erketyper.FordelingErketyper.STØNADSKONTOTYPE_FELLESPERIODE;
import static no.nav.foreldrepenger.vtp.dokumentgenerator.foreldrepengesoknad.erketyper.FordelingErketyper.STØNADSKONTOTYPE_FORELDREPENGER_FØR_FØDSEL;
import static no.nav.foreldrepenger.vtp.dokumentgenerator.foreldrepengesoknad.erketyper.FordelingErketyper.STØNADSKONTOTYPE_MØDREKVOTE;
import static no.nav.foreldrepenger.vtp.dokumentgenerator.foreldrepengesoknad.erketyper.FordelingErketyper.graderingsperiodeArbeidstaker;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import io.qameta.allure.Description;
import no.nav.foreldrepenger.autotest.aktoerer.Aktoer.Rolle;
import no.nav.foreldrepenger.autotest.base.ForeldrepengerTestBase;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.ForesloVedtakBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.aksjonspunktbekreftelse.avklarfakta.AvklarArbeidsforholdBekreftelse;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.AksjonspunktKoder;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.Behandling;
import no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.uttak.UttakResultatPeriode;
import no.nav.foreldrepenger.vtp.dokumentgenerator.foreldrepengesoknad.SøkersRolle;
import no.nav.foreldrepenger.vtp.dokumentgenerator.foreldrepengesoknad.builders.SøknadBuilder;
import no.nav.foreldrepenger.vtp.dokumentgenerator.foreldrepengesoknad.builders.ytelse.ForeldrepengerYtelseBuilder;
import no.nav.foreldrepenger.vtp.dokumentgenerator.foreldrepengesoknad.erketyper.SoekersRelasjonErketyper;
import no.nav.foreldrepenger.vtp.dokumentgenerator.foreldrepengesoknad.erketyper.SøknadErketyper;
import no.nav.foreldrepenger.vtp.dokumentgenerator.inntektsmelding.builders.InntektsmeldingBuilder;
import no.nav.foreldrepenger.vtp.kontrakter.TestscenarioDto;
import no.nav.foreldrepenger.vtp.testmodell.dokument.modell.koder.DokumenttypeId;
import no.nav.vedtak.felles.xml.soeknad.foreldrepenger.v3.Foreldrepenger;
import no.nav.vedtak.felles.xml.soeknad.uttak.v3.Fordeling;
import no.nav.vedtak.felles.xml.soeknad.uttak.v3.LukketPeriodeMedVedlegg;

@Execution(ExecutionMode.CONCURRENT)
@Tag("fpsak")
@Tag("foreldrepenger")
public class Termin extends ForeldrepengerTestBase {

    @Test
    @DisplayName("Mor søker med ett arbeidsforhold. Inntektmelding innsendt før søknad")
    @Description("Mor med ett arbeidsforhold sender inn inntektsmelding før søknad. " +
            "Forventer at vedtak bli fattet og det blir bare opprettet en behandling")
    public void MorSøkerMedEttArbeidsforholdInntektsmeldingFørSøknad() throws Exception {
        TestscenarioDto testscenario = opprettTestscenarioFraVTPTemplate("55");
        LocalDate termindato = LocalDate.now().plusWeeks(3);
        LocalDate startDatoForeldrepenger = termindato.minusWeeks(3);
        String aktørID = testscenario.getPersonopplysninger().getSøkerAktørIdent();

        fordel.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        List<InntektsmeldingBuilder> inntektsmeldinger = makeInntektsmeldingFromTestscenario(testscenario, startDatoForeldrepenger);
        Long saksnummer = fordel.sendInnInntektsmeldinger(inntektsmeldinger, testscenario);

        saksbehandler.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        saksbehandler.hentFagsak(saksnummer);
        saksbehandler.ventTilAksjonspunkt(AksjonspunktKoder.VENT_PÅ_SØKNAD);
        List<Behandling> behandlinger = saksbehandler.hentAlleBehandlingerForFagsak(saksnummer);
        verifiser(behandlinger.size() == 1, "Antall behandlinger er ikke 1");

        SøknadBuilder søknad = SøknadErketyper.foreldrepengesøknadTerminErketype(aktørID, SøkersRolle.MOR, 1, termindato);
        fordel.sendInnSøknad(søknad.build(), testscenario, DokumenttypeId.FOEDSELSSOKNAD_FORELDREPENGER, saksnummer);
        saksbehandler.ventTilAvsluttetBehandling();

        behandlinger = saksbehandler.hentAlleBehandlingerForFagsak(saksnummer);
        verifiser(behandlinger.size() == 1, "Antall behandlinger er ikke 1");
    }

    @Test
    @DisplayName("Mor søker sak behandlet før inntektsmelding mottatt")
    @Description("Mor søker og saken  blir behandlet før inntektsmelding er mottat basert på data fra inntektskomponenten, så mottas inntektsmeldingen ")
    public void MorSøkerMedEttArbeidsforholdInntektsmeldingPåGjennopptattSøknad() throws Exception {
        TestscenarioDto testscenario = opprettTestscenarioFraVTPTemplate("55");
        LocalDate termindato = LocalDate.now().minusWeeks(1);
        LocalDate startDatoForeldrepenger = termindato.minusWeeks(3);

        fordel.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        String aktørID = testscenario.getPersonopplysninger().getSøkerAktørIdent();
        SøknadBuilder søknad = SøknadErketyper.foreldrepengesøknadTerminErketype(aktørID, SøkersRolle.MOR, 1, termindato);
        Long saksnummer = fordel.sendInnSøknad(søknad.build(), testscenario, DokumenttypeId.FOEDSELSSOKNAD_FORELDREPENGER);

        saksbehandler.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        saksbehandler.hentFagsak(saksnummer);
        verifiser(saksbehandler.valgtBehandling.erSattPåVent(), "Behandling er ikke satt på vent etter uten inntektsmelding");


        saksbehandler.gjenopptaBehandling();
        saksbehandler.gjenopptaBehandling();

        verifiser(saksbehandler.harAksjonspunkt(AksjonspunktKoder.VURDER_ARBEIDSFORHOLD), "Mangler aksonspunkt for vurdering av arbeidsforhold (8050)");
        saksbehandler.hentAksjonspunktbekreftelse(AvklarArbeidsforholdBekreftelse.class)
                .bekreftArbeidsforholdErRelevant("BEDRIFT AS", true);
        saksbehandler.bekreftAksjonspunktBekreftelse(AvklarArbeidsforholdBekreftelse.class);


        verifiser(saksbehandler.harAksjonspunkt(AksjonspunktKoder.VURDER_FAKTA_FOR_ATFL_SN), "Mangler aksonspunkt for vurdering av fakta arbeid frilans (5058)");

        List<InntektsmeldingBuilder> inntektsmeldinger = makeInntektsmeldingFromTestscenario(testscenario, startDatoForeldrepenger);
        fordel.sendInnInntektsmeldinger(inntektsmeldinger, testscenario, saksnummer);

        saksbehandler.ventTilAksjonspunkt(AksjonspunktKoder.FORESLÅ_VEDTAK);
        verifiser(!saksbehandler.harAksjonspunkt(AksjonspunktKoder.VURDER_FAKTA_FOR_ATFL_SN), "Har uventet aksonspunkt - vurdering av fakta arbeid frilans (5058)");

        saksbehandler.bekreftAksjonspunktBekreftelse(ForesloVedtakBekreftelse.class);
    }

    @Tag("pending")
    @Test
    @Disabled
    public void MorSøkerMedEttArbeidsforholdOvergangFraYtelse() throws Exception {
        //TODO
    }

    @Test
    @DisplayName("Mor søker termin med avvik i gradering")
    @Description("Mor med to arbeidsforhold søker termin. Søknad inneholder gradering. En periode som er forflyttet i fht IM, " +
            "en periode som har feil graderingsprosent i fht IM, en periode som har feil orgnr i fht IM og en periode som " +
            "er ok.")
    public void morSøkerTerminEttArbeidsforhold_avvikIGradering() throws Exception {
        TestscenarioDto testscenario = opprettTestscenarioFraVTPTemplate("77");

        String søkerAktørId = testscenario.getPersonopplysninger().getSøkerAktørIdent();
        LocalDate termindato = LocalDate.now().plusWeeks(6);
        LocalDate fpstartdato = termindato.minusWeeks(3);
        // 138 - 40%
        String orgnr1 = testscenario.getScenariodata().getArbeidsforholdModell().getArbeidsforhold().get(0).getArbeidsgiverOrgnr();
        // 200 - 60%
        String orgnr2 = testscenario.getScenariodata().getArbeidsforholdModell().getArbeidsforhold().get(1).getArbeidsgiverOrgnr();

        Fordeling fordeling = new Fordeling();
        fordeling.setAnnenForelderErInformert(true);
        List<LukketPeriodeMedVedlegg> perioder = fordeling.getPerioder();;
        perioder.add(uttaksperiode(STØNADSKONTOTYPE_FORELDREPENGER_FØR_FØDSEL, fpstartdato, fpstartdato.plusWeeks(3).minusDays(1)));
        perioder.add(uttaksperiode(STØNADSKONTOTYPE_MØDREKVOTE, termindato, termindato.plusWeeks(6).minusDays(1)));
        perioder.add(graderingsperiodeArbeidstaker(STØNADSKONTOTYPE_MØDREKVOTE, termindato.plusWeeks(6), termindato.plusWeeks(9).minusDays(1), orgnr2, 40));
        perioder.add(uttaksperiode(STØNADSKONTOTYPE_MØDREKVOTE, termindato.plusWeeks(9), termindato.plusWeeks(12).minusDays(1)));
        perioder.add(graderingsperiodeArbeidstaker(STØNADSKONTOTYPE_MØDREKVOTE, termindato.plusWeeks(12), termindato.plusWeeks(15).minusDays(1), orgnr1, 10));
        perioder.add(graderingsperiodeArbeidstaker(STØNADSKONTOTYPE_FELLESPERIODE, termindato.plusWeeks(15), termindato.plusWeeks(18).minusDays(1), orgnr2, 20));
        perioder.add(graderingsperiodeArbeidstaker(STØNADSKONTOTYPE_FELLESPERIODE, termindato.plusWeeks(18), termindato.plusWeeks(21).minusDays(1), orgnr1, 30));

        fordel.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        String aktørID = testscenario.getPersonopplysninger().getSøkerAktørIdent();
        Foreldrepenger foreldrepenger = new ForeldrepengerYtelseBuilder(
                SoekersRelasjonErketyper.termin(1, termindato), fordeling)
                .build();

        SøknadBuilder søknad = new SøknadBuilder(foreldrepenger, aktørID, SøkersRolle.MOR);
        Long saksnummer = fordel.sendInnSøknad(søknad.build(), testscenario, DokumenttypeId.FOEDSELSSOKNAD_FORELDREPENGER);

        List<InntektsmeldingBuilder> inntektsmeldinger = makeInntektsmeldingFromTestscenario(testscenario, fpstartdato);
        fordel.sendInnInntektsmeldinger(inntektsmeldinger, testscenario, saksnummer);

        saksbehandler.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        saksbehandler.hentFagsak(saksnummer);
        List<UttakResultatPeriode> resultatPerioder = saksbehandler.valgtBehandling.getUttakResultatPerioder().getPerioderForSøker();
        verifiser(resultatPerioder.size() == 7, "Antall perioder er ikke 7.");
        verifiser(resultatPerioder.get(0).getPeriodeResultatType().kode.equals("INNVILGET"), "Perioden er ikke automatisk innvilget.");
        verifiser(resultatPerioder.get(1).getPeriodeResultatType().kode.equals("INNVILGET"), "Perioden er ikke automatisk innvilget.");
        verifiser(resultatPerioder.get(2).getPeriodeResultatType().kode.equals("INNVILGET"), "Perioden er ikke automatisk innvilget.");
        verifiser(resultatPerioder.get(3).getPeriodeResultatType().kode.equals("INNVILGET"), "Perioden er ikke automatisk innvilget.");
        verifiser(resultatPerioder.get(4).getPeriodeResultatType().kode.equals("INNVILGET"), "Perioden er ikke automatisk innvilget.");
        verifiser(resultatPerioder.get(5).getPeriodeResultatType().kode.equals("INNVILGET"), "Perioden er ikke automatisk innvilget.");
        verifiser(resultatPerioder.get(6).getPeriodeResultatType().kode.equals("INNVILGET"), "Perioden er ikke automatisk innvilget.");
        verifiser(resultatPerioder.get(2).getGraderingInnvilget().equals(true), "Gradering ikke innvilget");
        verifiser(resultatPerioder.get(4).getGraderingInnvilget().equals(true), "Gradering ikke innvilget");
        verifiser(resultatPerioder.get(5).getGraderingInnvilget().equals(true), "Gradering ikke innvilget");
        verifiser(resultatPerioder.get(6).getGraderingInnvilget().equals(true), "Gradering ikke innvilget");
        verifiser(resultatPerioder.get(2).getGradertArbeidsprosent().compareTo(BigDecimal.valueOf(40))== 0, "Feil graderingsprosent");
        verifiser(resultatPerioder.get(4).getGradertArbeidsprosent().compareTo(BigDecimal.valueOf(10))== 0, "Feil graderingsprosent");
        verifiser(resultatPerioder.get(5).getGradertArbeidsprosent().compareTo(BigDecimal.valueOf(20))== 0, "Feil graderingsprosent");
        verifiser(resultatPerioder.get(6).getGradertArbeidsprosent().compareTo(BigDecimal.valueOf(30))== 0, "Feil graderingsprosent");
        verifiser(saksbehandler.valgtBehandling.status.kode.equals("AVSLU"), "Behandlingen har ikke status avsluttet.");
    }

    @Test
    @DisplayName("Mor søker termin uten FPFF")
    @Description("Mor søker termin uten periode for foreldrepenger før fødsel. Skjæringstidspunkt skal være 3 uker før termindato.")
    public void morSokerTerminUtenFPFFperiode() throws Exception {
        TestscenarioDto testscenario = opprettTestscenarioFraVTPTemplate("55");
        String søkerAktørId = testscenario.getPersonopplysninger().getSøkerAktørIdent();
        LocalDate termindato = LocalDate.now().plusWeeks(3);

        Fordeling fordeling = new Fordeling();
        fordeling.setAnnenForelderErInformert(true);
        List<LukketPeriodeMedVedlegg> perioder = fordeling.getPerioder();
        perioder.add(uttaksperiode(STØNADSKONTOTYPE_MØDREKVOTE, termindato, termindato.plusWeeks(15).minusDays(1)));

        fordel.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        String aktørID = testscenario.getPersonopplysninger().getSøkerAktørIdent();
        Foreldrepenger foreldrepenger = new ForeldrepengerYtelseBuilder(
                SoekersRelasjonErketyper.termin(1, termindato), fordeling)
                .build();

        SøknadBuilder søknad = new SøknadBuilder(foreldrepenger, søkerAktørId, SøkersRolle.MOR);

        Long saksnummer = fordel.sendInnSøknad(søknad.build(), testscenario, DokumenttypeId.FOEDSELSSOKNAD_FORELDREPENGER);

        List<InntektsmeldingBuilder> inntektsmeldinger = makeInntektsmeldingFromTestscenario(testscenario, termindato);
        fordel.sendInnInntektsmeldinger(inntektsmeldinger, testscenario, saksnummer);

        saksbehandler.erLoggetInnMedRolle(Rolle.SAKSBEHANDLER);
        saksbehandler.hentFagsak(saksnummer);

        List<UttakResultatPeriode> resultatPerioder = saksbehandler.valgtBehandling.hentUttaksperioder();
        verifiser(resultatPerioder.size() == 2, "Det er ikke blitt opprettet riktig antall perioder.");
        verifiser(resultatPerioder.get(0).getPeriodeResultatType().kode.equals("INNVILGET"), "Perioden søkt for skal være innvilget.");
        verifiser(resultatPerioder.get(0).getAktiviteter().get(0).getStønadskontoType().kode.equals("MØDREKVOTE"), "Feil stønadskontotype.");
        verifiser(resultatPerioder.get(1).getPeriodeResultatType().kode.equals("INNVILGET"), "Perioden søkt for skal være innvilget.");
        verifiser(resultatPerioder.get(1).getAktiviteter().get(0).getStønadskontoType().kode.equals("MØDREKVOTE"), "Feil stønadskontotype.");
        String skjaeringstidspunkt = termindato.minusWeeks(3).toString();
        verifiser(saksbehandler.valgtBehandling.behandlingsresultat.getSkjaeringstidspunktForeldrepenger().equals(skjaeringstidspunkt), "Mismatch på skjæringstidspunkt.");


    }

}
