package no.nav.foreldrepenger.autotest.base;

import no.nav.foreldrepenger.autotest.aktoerer.saksbehandler.fpsak.Oppgavestyrer;

import no.nav.foreldrepenger.autotest.klienter.fpsak.historikk.dto.DokumentTag;
import no.nav.foreldrepenger.autotest.klienter.fpsak.historikk.dto.HistorikkType;
import no.nav.foreldrepenger.autotest.util.pdf.Pdf;

import no.nav.foreldrepenger.common.domain.Fødselsnummer;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.text.PDFTextStripper;
import org.assertj.core.api.Fail;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.BeforeEach;

import no.nav.foreldrepenger.autotest.aktoerer.saksbehandler.fpsak.Beslutter;
import no.nav.foreldrepenger.autotest.aktoerer.saksbehandler.fpsak.Klagebehandler;
import no.nav.foreldrepenger.autotest.aktoerer.saksbehandler.fpsak.Overstyrer;
import no.nav.foreldrepenger.autotest.aktoerer.saksbehandler.fpsak.Saksbehandler;
import no.nav.foreldrepenger.autotest.util.log.LoggFormater;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

// TODO: Fiks opp i testbasene
public abstract class FpsakTestBase {

    protected static final String OVERSKRIFT_ENGANGSSTØNAD_INNVILGET = "Nav har innvilget søknaden din om engangsstønad";

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

    protected void hentBrevOgSjekkAtInnholdetErRiktig(Map<String, String> ekstraTekstAssertions, Fødselsnummer fnr, DokumentTag dokumentTag) {
        var dokumentId = saksbehandler.hentHistorikkinnslagAvType(HistorikkType.BREV_SENDT, dokumentTag).dokumenter().getFirst().dokumentId();
        var pdf = saksbehandler.hentJournalførtDokument(dokumentId, "ARKIV");
        assertThat(Pdf.is_pdf(pdf)).as("Sjekker om byte array er av typen PDF").isTrue();

        var brevAssertions = new HashMap<String, String>();
        brevAssertions.put("Sjekk om riktig fødselsnummer i brevet.", fnr.value().substring(0, 6) + " " + fnr.value().substring(6));
        brevAssertions.put("Sjekk om riktig hilsen er brukt.", "Nav Familie- og pensjonsytelser");
        brevAssertions.put("Sjekk rett til klage.", "Du har rett til å klage");
        brevAssertions.put("Sjekk rett til innsyn.", "Du har rett til innsyn");
        brevAssertions.put("Sjekk om spørsmål paragraf finnes.", "Har du spørsmål?");

        brevAssertions.putAll(ekstraTekstAssertions);

        sjekkAtBrevetInneholderTekst(pdf, brevAssertions);
    }

    protected static void sjekkAtBrevetInneholderTekst(byte[] pdfBytes, Map<String, String> asserts) {
        try (var document = Loader.loadPDF(pdfBytes)) {
            var text = new PDFTextStripper().getText(document);
            var softAssertions = new SoftAssertions();
            asserts.forEach((assertionTekst, tekst) -> softAssertions.assertThat(text.contains(tekst)).as(assertionTekst).isTrue());
            softAssertions.assertAll();
        } catch (IOException e) {
            Fail.fail("Kunne ikke lese PDFen", e);
        }
    }

    protected static Map<String, String> engangsstønadStandardBrevAssertions() {
        return Map.of("Sjekk om riktig paragraf er brukt.", "Vedtaket er gjort etter folketrygdloven § 14-17.");
    }

}
