package no.nav.foreldrepenger.autotest.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public final class DockerUtils {

    private DockerUtils() {}

    public static String hentAuditNaisLogg() {
        return hentLoggForContainer("audit.nais");
    }

    public static String hentLoggForContainer(String containerNavn) {
        try {
            final Process logProcess = new ProcessBuilder(
                "docker",
                "logs",
                containerNavn)
                    .redirectErrorStream(true)
                    .start();

            return outputToString(logProcess);
        } catch (IOException|InterruptedException e) {
            throw new RuntimeException("Feil ved uthenting av logg fra container: " + containerNavn, e);
        }
    }

    public static String[] hentContainerNavn() {
        try {
            final Process containerNavnProcess = new ProcessBuilder(
                "docker",
                "ps",
                "--format", "{{.Names}}")
                    .start();

            return outputToString(containerNavnProcess).split("\n");
        } catch (IOException|InterruptedException e) {
            throw new RuntimeException("Feil ved uthenting av containernavn.", e);
        }
    }

    private static String outputToString(final Process logProcess) throws InterruptedException, IOException {
        try (InputStream inputStream = logProcess.getInputStream()) {
            String result = inputStreamString(inputStream);
            int exitValue = logProcess.waitFor();
            if (exitValue > 0) {
                throw new IllegalStateException("Exit value: " + exitValue + "\nResult: " + result);
            }
            return result;
        }
    }

    private static String inputStreamString(final InputStream is) {
        try {
            try (var in = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
                var sb = new StringBuilder();
                String line;
                while ((line = in.readLine()) != null) {
                    sb.append(line).append("\n");
                }
                return sb.toString();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
