package no.nav.foreldrepenger.autotest.teknisk.logg;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Scanner;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.qameta.allure.Description;
import jakarta.validation.ConstraintViolationException;
import no.nav.foreldrepenger.autotest.klienter.vtp.testscenario.TestscenarioKlient;
import no.nav.foreldrepenger.autotest.util.DockerUtils;
import no.nav.foreldrepenger.vtp.kontrakter.TestscenarioDto;
import no.nav.foreldrepenger.vtp.testmodell.inntektytelse.InntektYtelseModell;
import no.nav.foreldrepenger.vtp.testmodell.inntektytelse.arbeidsforhold.ArbeidsforholdModell;

@Tag("logger")
class LoggTest {
    private static final Logger LOG = LoggerFactory.getLogger(LoggTest.class);

    private static final List<String> UNWANTED_STRINGS = List.of(
        "Server Error",
        "deadlock detected",
        "Vil ikke prøve igjen",
        NullPointerException.class.getSimpleName(),
        IllegalArgumentException.class.getSimpleName(),
        IllegalStateException.class.getSimpleName(),
        UnsupportedOperationException.class.getSimpleName(),
        ArrayIndexOutOfBoundsException.class.getSimpleName(),
        NoSuchElementException.class.getSimpleName(),
        SQLException.class.getSimpleName(),
        ConstraintViolationException.class.getSimpleName(),
        IOException.class.getSimpleName(),
        "javax.persistence.PersistenceException", "jakarta.persistence.PersistenceException");

    private static final List<String> IGNORE_EXCEPTION_IF_CONTAINS = List.of(
            "taskName=behandlingskontroll.tilbakeTilStart",
            "Error while loading kafka-streams-version.properties",
            "Vil automatisk prøve igjen",
            "Unable to acquire JDBC",
            "Kan ikke etterlyse inntektsmeldinger når ingen innteksmeldinger mangler", // Logges av formidling pga timingproblemer i verdikjede
            "FP-018669:Feil ved kall til Abakus: Kunne ikke hente grunnlag fra abakus", // Logges i fpsak som konsekvens av det nedenfor
            "duplicate key value violates unique constraint \\\"uidx_kobling_1\\\"" // Logges i fpabakus
    );

    private static final List<String> ignoreContainersFeil = List.of("vtp", "audit.nais", "postgres", "oracle", "authserver", "fptilgang", "fpkalkulus", "fager-api", "valkey");
    private static final List<String> ignoreContainersSensitiveInfo = List.of("vtp", "audit.nais", "postgres", "oracle", "authserver", "fpsoknad-mottak", "foreldrepengesoknad-api", "fpkalkulus", "fager-api", "valkey");
    private static String IKKE_SJEKK_LENGDE_AV_CONTAINERE;

    private static String toNumericPattern(String s) {
        return "^(.*[^0-9])?" + Pattern.quote(s) + "([^0-9].*)?$";
    }

    private static Stream<String> hentContainerNavn() {
        return Arrays.stream(DockerUtils.hentContainerNavn());
    }

    @BeforeAll
    public static void setup() {
        IKKE_SJEKK_LENGDE_AV_CONTAINERE = Optional.ofNullable(System.getProperty("ikkeSjekkLengdeAvContainer"))
                .or(() -> Optional.ofNullable(System.getenv("ikkeSjekkLengdeAvContainer")))
                .orElse("sjekker alle");
        LOG.info("Sjekker ikke lengden av følgende containere: {}", IKKE_SJEKK_LENGDE_AV_CONTAINERE);
    }

    @DisplayName("Test om lekker sensitive opplysninger")
    @Description("Test om lekker sensitive opplysninger")
    @ParameterizedTest(name = "Sjekk sensitiv logg lekkasje[{index}] {arguments}")
    @MethodSource("hentContainerNavn")
    void sjekkLoggerForPersonopplysninger(String containerNavn) {
        if (!ignoreContainersSensitiveInfo.contains(containerNavn)) {
            var sensitiveStrenger = hentSensitiveStrengerFraVTP(); // Hentes i test for å fungere med Allure
            var log = DockerUtils.hentLoggForContainer(containerNavn);
            try (var scanner = new Scanner(log)) {
                int linePos = 0;
                while (scanner.hasNextLine()) {
                    var currentLine = scanner.nextLine();
                    linePos++;
                    for (var sensitiv : sensitiveStrenger) {
                        var inneholderSensistivOpplysning = currentLine.matches(sensitiv.getData());
                        var msg = String.format("Fant sensitiv opplysning i logg (syntetisk): [%s] for applikasjon: [%s], linje[%s]=%s, type=%s",
                                sensitiv.getData(), containerNavn, linePos, currentLine, sensitiv.getKilde());
                        if (inneholderSensistivOpplysning) {
                            assertEquals("", sensitiv.getData(), msg);
                        }
                    }
                }
            }
        }
    }

    @Tag("loggerFeil")
    @DisplayName("Test om logger kritiske feil")
    @Description("Test om logger kritiske feil")
    @ParameterizedTest(name = "Sjekk Feil i Logger[{index}] {arguments}")
    @MethodSource("hentContainerNavn")
    void sjekkFeilILogger(String containerNavn) {
        if (!ignoreContainersFeil.contains(containerNavn)) {

            var log = DockerUtils.hentLoggForContainer(containerNavn);
            try (var scanner = new Scanner(log)) {
                int linePos = 0;
                while (scanner.hasNextLine()) {
                    var currentLine = scanner.nextLine();
                    linePos++;
                    for (var unwantedString : UNWANTED_STRINGS) {
                        assertFalse(isUnwantedString(currentLine, unwantedString),
                                String.format("Fant feil i logg : [%s] for applikasjon: [%s], linje[%s]=%s", unwantedString, containerNavn, linePos, currentLine));
                    }
                }

                if (!IKKE_SJEKK_LENGDE_AV_CONTAINERE.contains(containerNavn) && linePos < 75) {
                    fail(String.format("Det forventes minst 75 linjer i loggen for applijasjon: %s, men var %s.",
                            containerNavn, linePos));
                }
            }
        }
    }

    private boolean isUnwantedString(String currentLine, String unwantedString) {
        return currentLine.contains(unwantedString) && IGNORE_EXCEPTION_IF_CONTAINS.stream().noneMatch(currentLine::contains);
    }

    private List<SensitivInformasjon> hentSensitiveStrengerFraVTP() {
        var scenarioKlient = new TestscenarioKlient();
        var testscenarioDtos = scenarioKlient.hentAlleScenarier();
        List<SensitivInformasjon> sensitiveStrenger = new ArrayList<>();

        testscenarioDtos.forEach(testscenarioDto -> {
            sensitiveStrenger.add(new SensitivInformasjon("FNR/DNR på person", toNumericPattern(testscenarioDto.personopplysninger().søkerIdent())));
            sensitiveStrenger.add(new SensitivInformasjon("aktørID på person", toNumericPattern(testscenarioDto.personopplysninger().søkerAktørIdent())));
            if (testscenarioDto.personopplysninger().annenpartIdent() != null) {
                sensitiveStrenger.add(new SensitivInformasjon("FNR/DNR på annenPart", toNumericPattern(testscenarioDto.personopplysninger().annenpartIdent())));
                sensitiveStrenger.add(new SensitivInformasjon("aktørID på annenPart", toNumericPattern(testscenarioDto.personopplysninger().annenpartAktørIdent())));
            }

            final Set<SensitivInformasjon> arbeidsgivere = sensitivArbeidsgiverInformasjon(testscenarioDto);
            sensitiveStrenger.addAll(arbeidsgivere);
        });
        return sensitiveStrenger;
    }

    private Set<SensitivInformasjon> sensitivArbeidsgiverInformasjon(TestscenarioDto testscenarioDto) {
        return Optional.ofNullable(testscenarioDto.scenariodata())
            .map(InntektYtelseModell::arbeidsforholdModell)
            .map(ArbeidsforholdModell::arbeidsforhold)
            .map(o -> o.stream()
                .map(f -> f.arbeidsgiverAktorId() != null
                    ? new SensitivInformasjon("aktørId på arbeidsgiver", toNumericPattern(f.arbeidsgiverAktorId()))
                    : new SensitivInformasjon("orgnr på arbeidsgiver", toNumericPattern(f.arbeidsgiverOrgnr())))
                .collect(Collectors.toSet()))
            .orElse(Collections.emptySet());
    }

    private static class SensitivInformasjon {
        private final String kilde;
        private final String data;

        private SensitivInformasjon(String kilde, String data) {
            this.kilde = kilde;
            this.data = data;
        }

        public String getKilde() {
            return kilde;
        }

        public String getData() {
            return data;
        }
    }
}
