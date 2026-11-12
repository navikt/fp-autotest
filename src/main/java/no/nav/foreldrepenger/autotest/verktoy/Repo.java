package no.nav.foreldrepenger.autotest.verktoy;

import java.nio.file.Path;

final class Repo {

    private Repo() {
    }

    static Path rot() {
        try {
            var klasser = Path.of(Cli.class.getProtectionDomain().getCodeSource().getLocation().toURI());
            return klasser.getParent().getParent().toAbsolutePath().normalize();
        } catch (Exception e) {
            return Path.of("").toAbsolutePath();
        }
    }
}
