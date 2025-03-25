package no.nav.foreldrepenger.autotest.base;

import static java.time.format.DateTimeFormatter.ofPattern;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.LocalDate;
import java.util.Locale;
import java.util.Set;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.text.PDFTextStripper;
import org.assertj.core.api.Fail;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.BeforeEach;

import no.nav.foreldrepenger.autotest.aktoerer.saksbehandler.fpsak.Beslutter;
import no.nav.foreldrepenger.autotest.aktoerer.saksbehandler.fpsak.Klagebehandler;
import no.nav.foreldrepenger.autotest.aktoerer.saksbehandler.fpsak.Oppgavestyrer;
import no.nav.foreldrepenger.autotest.aktoerer.saksbehandler.fpsak.Overstyrer;
import no.nav.foreldrepenger.autotest.aktoerer.saksbehandler.fpsak.Saksbehandler;
import no.nav.foreldrepenger.autotest.klienter.fpsak.historikk.dto.DokumentTag;
import no.nav.foreldrepenger.autotest.klienter.fpsak.historikk.dto.HistorikkType;
import no.nav.foreldrepenger.autotest.util.log.LoggFormater;
import no.nav.foreldrepenger.autotest.util.pdf.Pdf;
import no.nav.foreldrepenger.common.domain.Fødselsnummer;

// TODO: Fiks opp i testbasene
public abstract class FpsakTestBase {

    /*
     * Aktører
     */
    protected Saksbehandler saksbehandler;
    protected Overstyrer overstyrer;
    protected Beslutter beslutter;
    protected Klagebehandler klagebehandler;
    protected Oppgavestyrer oppgavestyrer;
    // TODO: Drifter


    @BeforeEach
    public void setUp() {
        saksbehandler = new Saksbehandler();
        overstyrer = new Overstyrer();
        beslutter = new Beslutter();
        klagebehandler = new Klagebehandler();
        oppgavestyrer = new Oppgavestyrer();
        LoggFormater.leggTilKjørendeTestCaseILogger();
    }

    protected void hentBrevOgSjekkAtInnholdetErRiktig(BrevAssertionBuilder assertionBuilder,
                                                      Fødselsnummer fnr,
                                                      DokumentTag dokumentTag,
                                                      HistorikkType ventTilHistorikkinnslag) {
        var behandler = saksbehandler;
        if (DokumentTag.KLAGE_OMGJØRIN.equals(dokumentTag)) {
            behandler = klagebehandler;
        }
        behandler.ventTilHistorikkinnslag(ventTilHistorikkinnslag);
        var dokumentId = behandler.hentHistorikkinnslagAvType(ventTilHistorikkinnslag, dokumentTag)
                .dokumenter()
                .getFirst()
                .dokumentId();
        var pdf = behandler.hentJournalførtDokument(dokumentId, "ARKIV");
        assertThat(Pdf.is_pdf(pdf)).as("Sjekker om byte array er av typen PDF").isTrue();

        if (!DokumentTag.INNTEKSTMELDING.equals(dokumentTag)) {
            assertionBuilder.medEgenndefinertAssertion("Fødselsnummer: %s".formatted(formaterFnr(fnr)))
                    .medEgenndefinertAssertion("Saksnummer: %s".formatted(behandler.valgtFagsak.saksnummer().value()))
                    .medKapittelDuHarRettTilInnsyn()
                    .medKapittelHarDuSpørsmål()
                    .medVennligHilsen()
                    .medUnderksriftNFP();
        }

        sjekkAtBrevetInneholderTekst(pdf, assertionBuilder.build());
    }

    private static void sjekkAtBrevetInneholderTekst(byte[] pdfBytes, Set<String> asserts) {
        try (var document = Loader.loadPDF(pdfBytes)) {
            // Fjerne linjeskift for å kunne søke på tvers av linjer
            var pdfTekst = new PDFTextStripper().getText(document).replace("\n", "");
            var softAssert = new SoftAssertions();
            asserts.forEach(skalFinnes -> softAssert
                    .assertThat(pdfTekst.contains(skalFinnes))
                    .as("'%s' finnes ikke".formatted(skalFinnes))
                    .isTrue());
            softAssert.assertAll();
        } catch (IOException e) {
            Fail.fail("Kunne ikke lese PDFen", e);
        }
    }

    protected static String formaterFnr(Fødselsnummer fnr) {
        return fnr.value().substring(0, 6) + " " + fnr.value().substring(6);
    }

    protected static String formaterKroner(int beløp) {
        var symbols = new DecimalFormatSymbols();
        symbols.setGroupingSeparator(' '); // Explicitly setting a normal space (U+0020)
        return new DecimalFormat("#,###", symbols).format(beløp);
    }

    protected static String formaterDato(LocalDate dato) {
        if (dato == null) {
            return null;
        }
        return dato.format(ofPattern("d. MMMM yyyy", Locale.forLanguageTag("NO")));
    }

    protected static BrevAssertionBuilder engangsstønadInnvilgetAssertionsBuilder() {
        return BrevAssertionBuilder.ny()
                .medOverskriftOmInnvilgetEnagangsstønad()
                .medKapittelDuHarRettTilKlage()
                .medTekstOmVedtaketEtterFolketrygdloven()
                .medParagraf_14_17();
    }

    protected static BrevAssertionBuilder foreldrepengerAvslagAssertionsBuilder() {
        return BrevAssertionBuilder.ny()
                .medOverskriftOmAvslagAvForeldrepenger()
                .medKapittelDuHarRettTilKlage()
                .medTekstOmVedtaketEtterFolketrygdloven()
                .medParagraf_14_7();
    }

    protected static BrevAssertionBuilder foreldrepengerInnvilget80ProsentAssertionsBuilder() {
        return foreldrepengerInnvilgetFellesAssertionBuilder()
                .medOverskriftOmInnvilget80ProsentForeldrepenger()
                .medEgenndefinertAssertion("Fordi du har valgt 80 prosent foreldrepenger, får du mindre utbetalt i måneden.");
    }

    protected static BrevAssertionBuilder foreldrepengerInnvilget100ProsentAssertionsBuilder() {
        return foreldrepengerInnvilgetFellesAssertionBuilder().medOverskriftOmInnvilget100ProsentForeldrepenger();
    }

    private static BrevAssertionBuilder foreldrepengerInnvilgetFellesAssertionBuilder() {
        return BrevAssertionBuilder.ny()
                .medTekstOmVedtaketEtterFolketrygdloven()
                .medTekstOmBeregningEtterFolketrygdloven()
                .medParagraf_14_7()
                .medTekstOmInntektBruktIBeregningen()
                .medKapittelDuMåMeldeOmEndringer()
                .medKapittelDuHarRettTilKlage();
    }

    protected static BrevAssertionBuilder foreldrepengerInnvilgetEndringAssertionsBuilder() {
        return BrevAssertionBuilder.ny()
                .medOverskriftOmInnvilgetEndringAvForeldrepenger()
                .medKapittelDetteHarViInnvilget()
                .medKapittelDuMåMeldeOmEndringer()
                .medKapittelDuHarRettTilKlage()
                .medTekstOmVedtaketEtterFolketrygdloven();
    }

    protected static BrevAssertionBuilder foreldrepengerAnnuleringAssertionsBuilder() {
        return BrevAssertionBuilder.ny()
                .medOverskriftOmInnvilgetAnnuleringAvForeldrepenger()
                .medTekstOmDuMåSøkeNyForeldrepengerperiodePå()
                .medTekstOmSøkeSenestDagenFørNyPeriodeEllerBarnetFyllerTreÅr()
                .medEgenndefinertAssertion("Du har valgt å ikke ta ut din tidligere innvilgede periode med foreldrepenger")
                .medEgenndefinertAssertion(
                        "Når det er 4 uker igjen til du skal ta ut foreldrepenger, må arbeidsgiveren din sende inn ny inntektsmelding.")
                .medTekstOmVedtaketEtterFolketrygdloven()
                .medParagraf_14_6()
                .medParagraf_14_7()
                .medParagraf_14_9()
                .medParagraf_14_10()
                .medParagraf_14_11()
                .medParagraf_14_12()
                .medKapittelDuHarRettTilKlage();
    }
}
