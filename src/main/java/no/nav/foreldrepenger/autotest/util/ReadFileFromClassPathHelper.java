package no.nav.foreldrepenger.autotest.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Objects;
import java.util.stream.Collectors;

public final class ReadFileFromClassPathHelper {
    private ReadFileFromClassPathHelper() {
    }

    public static String hent(String filsti) {
        Objects.requireNonNull(filsti, "Må oppgi en filsti det skal hentes fra");

        try(var inputStream = ReadFileFromClassPathHelper.class.getClassLoader().getResourceAsStream(filsti)) {
            if (inputStream == null) {
                throw new IllegalArgumentException("Finner ikke fil på classpath '" + filsti + "'.");
            }
            return new BufferedReader(new InputStreamReader(inputStream))
                    .lines().collect(Collectors.joining("\n"));

        } catch (Exception e) {
            throw new IllegalArgumentException("Feil ved lesing av fil '" + filsti + "'", e);
        }
    }
}
