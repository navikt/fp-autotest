package no.nav.foreldrepenger.autotest.util.pdfcapture;

import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.text.PDFTextStripper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.nav.foreldrepenger.autotest.aktoerer.saksbehandler.fpsak.Saksbehandler;
import no.nav.foreldrepenger.kontrakter.felles.typer.Saksnummer;

/**
 * MIDLERTIDIG: Lagrer søknads-PDFer (og tekst) per test for sammenligning av før/etter-format på uttaksplan.
 * Aktiveres med -Dpdfcapture.label=before|after. Skal ikke merges.
 */
public final class PdfInnsamler {
    private static final Logger LOG = LoggerFactory.getLogger(PdfInnsamler.class);
    private static final String LABEL = System.getProperty("pdfcapture.label");
    private static final Path ROT = Path.of(System.getProperty("pdfcapture.dir", "target/pdf-capture"));
    private static final java.util.concurrent.ConcurrentHashMap<String, AtomicInteger> TELLER = new java.util.concurrent.ConcurrentHashMap<>();
    private static final int MAKS_FORSØK = 30;

    private PdfInnsamler() {
    }

    public static boolean aktiv() {
        return LABEL != null && !LABEL.isBlank();
    }

    public static void fangSøknadPdfer(Saksnummer saksnummer) {
        if (!aktiv()) {
            return;
        }
        try {
            var saksbehandler = new Saksbehandler();
            saksbehandler.hentFagsak(saksnummer);
            var lagret = 0;
            for (var forsøk = 0; forsøk < MAKS_FORSØK && lagret == 0; forsøk++) {
                lagret = lagreSøknadDokumenter(saksbehandler, saksnummer);
                if (lagret == 0) {
                    Thread.sleep(1000);
                }
            }
            if (lagret == 0) {
                LOG.warn("pdfcapture: fant ingen søknad-PDF for {} i {}", saksnummer.value(), testId());
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            LOG.warn("pdfcapture: feilet for {}", saksnummer.value(), e);
        }
    }

    private static int lagreSøknadDokumenter(Saksbehandler saksbehandler, Saksnummer saksnummer) throws Exception {
        var antall = 0;
        var dokumenter = saksbehandler.hentHistorikkinnslagPåFagsak().stream()
                .flatMap(innslag -> innslag.dokumenter().stream())
                .filter(d -> d.tag() != null && d.tag().toLowerCase().contains("søknad"))
                .filter(d -> !d.utgått())
                .toList();
        var sett = new java.util.HashSet<String>();
        for (var dok : dokumenter) {
            if (!sett.add(dok.dokumentId())) {
                continue;
            }
            var bytes = saksbehandler.hentJournalførtDokument(dok.dokumentId(), "ARKIV");
            lagre(bytes, dok.tag());
            antall++;
        }
        return antall;
    }

    private static void lagre(byte[] pdf, String tag) throws Exception {
        var id = testId();
        var katalog = ROT.resolve(LABEL).resolve(id);
        Files.createDirectories(katalog);
        var navn = "%02d-%s".formatted(TELLER.computeIfAbsent(id, k -> new AtomicInteger()).incrementAndGet(), tag.replaceAll("[^\\p{L}\\p{N}]+", "_"));
        Files.write(katalog.resolve(navn + ".pdf"), pdf);
        try (var doc = Loader.loadPDF(pdf)) {
            Files.writeString(katalog.resolve(navn + ".txt"), new PDFTextStripper().getText(doc), StandardCharsets.UTF_8);
        }
    }

    /* Finner første stackframe som er en JUnit-testmetode */
    static String testId() {
        for (var frame : Thread.currentThread().getStackTrace()) {
            try {
                var klasse = Class.forName(frame.getClassName(), false, PdfInnsamler.class.getClassLoader());
                var erTest = Arrays.stream(klasse.getDeclaredMethods())
                        .filter(m -> m.getName().equals(frame.getMethodName()))
                        .anyMatch(PdfInnsamler::erTestMetode);
                if (erTest) {
                    return klasse.getSimpleName() + "/" + frame.getMethodName();
                }
            } catch (ClassNotFoundException | LinkageError _) {
                // hopper over
            }
        }
        return "ukjent/" + Thread.currentThread().getName().replaceAll("[^\\w-]", "_");
    }

    private static boolean erTestMetode(Method m) {
        return Arrays.stream(m.getAnnotations()).anyMatch(a -> a.annotationType().getName().startsWith("org.junit.jupiter.api.")
                && a.annotationType().getSimpleName().matches("Test|RepeatedTest|TestFactory|TestTemplate")
                || a.annotationType().getName().equals("org.junit.jupiter.params.ParameterizedTest"));
    }
}
