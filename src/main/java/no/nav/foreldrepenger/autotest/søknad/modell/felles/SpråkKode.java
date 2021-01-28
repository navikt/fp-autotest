package no.nav.foreldrepenger.autotest.søknad.modell.felles;

public enum SpråkKode {
    NN, NB, EN;

    @SuppressWarnings("SameReturnValue")
    public static SpråkKode defaultSpråk() {
        return NB;
    }
}
