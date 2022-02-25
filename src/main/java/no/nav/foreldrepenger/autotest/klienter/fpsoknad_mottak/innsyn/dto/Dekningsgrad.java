package no.nav.foreldrepenger.autotest.klienter.fpsoknad_mottak.innsyn.dto;

public enum Dekningsgrad {
    ÅTTI, HUNDRE;

    public static Dekningsgrad valueOf(Integer value) {
        if (value == null) {
            return null;
        }
        if (value == 80) {
            return ÅTTI;
        }
        if (value == 100) {
            return HUNDRE;
        }
        throw new IllegalArgumentException("Ukjent dekningsgrad " + value);
    }
}
