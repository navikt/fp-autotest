package no.nav.foreldrepenger.autotest.søknad.erketyper;


import java.time.LocalDate;

import no.nav.foreldrepenger.autotest.søknad.modell.foreldrepenger.Rettigheter;

public final class RettigheterErketyper {

    private RettigheterErketyper() {
    }

    public static Rettigheter beggeForeldreRettIkkeAleneomsorg() {
        return new Rettigheter(
                true,
                true,
                false,
                null);
    }

    // NB! Default verdi hvis annenpart er "ukjent foreldre"!
    public static Rettigheter harAleneOmsorgOgEnerett() {
        return new Rettigheter(
                false,
                true,
                true,
                LocalDate.now()); // Finn ut hva som skal stå på "datoForAleneomsorg"
    }

    public static Rettigheter harIkkeAleneomsorgOgAnnenpartIkkeRett() {
        return new Rettigheter(
                false,
                true,
                false,
                null);
    }
}
