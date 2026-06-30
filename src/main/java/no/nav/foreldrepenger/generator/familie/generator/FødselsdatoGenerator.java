package no.nav.foreldrepenger.generator.familie.generator;

import java.time.LocalDate;
import java.time.Month;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Genererer tilfeldige fødselsdatoer for testpersoner.
 *
 * <p>Datoene spres over et bredt alders- og månedsspenn for å unngå kollisjon
 * i VTP sin FNR-pool, som har begrenset kapasitet per (dato, kjønn)-kombinasjon.
 */
public final class FødselsdatoGenerator {

    private static final int MIN_ALDER = 20;
    private static final int MAX_ALDER = 50;

    private FødselsdatoGenerator() {
        // Statisk verktøyklasse
    }

    public static LocalDate tilfeldig() {
        var rng = ThreadLocalRandom.current();
        int alder = rng.nextInt(MIN_ALDER, MAX_ALDER + 1);
        int måned = rng.nextInt(1, 13);
        int maxDag = Month.of(måned).length(false); // ikke skuddår — enklere og alltid gyldig
        int dag = rng.nextInt(1, maxDag + 1);
        return LocalDate.now().minusYears(alder).withMonth(måned).withDayOfMonth(dag);
    }
}
