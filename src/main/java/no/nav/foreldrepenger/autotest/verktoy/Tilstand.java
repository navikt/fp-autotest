package no.nav.foreldrepenger.autotest.verktoy;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Function;

import no.nav.foreldrepenger.autotest.klienter.vtp.sikkerhet.azure.SaksbehandlerRolle;
import no.nav.foreldrepenger.generator.familie.Familie;
import no.nav.foreldrepenger.generator.familie.Søker;
import no.nav.foreldrepenger.vtp.kontrakter.person.TilordnetIdentDto;
import no.nav.foreldrepenger.vtp.kontrakter.person.v2.PersonDto;
import no.nav.vedtak.mapper.json.DefaultJsonMapper;

/**
 * Familier og saker lagret i en fil, slik at hvert kall fra kommandolinja ser det forrige kall laget.
 */
final class Tilstand {

    record LagretFamilie(List<PersonDto> parter, List<TilordnetIdentDto> identer, LocalDate fodselsdato, String hendelse, String ytelse) {
        // fodselsdato er datoen for familiehendelsen: fødsel, termin eller omsorgsovertakelse. Navnet er beholdt for eldre tilstandsfiler.
        String hendelseType() {
            return hendelse == null ? "fodsel" : hendelse;
        }

        String ytelseType() {
            return ytelse == null ? "FP" : ytelse;
        }

        Familie familie() {
            return new Familie(parter, identer, SaksbehandlerRolle.SAKSBEHANDLER);
        }
    }

    record LagretSak(String familieId, String forelder, LocalDate startdato, String ytelse) {
        String ytelseType() {
            return ytelse == null ? "FP" : ytelse;
        }

        Søker søker(LagretFamilie familie) {
            var f = familie.familie();
            return forelder.equals("mor") ? f.mor() : f.far();
        }
    }

    record Data(Map<String, LagretFamilie> familier, Map<String, LagretSak> saker) {
    }

    // Fillåsen gjelder mellom prosesser, men to tråder i samme JVM kan ikke låse samme fil, så de køes her først.
    private static final ReentrantLock LÅS_I_JVM = new ReentrantLock();

    private final Path fil;

    Tilstand(Path fil) {
        this.fil = fil;
    }

    static Tilstand standard() {
        var fraMiljø = System.getenv("FP_AUTOTEST_TILSTAND");
        if (fraMiljø != null && !fraMiljø.isBlank()) {
            return new Tilstand(Path.of(fraMiljø));
        }
        // I repoet, ikke i hjemmemappa: sandkassa (cplt) kan bare skrive i repoet, og brukerens terminal må se samme fil.
        return new Tilstand(Repo.rot().resolve("verktoy").resolve(".tilstand.json"));
    }

    private static final String ID_TEGN = "abcdefghjkmnpqrstuvwxyz23456789";
    private static final java.security.SecureRandom TILFELDIG = new java.security.SecureRandom();

    String leggTilFamilie(Familie familie, LocalDate dato, String hendelse, String ytelse) {
        return endre(data -> {
            String id;
            do {
                var tegn = new StringBuilder("F-");
                for (int i = 0; i < 4; i++) {
                    tegn.append(ID_TEGN.charAt(TILFELDIG.nextInt(ID_TEGN.length())));
                }
                id = tegn.toString();
            } while (data.familier().containsKey(id));
            data.familier().put(id, new LagretFamilie(familie.parter(), familie.identer(), dato, hendelse, ytelse));
            return id;
        });
    }

    void slettFamilie(String id) {
        endre(data -> {
            data.familier().remove(id);
            data.saker().values().removeIf(sak -> id.equals(sak.familieId()));
            return null;
        });
    }

    LagretFamilie familie(String id) {
        return Optional.ofNullable(les().familier().get(id))
                .orElseThrow(() -> new IllegalArgumentException("Ukjent familie_id: " + id + " (lagret i " + fil + ")."));
    }

    void leggTilSak(String saksnummer, LagretSak sak) {
        endre(data -> data.saker().put(saksnummer, sak));
    }

    Optional<LagretSak> sak(String saksnummer) {
        return Optional.ofNullable(les().saker().get(saksnummer));
    }

    private <T> T endre(Function<Data, T> endring) {
        LÅS_I_JVM.lock();
        try {
            Files.createDirectories(fil.getParent());
            var låsfil = fil.resolveSibling(fil.getFileName() + ".lock");
            try (var kanal = FileChannel.open(låsfil, StandardOpenOption.CREATE, StandardOpenOption.WRITE);
                 var lås = kanal.lock()) {
                var data = les();
                var resultat = endring.apply(data);
                skriv(data);
                return resultat;
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        } finally {
            LÅS_I_JVM.unlock();
        }
    }

    private Data les() {
        if (!Files.exists(fil)) {
            return new Data(new LinkedHashMap<>(), new LinkedHashMap<>());
        }
        try {
            var data = DefaultJsonMapper.getJsonMapper().readValue(Files.readString(fil), Data.class);
            return new Data(new LinkedHashMap<>(data.familier()), new LinkedHashMap<>(data.saker()));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private void skriv(Data data) {
        try {
            Files.createDirectories(fil.getParent());
            var tmp = Files.createTempFile(fil.getParent(), "tilstand", ".json");
            Files.writeString(tmp, DefaultJsonMapper.toJson(data));
            Files.move(tmp, fil, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
