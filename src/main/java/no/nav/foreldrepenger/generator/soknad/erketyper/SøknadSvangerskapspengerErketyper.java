package no.nav.foreldrepenger.generator.soknad.erketyper;

import java.time.LocalDate;
import java.util.List;

import no.nav.foreldrepenger.common.domain.BrukerRolle;
import no.nav.foreldrepenger.common.domain.svangerskapspenger.tilrettelegging.Tilrettelegging;
import no.nav.foreldrepenger.generator.soknad.builder.SvangerskapspengerBuilder;

public final class SøknadSvangerskapspengerErketyper {

    private SøknadSvangerskapspengerErketyper() {
    }

    public static SvangerskapspengerBuilder lagSvangerskapspengerSøknad(BrukerRolle brukerRolle,
                                                                        LocalDate termin,
                                                                        List<Tilrettelegging> tilretteleggingListe) {
        return new SvangerskapspengerBuilder(brukerRolle, tilretteleggingListe)
                .medTermindato(termin)
                .medMedlemsskap(MedlemsskapErketyper.medlemsskapNorge());
    }
}
