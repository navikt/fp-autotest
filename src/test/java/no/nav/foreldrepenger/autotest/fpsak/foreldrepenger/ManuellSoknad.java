package no.nav.foreldrepenger.autotest.fpsak.foreldrepenger;

import static no.nav.foreldrepenger.generator.familie.generator.PersonGenerator.far;
import static no.nav.foreldrepenger.generator.familie.generator.PersonGenerator.mor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import io.qameta.allure.Description;
import no.nav.foreldrepenger.autotest.base.VerdikjedeTestBase;
import no.nav.foreldrepenger.autotest.klienter.fpinntektsmelding.InntektsmeldingKlient;
import no.nav.foreldrepenger.autotest.klienter.fpsak.fagsak.FagsakKlient;
import no.nav.foreldrepenger.autotest.klienter.fpsak.fagsak.dto.Fagsak;
import no.nav.foreldrepenger.autotest.klienter.vtp.sikkerhet.azure.SaksbehandlerRolle;
import no.nav.foreldrepenger.generator.familie.Søker;
import no.nav.foreldrepenger.generator.familie.generator.FamilieGenerator;
import no.nav.foreldrepenger.generator.familie.generator.InntektGenerator;
import no.nav.foreldrepenger.generator.familie.generator.PersonBuilder;
import no.nav.foreldrepenger.kontrakter.felles.typer.Saksnummer;
import no.nav.foreldrepenger.vtp.kontrakter.person.v2.FamilierelasjonDto;
import no.nav.foreldrepenger.vtp.kontrakter.person.v2.PersonDto;

/**
 * Lager mor og far i VTP uten å sende søknad, slik at utvikleren kan fylle ut søknaden selv.
 * Uten {@code -Dmanuell.inntektsmelding} avslutter testen straks familien er laget.
 * Med {@code auto} sendes inntektsmelding for hver forelder med arbeid så snart forespørselen finnes.
 * Med {@code vent} venter testen på filen {@code target/manuell-soknad/inntektsmelding} med
 * innholdet {@code mor}, {@code far}, {@code begge} eller {@code ingen}, og sender inntektsmelding for de som er valgt.
 */
@Tag("utvikling")
class ManuellSoknad extends VerdikjedeTestBase {

    private static final Path MAPPE = Path.of("target", "manuell-soknad");
    private static final Path FAMILIE_FIL = MAPPE.resolve("familie.txt");
    private static final Path IM_FIL = MAPPE.resolve("inntektsmelding");
    private static final Path KJØRING_FIL = MAPPE.resolve("kjoring");
    private static final Duration MAKS_VENTETID = Duration.ofHours(1);

    private final String kjøringId = UUID.randomUUID().toString();

    @Test
    @DisplayName("Mor og far som fyller ut søknaden selv")
    @Description("Oppretter mor og far i VTP uten søknad. Etter at søknaden er sendt manuelt, sendes inntektsmelding "
            + "for valgte foreldre. Styres med -Dmanuell.familiehendelse=fodsel|termin, -Dmanuell.mor=arbeid|ingen, "
            + "-Dmanuell.far=arbeid|ingen, -Dmanuell.inntektsmelding=auto|vent (standard: ingen inntektsmelding, ingen venting).")
    void morOgFarFyllerUtSøknadSelv() throws IOException, InterruptedException {
        Files.createDirectories(MAPPE);
        Files.writeString(KJØRING_FIL, kjøringId);
        Files.deleteIfExists(IM_FIL);
        Files.deleteIfExists(FAMILIE_FIL);

        var termin = "termin".equals(System.getProperty("manuell.familiehendelse", "fodsel"));
        var generator = FamilieGenerator.ny()
                .forelder(medInntekt(mor(), "manuell.mor"))
                .forelder(medInntekt(far(), "manuell.far"))
                .relasjonForeldre(FamilierelasjonDto.Relasjon.EKTE);
        if (!termin) {
            generator.barn(LocalDate.now().minusWeeks(2));
        }
        var familie = generator.build();
        var mor = familie.mor();
        var far = familie.far();

        var hendelse = termin
                ? "termin " + LocalDate.now().plusWeeks(4) + " (forslag, barnet er ikke registrert)"
                : "barn født " + familie.barn().fødselsdato();
        var tmp = MAPPE.resolve("familie.txt.tmp");
        Files.writeString(tmp, "mor=" + mor.fødselsnummer().value() + "\n"
                + "far=" + far.fødselsnummer().value() + "\n"
                + hendelse + "\n"
                + "søknad: http://localhost:9101\n");
        Files.move(tmp, FAMILIE_FIL, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
        var inntektsmelding = System.getProperty("manuell.inntektsmelding", "ingen");
        if ("auto".equals(inntektsmelding)) {
            var medArbeid = new ArrayList<Søker>();
            if (harArbeid("manuell.mor")) {
                medArbeid.add(mor);
            }
            if (harArbeid("manuell.far")) {
                medArbeid.add(far);
            }
            LOG.info("Familie opprettet, se {}. Sender inntektsmelding automatisk når søknaden er sendt",
                    FAMILIE_FIL.toAbsolutePath());
            sendInntektsmeldingNårSakFinnes(medArbeid);
            return;
        }
        if (!"vent".equals(inntektsmelding)) {
            LOG.info("Familie opprettet, se {}. Sender ikke inntektsmelding", FAMILIE_FIL.toAbsolutePath());
            return;
        }

        LOG.info("Familie opprettet, se {}. Venter på {}", FAMILIE_FIL.toAbsolutePath(), IM_FIL.toAbsolutePath());
        var valg = ventPåValg();
        if (valg.isEmpty()) {
            return;
        }
        if (valg.get().equals("mor") || valg.get().equals("begge")) {
            sendInntektsmelding(mor);
        }
        if (valg.get().equals("far") || valg.get().equals("begge")) {
            sendInntektsmelding(far);
        }
    }

    private boolean erAvløst() throws IOException {
        if (!Files.exists(KJØRING_FIL) || kjøringId.equals(Files.readString(KJØRING_FIL).trim())) {
            return false;
        }
        LOG.info("En ny ManuellSoknad-kjøring har startet, avslutter denne");
        return true;
    }

    private static boolean harArbeid(String property) {
        return "arbeid".equals(System.getProperty(property, "arbeid"));
    }

    private static PersonDto medInntekt(PersonBuilder person, String property) {
        if (harArbeid(property)) {
            person.inntekt(InntektGenerator.ny().arbeidMedOpptjeningUnder6G().build());
        }
        return person.build();
    }

    private Optional<String> ventPåValg() throws IOException, InterruptedException {
        var frist = Instant.now().plus(MAKS_VENTETID);
        while (Instant.now().isBefore(frist)) {
            if (erAvløst()) {
                return Optional.empty();
            }
            if (Files.exists(IM_FIL)) {
                var valg = Files.readString(IM_FIL).trim();
                if (!valg.matches("mor|far|begge|ingen")) {
                    throw new IllegalArgumentException("Ugyldig valg i " + IM_FIL + ": " + valg);
                }
                return Optional.of(valg);
            }
            Thread.sleep(2000);
        }
        throw new IllegalStateException("Fikk ikke svar om inntektsmelding innen " + MAKS_VENTETID);
    }

    private void sendInntektsmeldingNårSakFinnes(List<Søker> søkere) throws IOException, InterruptedException {
        var gjenstår = new ArrayList<>(søkere);
        var frist = Instant.now().plus(MAKS_VENTETID);
        while (!gjenstår.isEmpty() && Instant.now().isBefore(frist)) {
            if (erAvløst()) {
                return;
            }
            var iterator = gjenstår.iterator();
            while (iterator.hasNext()) {
                var søker = iterator.next();
                var saksnummer = nyesteSak(søker);
                var startdato = saksnummer.flatMap(ManuellSoknad::forespørselStartdato);
                if (startdato.isPresent()) {
                    søker.arbeidsgiver().sendInntektsmeldingerFP(saksnummer.get(), startdato.get());
                    LOG.info("Inntektsmelding sendt for saksnummer {}", saksnummer.get().value());
                    iterator.remove();
                }
            }
            Thread.sleep(5000);
        }
        if (!gjenstår.isEmpty()) {
            LOG.info("Fant ingen forespørsel innen {} for {} forelder(e), sender ikke inntektsmelding for dem",
                    MAKS_VENTETID, gjenstår.size());
        }
    }

    private static Optional<Saksnummer> nyesteSak(Søker søker) {
        return new FagsakKlient(SaksbehandlerRolle.SAKSBEHANDLER).søk(søker.fødselsnummer())
                .stream()
                .max(Comparator.comparingInt(f -> Integer.parseInt(f.saksnummer().value())))
                .map(Fagsak::saksnummer);
    }

    private static Optional<LocalDate> forespørselStartdato(Saksnummer saksnummer) {
        try {
            var svar = InntektsmeldingKlient.hentInntektsmeldingForespørslerFor(saksnummer);
            if (svar == null || svar.inntektmeldingForespørsler() == null) {
                return Optional.empty();
            }
            return svar.inntektmeldingForespørsler().stream().findFirst().map(f -> f.startDato());
        } catch (RuntimeException e) {
            // Feil her tolkes som at forespørselen ikke finnes ennå; løkken prøver igjen
            return Optional.empty();
        }
    }

    private void sendInntektsmelding(Søker søker) {
        var saksnummer = nyesteSak(søker)
                .orElseThrow(() -> new IllegalStateException("Fant ingen sak. Er søknaden sendt inn?"));
        sendInntektsmelding(søker, saksnummer);
    }

    private void sendInntektsmelding(Søker søker, Saksnummer saksnummer) {
        ventPåInntektsmeldingForespørsel(saksnummer);
        var startdato = InntektsmeldingKlient.hentInntektsmeldingForespørslerFor(saksnummer)
                .inntektmeldingForespørsler()
                .getFirst()
                .startDato();
        søker.arbeidsgiver().sendInntektsmeldingerFP(saksnummer, startdato);
        LOG.info("Inntektsmelding sendt for saksnummer {}", saksnummer.value());
    }
}
