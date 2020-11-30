package no.nav.foreldrepenger.autotest.søknad.modell.felles;

public enum SpråkKode {
    NN, NB, EN;

    public static SpråkKode defaultSpråk() {
        return NB;
    }
}
