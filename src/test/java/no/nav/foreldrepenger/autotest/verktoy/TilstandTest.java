package no.nav.foreldrepenger.autotest.verktoy;

import static org.assertj.core.api.Assertions.assertThat;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import io.qameta.allure.Description;

@Tag("internal")
class TilstandTest {

    private static final int PROSESSER = 4;
    private static final int SAKER_PER_PROSESS = 20;

    @Test
    @DisplayName("Samtidige skriv fra flere prosesser mister ingen saker")
    @Description("Fire JVM-er legger til saker i samme tilstandsfil samtidig, slik folg_opp i bakgrunnen og andre kall gjør.")
    void samtidigeSkrivFraFlereProsesserMisterIngenSaker(@TempDir Path mappe) throws Exception {
        var fil = mappe.resolve("tilstand.json");
        var java = Path.of(System.getProperty("java.home"), "bin", "java").toString();
        var prosesser = new ArrayList<Process>();
        for (int p = 0; p < PROSESSER; p++) {
            prosesser.add(new ProcessBuilder(java, "-cp", System.getProperty("java.class.path"), Skriver.class.getName(),
                    fil.toString(), String.valueOf(p))
                    .redirectErrorStream(true)
                    .redirectOutput(mappe.resolve("prosess" + p + ".log").toFile())
                    .start());
        }
        for (var prosess : prosesser) {
            assertThat(prosess.waitFor(60, TimeUnit.SECONDS)).isTrue();
            assertThat(prosess.exitValue()).as(Files.readString(mappe.resolve("prosess" + prosesser.indexOf(prosess) + ".log"))).isZero();
        }

        var tilstand = new Tilstand(fil);
        IntStream.range(0, PROSESSER).forEach(p -> IntStream.range(0, SAKER_PER_PROSESS)
                .forEach(i -> assertThat(tilstand.sak(p + "-" + i)).as("sak " + p + "-" + i).isPresent()));
    }

    @Test
    @DisplayName("Sletting av familie fjerner også sakene dens")
    @Description("Familier som er borte fra VTP slettes, og saker som peker på dem skal ikke bli liggende igjen.")
    void slettingAvFamilieFjernerSakene(@TempDir Path mappe) {
        var tilstand = new Tilstand(mappe.resolve("tilstand.json"));
        tilstand.leggTilSak("1", new Tilstand.LagretSak("F-aaaa", "mor", null, "FP"));
        tilstand.leggTilSak("2", new Tilstand.LagretSak("F-bbbb", "mor", null, "FP"));

        tilstand.slettFamilie("F-aaaa");

        assertThat(tilstand.sak("1")).isEmpty();
        assertThat(tilstand.sak("2")).isPresent();
    }

    static class Skriver {
        public static void main(String[] args) {
            var tilstand = new Tilstand(Path.of(args[0]));
            for (int i = 0; i < SAKER_PER_PROSESS; i++) {
                tilstand.leggTilSak(args[1] + "-" + i, new Tilstand.LagretSak("F1", "mor", null, "FP"));
            }
        }
    }
}
