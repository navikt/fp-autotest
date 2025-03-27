package no.nav.foreldrepenger.autotest.base;

import static no.nav.foreldrepenger.autotest.base.Paragrafer.P_14_10;
import static no.nav.foreldrepenger.autotest.base.Paragrafer.P_14_11;
import static no.nav.foreldrepenger.autotest.base.Paragrafer.P_14_12;
import static no.nav.foreldrepenger.autotest.base.Paragrafer.P_14_4;
import static no.nav.foreldrepenger.autotest.base.Paragrafer.P_14_6;
import static no.nav.foreldrepenger.autotest.base.Paragrafer.P_14_7;
import static no.nav.foreldrepenger.autotest.base.Paragrafer.P_14_9;
import static no.nav.foreldrepenger.autotest.base.Paragrafer.P_8_30;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Set;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.text.PDFTextStripper;
import org.assertj.core.api.Fail;
import org.assertj.core.api.SoftAssertions;

import no.nav.foreldrepenger.autotest.brev.BrevAssertionBuilder;
import no.nav.foreldrepenger.autotest.brev.BrevFormateringUtils;
import no.nav.foreldrepenger.common.domain.Fødselsnummer;
import no.nav.foreldrepenger.common.domain.Saksnummer;

public class BrevTestBase {

    protected static void validerBrevetInneholderForventedeTekstavsnitt(byte[] pdfBytes, Set<String> tekstavsnitt) {
        try (var document = Loader.loadPDF(pdfBytes)) {
            // Fjerne linjeskift for å kunne søke på tvers av linjer
            var pdfTekst = new PDFTextStripper().getText(document).replace("\n", "");
            var softAssert = new SoftAssertions();
            tekstavsnitt.forEach(skalFinnes -> softAssert.assertThat(pdfTekst.contains(skalFinnes))
                    .as("'%s' finnes ikke".formatted(skalFinnes))
                    .isTrue());
            softAssert.assertAll();
        } catch (IOException e) {
            Fail.fail("Kunne ikke lese PDFen", e);
        }
    }

    protected static BrevAssertionBuilder engangsstønadInnvilgetAssertionsBuilder(Fødselsnummer fnr, Saksnummer saksnummer) {
        return alleYtelserFellesAssertionsBuilder(fnr, saksnummer).medEgenndefinertAssertion(
                        "Du har rett til engangsstønad og får utbetalt 92 648 kroner innen en uke.")
                .medEgenndefinertAssertion("Stønaden er skattefri, og du kan sjekke utbetalingen din ved å logge inn på")
                .medOverskriftOmInnvilgetEnagangsstønad()
                .medKapittelDuHarRettTilKlage()
                .medTekstOmVedtaketEtterFolketrygdloven()
                .medParagraf(Paragrafer.P_14_17);
    }

    protected static BrevAssertionBuilder foreldrepengerAvslagAssertionsBuilder(Fødselsnummer fnr, Saksnummer saksnummer) {
        return alleYtelserFellesAssertionsBuilder(fnr, saksnummer).medEgenndefinertAssertion(
                        "Selv om du ikke har rett til foreldrepenger, kan det være at du har rett til engangsstønad.")
                .medOverskriftOmAvslagAvForeldrepenger()
                .medKapittelDuHarRettTilKlage()
                .medTekstOmVedtaketEtterFolketrygdloven()
                .medParagraf(P_14_7);
    }

    protected static BrevAssertionBuilder foreldrepengerInnvilget80ProsentAssertionsBuilder(Fødselsnummer fnr, Saksnummer saksnummer) {
        return foreldrepengerInnvilgetFellesAssertionBuilder(fnr, saksnummer).medOverskriftOmInnvilget80ProsentForeldrepenger()
                .medEgenndefinertAssertion("Fordi du har valgt 80 prosent foreldrepenger, får du mindre utbetalt i måneden.");
    }

    protected static BrevAssertionBuilder foreldrepengerInnvilget100ProsentAssertionsBuilder(Fødselsnummer fnr,
                                                                                             Saksnummer saksnummer) {
        return foreldrepengerInnvilgetFellesAssertionBuilder(fnr, saksnummer).medOverskriftOmInnvilget100ProsentForeldrepenger();
    }

    private static BrevAssertionBuilder foreldrepengerInnvilgetFellesAssertionBuilder(Fødselsnummer fnr, Saksnummer saksnummer) {
        return alleYtelserFellesAssertionsBuilder(fnr, saksnummer).medTekstOmVedtaketEtterFolketrygdloven()
                .medTekstOmBeregningEtterFolketrygdloven()
                .medParagraf(P_14_7)
                .medTekstOmInntektBruktIBeregningen()
                .medKapittelDuMåMeldeOmEndringer()
                .medKapittelDuHarRettTilKlage();
    }

    protected static BrevAssertionBuilder foreldrepengerInnvilgetEndringAssertionsBuilder(Fødselsnummer fnr, Saksnummer saksnummer) {
        return alleYtelserFellesAssertionsBuilder(fnr, saksnummer)
                .medOverskriftOmInnvilgetEndringAvForeldrepenger()
                .medKapittelDetteHarViInnvilget()
                .medKapittelDuMåMeldeOmEndringer()
                .medKapittelDuHarRettTilKlage()
                .medTekstOmVedtaketEtterFolketrygdloven();
    }

    protected static BrevAssertionBuilder foreldrepengerAnnuleringAssertionsBuilder(Fødselsnummer fnr, Saksnummer saksnummer) {
        return alleYtelserFellesAssertionsBuilder(fnr, saksnummer).medOverskriftOmInnvilgetAnnuleringAvForeldrepenger()
                .medTekstOmDuMåSøkeNyForeldrepengerperiodePå()
                .medTekstOmSøkeSenestDagenFørNyPeriodeEllerBarnetFyllerTreÅr()
                .medEgenndefinertAssertion("Du har valgt å ikke ta ut din tidligere innvilgede periode med foreldrepenger")
                .medEgenndefinertAssertion(
                        "Når det er 4 uker igjen til du skal ta ut foreldrepenger, må arbeidsgiveren din sende inn ny inntektsmelding.")
                .medTekstOmVedtaketEtterFolketrygdloven()
                .medParagraf(P_14_6)
                .medParagraf(P_14_7)
                .medParagraf(P_14_9)
                .medParagraf(P_14_10)
                .medParagraf(P_14_11)
                .medParagraf(P_14_12)
                .medKapittelDuHarRettTilKlage();
    }

    protected static BrevAssertionBuilder svangerskapspengerInnvilgetAssertionsBuilder(Fødselsnummer fnr,
                                                                                       Saksnummer saksnummer,
                                                                                       boolean harRefusjon,
                                                                                       boolean harFlereArbeidsgivere) {
        var brevAssertionsBuilder = alleYtelserFellesAssertionsBuilder(fnr, saksnummer).medOverskriftOmInnvilgettSvangerskapspenger()
                .medTekstOmVedtaketEtterFolketrygdloven()
                .medParagraf(P_14_4)
                .medTekstOmInntektBruktIBeregningen()
                .medKapittelDuMåMeldeOmEndringer()
                .medKapittelDuHarRettTilKlage()
                .medParagraf(P_8_30);

        if (!harRefusjon) {
            brevAssertionsBuilder.medTekstOmAtSvpBlirUtbetaltForAlleDagerOgAtUtbetalingeneKanVariere()
                    .medTekstOmPengenePåKontoDen25HverMåned();
        } else {
            if (harFlereArbeidsgivere) {
                brevAssertionsBuilder.medTekstOmAtViUtbetalerTilFlereArbeidsgiverne()
                        .medEgenndefinertAssertion("I disse periodene får du svangerskapspenger")
                        .medEgenndefinertAssertion(
                                "Arbeidsgiverne dine har gitt oss disse opplysningene. Dette er gjennomsnittet av inntekten din fra "
                                        + "de siste tre månedene. Hvis du nettopp har begynt å arbeide, byttet arbeidsforhold eller lønnen "
                                        + "din har endret seg, har vi brukt månedsinntektene etter at endringen skjedde.");

            } else {
                brevAssertionsBuilder.medTekstOmAtViUtbetalerTilArbeidsgiveren()
                        .medEgenndefinertAssertion("I denne perioden får du svangerskapspenger")
                        .medEgenndefinertAssertion(
                                "Arbeidsgiveren din har gitt oss disse opplysningene. Dette er gjennomsnittet av inntekten din fra "
                                        + "de siste tre månedene. Hvis du nettopp har begynt å arbeide, byttet arbeidsforhold eller lønnen "
                                        + "din har endret seg, har vi brukt månedsinntektene etter at endringen skjedde.");
            }
        }

        return brevAssertionsBuilder;
    }

    protected static BrevAssertionBuilder alleYtelserFellesAssertionsBuilder(Fødselsnummer fnr, Saksnummer saksnummer) {
        return BrevAssertionBuilder.ny()
                .medEgenndefinertAssertion("Fødselsnummer: %s".formatted(BrevFormateringUtils.formaterFnr(fnr.value())))
                .medEgenndefinertAssertion("Saksnummer: %s".formatted(saksnummer.value()))
                .medKapittelDuHarRettTilInnsyn()
                .medKapittelHarDuSpørsmål()
                .medVennligHilsen()
                .medUnderksriftNFP();
    }

    protected static BrevAssertionBuilder inntektsmeldingBrevAssertionsBuilder(String fødselsnummer,
                                                                               LocalDate førsteDagMedYtelsen,
                                                                               Integer månedsInntekt,
                                                                               boolean harRefusjon,
                                                                               TypeYtelse typeYtelse) {
        var brevAssertionsBuilder = BrevAssertionBuilder.ny();
        var månedslønnFormatert = BrevFormateringUtils.formaterKroner(månedsInntekt);
        var refusjon = "Nei";
        if (harRefusjon) {
            refusjon = "Ja";
            brevAssertionsBuilder.medEgenndefinertAssertion("Refusjonsbeløp dere krever per måned%skr".formatted(månedslønnFormatert));
        }
        brevAssertionsBuilder.medEgenndefinertAssertion("Innsendt: %s".formatted(BrevFormateringUtils.formaterDato(LocalDate.now())))
                .medEgenndefinertAssertion("Inntektsmelding %s".formatted(BrevFormateringUtils.ytelseNavn(typeYtelse)))
                .medEgenndefinertAssertion("Arbeidsgiver")
                .medEgenndefinertAssertion("Den ansatte")
                .medEgenndefinertAssertion("f.nr. %s".formatted(BrevFormateringUtils.formaterFnr(fødselsnummer)))
                .medEgenndefinertAssertion("Kontaktperson fra bedriften")
                .medEgenndefinertAssertion("Corpolarsen")
                .medEgenndefinertAssertion("Første dag med %s".formatted(BrevFormateringUtils.ytelseNavn(typeYtelse)))
                .medEgenndefinertAssertion(BrevFormateringUtils.formaterDato(førsteDagMedYtelsen))
                .medEgenndefinertAssertion("Beregnet månedslønn")
                .medEgenndefinertAssertion("%s kr".formatted(månedslønnFormatert))
                .medEgenndefinertAssertion("Utbetaling og refusjon")
                .medEgenndefinertAssertion("Betaler dere den ansatte lønn under fraværet og krever refusjon? %s".formatted(refusjon))
                .medEgenndefinertAssertion("Naturalytelser")
                .medEgenndefinertAssertion("Har den ansatte naturalytelser som faller bort ved fraværet?Nei");
        return brevAssertionsBuilder;
    }

    public enum TypeYtelse {
        FP,
        SVP
    }

}
