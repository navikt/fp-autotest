package no.nav.foreldrepenger.autotest.teknisk.logg;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
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
import no.nav.foreldrepenger.vtp.testmodell.inntektytelse.arbeidsforhold.Arbeidsforhold;
import no.nav.foreldrepenger.vtp.testmodell.inntektytelse.arbeidsforhold.ArbeidsforholdModell;

@Tag("logger")
class LoggTest {
    private static final Logger LOG = LoggerFactory.getLogger(LoggTest.class);

    private static final Pattern IDENT_PATTERN = Pattern.compile("message.*\\d{9}");

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
    private static final List<String> IGNORE_SENSITIV_INFO_FROM = List.of(
            "Dummy MinSideVarsel-producer sender" // Logges lokalt i fpoversikt i en overgangsfase ved lokalt testing av varsler
    );

    private static final List<String> ignoreContainersFeil = List.of("vtp", "audit.nais", "postgres", "oracle", "authserver", "fptilgang", "fager-api", "fpcache");
    private static final List<String> ignoreContainersSensitiveInfo = List.of("vtp", "audit.nais", "postgres", "oracle", "authserver", "fpsoknad-mottak", "foreldrepengesoknad-api", "fager-api", "fpcache");
    private static String IKKE_SJEKK_LENGDE_AV_CONTAINERE;

    private static String toNumericPattern(String s) {
        return "^(.*[^0-9])?" + Pattern.quote(s) + "([^0-9].*)?$";
    }

    private static Stream<String> hentContainerNavn() {
        return Arrays.stream(DockerUtils.hentContainerNavn());
    }

    @BeforeAll
    static void setup() {
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
                    if (IDENT_PATTERN.matcher(currentLine).find()) {
                        for (var sensitiv : sensitiveStrenger) {
                            var inneholderSensistivOpplysning = currentLine.matches(sensitiv.data());
                            var msg = String.format("Fant sensitiv opplysning i logg (syntetisk): [%s] for applikasjon: [%s], linje[%s]=%s, type=%s",
                                    sensitiv.data(), containerNavn, linePos, currentLine, sensitiv.kilde());
                            if (inneholderSensistivOpplysning && IGNORE_SENSITIV_INFO_FROM.stream().noneMatch(currentLine::contains)) {
                                assertEquals("", sensitiv.data(), msg);
                            }
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
                    fail(String.format("Det forventes minst 75 linjer i loggen for applikasjon: %s, men var %s.",
                            containerNavn, linePos));
                }
            }
        }
    }

    private boolean isUnwantedString(String currentLine, String unwantedString) {
        return currentLine.contains(unwantedString) && IGNORE_EXCEPTION_IF_CONTAINS.stream().noneMatch(currentLine::contains);
    }

    private Set<SensitivInformasjon> hentSensitiveStrengerFraVTP() {
        var scenarioKlient = new TestscenarioKlient();
        var testscenarioDtos = scenarioKlient.hentAlleScenarier();
        Set<SensitivInformasjon> sensitiveStrenger = new LinkedHashSet<>();

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
            .map(ArbeidsforholdModell::arbeidsforhold).orElseGet(List::of).stream()
            .map(LoggTest::sensitivForArbeidsforhold)
            .flatMap(Collection::stream)
            .collect(Collectors.toSet());
    }

    private static Set<SensitivInformasjon> sensitivForArbeidsforhold(Arbeidsforhold arbeidsforhold) {
        Set<SensitivInformasjon> sensitiv = new LinkedHashSet<>();
        if (arbeidsforhold.arbeidsgiverOrgnr() != null) {
            sensitiv.add(new SensitivInformasjon("orgnr på arbeidsgiver", toNumericPattern(arbeidsforhold.arbeidsgiverOrgnr())));
        }
        if (arbeidsforhold.arbeidsgiverAktorId() != null) {
            sensitiv.add(new SensitivInformasjon("aktørID på arbeidsgiver", toNumericPattern(arbeidsforhold.arbeidsgiverAktorId())));
        }
        if (arbeidsforhold.personArbeidsgiver() != null && arbeidsforhold.personArbeidsgiver().getAktørIdent() != null) {
            sensitiv.add(new SensitivInformasjon("aktørID på arbeidsgiver", toNumericPattern(arbeidsforhold.personArbeidsgiver().getAktørIdent())));
        }
        if (arbeidsforhold.personArbeidsgiver() != null && arbeidsforhold.personArbeidsgiver().getIdent() != null) {
            sensitiv.add(new SensitivInformasjon("FNR/DNR på arbeidsgiver", toNumericPattern(arbeidsforhold.personArbeidsgiver().getIdent())));
        }
        return sensitiv;
    }

    private record SensitivInformasjon(String kilde, String data) {
    }
}
