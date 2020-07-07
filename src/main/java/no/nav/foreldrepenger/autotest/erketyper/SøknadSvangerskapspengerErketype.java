package no.nav.foreldrepenger.autotest.erketyper;

import java.time.LocalDate;
import java.util.List;

import no.nav.foreldrepenger.autotest.dokumentgenerator.foreldrepengesoknad.SøkersRolle;
import no.nav.foreldrepenger.autotest.dokumentgenerator.foreldrepengesoknad.builders.SvangerskapspengerBuilder;
import no.nav.vedtak.felles.xml.soeknad.svangerskapspenger.v1.Tilrettelegging;

public class SøknadSvangerskapspengerErketype {
    public static SvangerskapspengerBuilder lagSvangerskapspengerSøknad(String søkerAktørId, SøkersRolle søkersRolle,
            LocalDate termin, List<Tilrettelegging> tilretteleggingListe) {
        return new SvangerskapspengerBuilder(søkerAktørId, søkersRolle, tilretteleggingListe)
                .medTermindato(termin)
                .medMedlemskap(MedlemskapErketyper.medlemskapNorge());
    }
}
