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
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
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
 * Med {@code auto} sendes inntektsmelding for hver forelder med arbeid som søker ({@code -Dmanuell.soker}),
 * hver for seg så snart forespørselen finnes.
 * Med {@code vent} venter testen på filen {@code target/manuell-soknad/inntektsmelding} med
 * innholdet {@code mor}, {@code far}, {@code begge} eller {@code ingen}, og sender inntektsmelding for de som er valgt.
 * Med {@code -Dmanuell.antall=N} (standard 1) lages N familier i samme kjøring. Hver familie får da sin egen
 * undermappe {@code 01}, {@code 02}, ... med egen {@code familie.txt} og {@code inntektsmelding}, og loggmeldingene
 * sier hvilken familie de gjelder («mor i familie 03»).
 */
@Tag("utvikling")
class ManuellSoknad extends VerdikjedeTestBase {

    // -Dmanuell.mappe gir hver kjøring sin egen mappe, slik at flere kjøringer kan vente på inntektsmelding samtidig
    private static final Path MAPPE = Path.of(System.getProperty("manuell.mappe", "target/manuell-soknad"));
    private static final Duration MAKS_VENTETID = Duration.ofHours(1);

    private final String kjøringId = UUID.randomUUID().toString();

    /**
     * Én familie i kjøringen. {@code id} er null når kjøringen lager én familie: da ligger filene rett i
     * {@link #MAPPE}, og loggmeldingene er de samme som før {@code manuell.antall} fantes.
     */
    private record ManuellFamilie(String id, Path mappe, Søker mor, Søker far) {
        Path familieFil() {
            return mappe.resolve("familie.txt");
        }

        Path imFil() {
            return mappe.resolve("inntektsmelding");
        }

        Path kjøringFil() {
            return mappe.resolve("kjoring");
        }

        Søker søker(String rolle) {
            return "mor".equals(rolle) ? mor : far;
        }

        String etikett(String rolle) {
            return id == null ? rolle : rolle + " i familie " + id;
        }
    }

    @Test
    @DisplayName("Mor og far som fyller ut søknaden selv")
    @Description("Oppretter mor og far i VTP uten søknad. Etter at søknaden er sendt manuelt, sendes inntektsmelding "
            + "for valgte foreldre. Styres med -Dmanuell.familiehendelse=fodsel|termin, -Dmanuell.mor=arbeid|ingen, "
            + "-Dmanuell.far=arbeid|ingen, -Dmanuell.inntektsmelding=auto|vent (standard: ingen inntektsmelding, ingen venting), "
            + "-Dmanuell.soker=mor|far|begge (hvem auto venter på, standard begge), "
            + "-Dmanuell.fodt.uker.siden=<uker> (hvor mange uker siden barnet ble født, standard 2), "
            + "-Dmanuell.antall=<antall familier> (standard 1, flere gir undermappene 01, 02, ...).")
    void morOgFarFyllerUtSøknadSelv() throws IOException, InterruptedException {
        var antall = antall();
        var termin = "termin".equals(System.getProperty("manuell.familiehendelse", "fodsel"));
        var bredde = Math.max(2, String.valueOf(antall).length());
        // Alle familie.txt skrives før første «Familie opprettet, se» i loggen, så den som venter på den linjen,
        // kan lese alle filene med en gang.
        var familier = new ArrayList<ManuellFamilie>();
        for (var i = 1; i <= antall; i++) {
            var id = antall == 1 ? null : String.format("%0" + bredde + "d", i);
            familier.add(lagFamilie(id, id == null ? MAPPE : MAPPE.resolve(id), termin));
        }

        var inntektsmelding = System.getProperty("manuell.inntektsmelding", "ingen");
        if ("auto".equals(inntektsmelding)) {
            var søker = System.getProperty("manuell.soker", "begge");
            if (!søker.matches("mor|far|begge")) {
                throw new IllegalArgumentException("Ugyldig manuell.soker: " + søker);
            }
            var venterPå = new LinkedHashMap<String, Søker>();
            var familieFor = new HashMap<String, ManuellFamilie>();
            for (var familie : familier) {
                var roller = new ArrayList<String>();
                if (!"far".equals(søker) && harArbeid("manuell.mor")) {
                    roller.add("mor");
                }
                if (!"mor".equals(søker) && harArbeid("manuell.far")) {
                    roller.add("far");
                }
                for (var rolle : roller) {
                    venterPå.put(familie.etikett(rolle), familie.søker(rolle));
                    familieFor.put(familie.etikett(rolle), familie);
                }
                LOG.info("Familie opprettet, se {}. Sender inntektsmelding for {} så snart hver søknad er sendt",
                        familie.familieFil().toAbsolutePath(), roller.isEmpty() ? "ingen" : String.join(" og ", roller));
            }
            sendInntektsmeldingNårSakFinnes(venterPå, familieFor);
            return;
        }
        if (!"vent".equals(inntektsmelding)) {
            for (var familie : familier) {
                LOG.info("Familie opprettet, se {}. Sender ikke inntektsmelding", familie.familieFil().toAbsolutePath());
            }
            return;
        }

        for (var familie : familier) {
            LOG.info("Familie opprettet, se {}. Venter på {}", familie.familieFil().toAbsolutePath(),
                    familie.imFil().toAbsolutePath());
        }
        ventPåValgOgSend(familier);
    }

    private static int antall() {
        var verdi = System.getProperty("manuell.antall", "1").trim();
        if (!verdi.matches("[1-9]\\d{0,3}")) {
            throw new IllegalArgumentException("Ugyldig manuell.antall: " + verdi);
        }
        return Integer.parseInt(verdi);
    }

    private ManuellFamilie lagFamilie(String id, Path mappe, boolean termin) throws IOException {
        Files.createDirectories(mappe);
        var tom = new ManuellFamilie(id, mappe, null, null);
        Files.writeString(tom.kjøringFil(), kjøringId);
        Files.deleteIfExists(tom.imFil());
        Files.deleteIfExists(tom.familieFil());

        var generator = FamilieGenerator.ny()
                .forelder(medInntekt(mor(), "manuell.mor"))
                .forelder(medInntekt(far(), "manuell.far"))
                .relasjonForeldre(FamilierelasjonDto.Relasjon.EKTE);
        if (!termin) {
            generator.barn(LocalDate.now().minusWeeks(Long.getLong("manuell.fodt.uker.siden", 2)));
        }
        var familie = generator.build();
        var mor = familie.mor();
        var far = familie.far();

        var hendelse = termin
                ? "termin " + LocalDate.now().plusWeeks(4) + " (forslag, barnet er ikke registrert)"
                : "barn født " + familie.barn().fødselsdato();
        var tmp = mappe.resolve("familie.txt.tmp");
        Files.writeString(tmp, "mor=" + mor.fødselsnummer().value() + "\n"
                + "far=" + far.fødselsnummer().value() + "\n"
                + hendelse + "\n"
                + "søknad: http://localhost:9101\n");
        Files.move(tmp, tom.familieFil(), StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
        return new ManuellFamilie(id, mappe, mor, far);
    }

    private boolean erAvløst(ManuellFamilie familie) throws IOException {
        var fil = familie.kjøringFil();
        if (!Files.exists(fil) || kjøringId.equals(Files.readString(fil).trim())) {
            return false;
        }
        if (familie.id() == null) {
            LOG.info("En ny ManuellSoknad-kjøring har startet, avslutter denne");
        } else {
            LOG.info("En ny ManuellSoknad-kjøring har startet for familie {}, avslutter den", familie.id());
        }
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

    private void ventPåValgOgSend(List<ManuellFamilie> familier) throws IOException, InterruptedException {
        var gjenstår = new ArrayList<>(familier);
        var frist = Instant.now().plus(MAKS_VENTETID);
        while (!gjenstår.isEmpty() && Instant.now().isBefore(frist)) {
            var iterator = gjenstår.iterator();
            while (iterator.hasNext()) {
                var familie = iterator.next();
                if (erAvløst(familie)) {
                    iterator.remove();
                    continue;
                }
                var valg = lesValg(familie);
                if (valg.isEmpty()) {
                    continue;
                }
                iterator.remove();
                if (valg.get().equals("mor") || valg.get().equals("begge")) {
                    sendInntektsmelding(familie, "mor");
                }
                if (valg.get().equals("far") || valg.get().equals("begge")) {
                    sendInntektsmelding(familie, "far");
                }
            }
            if (!gjenstår.isEmpty()) {
                Thread.sleep(2000);
            }
        }
        if (!gjenstår.isEmpty()) {
            throw new IllegalStateException("Fikk ikke svar om inntektsmelding innen " + MAKS_VENTETID);
        }
    }

    private static Optional<String> lesValg(ManuellFamilie familie) throws IOException {
        var fil = familie.imFil();
        if (!Files.exists(fil)) {
            return Optional.empty();
        }
        var valg = Files.readString(fil).trim();
        if (!valg.matches("mor|far|begge|ingen")) {
            throw new IllegalArgumentException("Ugyldig valg i " + fil + ": " + valg);
        }
        return Optional.of(valg);
    }

    private void sendInntektsmeldingNårSakFinnes(Map<String, Søker> søkere, Map<String, ManuellFamilie> familieFor)
            throws IOException, InterruptedException {
        var gjenstår = new LinkedHashMap<>(søkere);
        var frist = Instant.now().plus(MAKS_VENTETID);
        var nesteStatus = Instant.now();
        while (!gjenstår.isEmpty() && Instant.now().isBefore(frist)) {
            var avløste = new HashSet<ManuellFamilie>();
            for (var familie : gjenstår.keySet().stream().map(familieFor::get).distinct().toList()) {
                if (erAvløst(familie)) {
                    avløste.add(familie);
                }
            }
            gjenstår.keySet().removeIf(etikett -> avløste.contains(familieFor.get(etikett)));
            if (gjenstår.isEmpty()) {
                return;
            }
            var iterator = gjenstår.entrySet().iterator();
            while (iterator.hasNext()) {
                var forelder = iterator.next();
                var saksnummer = nyesteSak(forelder.getValue());
                var startdato = saksnummer.flatMap(ManuellSoknad::forespørselStartdato);
                if (startdato.isPresent()) {
                    forelder.getValue().arbeidsgiver().sendInntektsmeldingerFP(saksnummer.get(), startdato.get());
                    LOG.info("Inntektsmelding sendt for {}, saksnummer {}", forelder.getKey(), saksnummer.get().value());
                    iterator.remove();
                }
            }
            if (!gjenstår.isEmpty() && Instant.now().isAfter(nesteStatus)) {
                LOG.info("Venter på søknad og forespørsel for {}", String.join(" og ", gjenstår.keySet()));
                nesteStatus = Instant.now().plus(Duration.ofMinutes(1));
            }
            Thread.sleep(5000);
        }
        if (!gjenstår.isEmpty()) {
            LOG.info("Fant ingen forespørsel innen {} for {}, sender ikke inntektsmelding",
                    MAKS_VENTETID, String.join(" og ", gjenstår.keySet()));
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

    private void sendInntektsmelding(ManuellFamilie familie, String rolle) {
        var søker = familie.søker(rolle);
        nyesteSak(søker).ifPresentOrElse(
                saksnummer -> sendInntektsmelding(familie, rolle, søker, saksnummer),
                () -> LOG.info("Fant ingen sak for {}, sender ikke inntektsmelding. Er søknaden sendt inn?",
                        familie.etikett(rolle)));
    }

    private void sendInntektsmelding(ManuellFamilie familie, String rolle, Søker søker, Saksnummer saksnummer) {
        ventPåInntektsmeldingForespørsel(saksnummer);
        var startdato = InntektsmeldingKlient.hentInntektsmeldingForespørslerFor(saksnummer)
                .inntektmeldingForespørsler()
                .getFirst()
                .startDato();
        søker.arbeidsgiver().sendInntektsmeldingerFP(saksnummer, startdato);
        if (familie.id() == null) {
            LOG.info("Inntektsmelding sendt for saksnummer {}", saksnummer.value());
        } else {
            LOG.info("Inntektsmelding sendt for {}, saksnummer {}", familie.etikett(rolle), saksnummer.value());
        }
    }
}
