package no.nav.foreldrepenger.autotest.søknad.erketyper;

import java.time.LocalDate;
import java.util.List;

import no.nav.foreldrepenger.autotest.søknad.builder.SvangerskapspengerBuilder;
import no.nav.foreldrepenger.autotest.søknad.modell.BrukerRolle;
import no.nav.foreldrepenger.autotest.søknad.modell.svangerskapspenger.tilrettelegging.Tilrettelegging;

public class SøknadSvangerskapspengerErketyper {

    public static SvangerskapspengerBuilder lagSvangerskapspengerSøknad(BrukerRolle brukerRolle,
                                                                        LocalDate termin,
                                                                        List<Tilrettelegging> tilretteleggingListe) {
        return new SvangerskapspengerBuilder(brukerRolle, tilretteleggingListe)
                .medTermindato(termin)
                .medMedlemsskap(MedlemsskapErketyper.medlemsskapNorge());
    }
}
