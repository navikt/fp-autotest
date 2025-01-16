package no.nav.foreldrepenger.generator.inntektsmelding.builders;

public record Prosent(Double prosent) {
    public static Prosent valueOf(int prosentAndel) {
        return new Prosent((double) prosentAndel);
    }
}
