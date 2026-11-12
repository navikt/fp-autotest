package no.nav.foreldrepenger.autotest.verktoy;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

final class Katalog {

    private final Path rot;

    Katalog(Path rot) {
        this.rot = rot;
    }

    static Katalog finn() {
        var fraMiljø = System.getenv("FP_AUTOTEST_KATALOG");
        if (fraMiljø != null && !fraMiljø.isBlank()) {
            return new Katalog(Path.of(fraMiljø));
        }
        return new Katalog(Repo.rot().resolve("katalog"));
    }

    record Oppføring(String kode, String tittel, Path fil) {
    }

    List<Oppføring> aksjonspunkt() {
        var mappe = rot.resolve("aksjonspunkt");
        if (!Files.isDirectory(mappe)) {
            return List.of();
        }
        try (Stream<Path> filer = Files.list(mappe)) {
            return filer.filter(f -> f.toString().endsWith(".md"))
                    .map(Katalog::lesOppføring)
                    .sorted(Comparator.comparing(Oppføring::kode))
                    .toList();
        }
        catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    Optional<Oppføring> finn(String kode) {
        return aksjonspunkt().stream().filter(o -> o.kode().equals(kode)).findFirst();
    }

    private static Oppføring lesOppføring(Path fil) {
        var kode = fil.getFileName().toString().replace(".md", "");
        var tittel = "";
        for (var linje : les(fil).lines().toList()) {
            if (linje.startsWith("kode:")) {
                kode = verdi(linje);
            } else if (linje.startsWith("tittel:")) {
                tittel = verdi(linje);
            }
        }
        return new Oppføring(kode, tittel, fil);
    }

    private static String verdi(String linje) {
        return linje.substring(linje.indexOf(':') + 1).trim().replace("\"", "");
    }

    private static String les(Path fil) {
        try {
            return Files.readString(fil);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
