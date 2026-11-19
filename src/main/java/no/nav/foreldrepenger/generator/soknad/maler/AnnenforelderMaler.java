package no.nav.foreldrepenger.generator.soknad.maler;

import no.nav.foreldrepenger.soknad.kontrakt.foreldrepenger.annenpart.AnnenForelderDto;
import no.nav.foreldrepenger.kontrakter.felles.typer.Fødselsnummer;
import no.nav.foreldrepenger.generator.familie.Søker;
import no.nav.foreldrepenger.soknad.kontrakt.builder.AnnenforelderBuilder;

public class AnnenforelderMaler {

    // Rettighetene settes som søknaden gjør: erAleneOmOmsorg er alltid besvart, og harAnnenForelderOppholdtSegIEØS
    // er besvart når annen forelder ikke har rett i Norge.
    private AnnenforelderMaler() {
    }

    public static AnnenForelderDto norskMedRettighetNorge(Søker annenpart) {
        return AnnenforelderBuilder.norskMedRettighetNorge(annenpart.fødselsnummer()).medErAleneOmOmsorg(false).build();
    }

    public static AnnenForelderDto norskIkkeRett(Søker annenpart) {
        return AnnenforelderBuilder.norskIkkeRett(annenpart.fødselsnummer()).medHarAnnenForelderOppholdtSegIEØS(false).build();
    }

    public static AnnenForelderDto norskAleneomsorg(Søker annenpart) {
        return AnnenforelderBuilder.aleneomsorg(annenpart.fødselsnummer())
                .medHarRettPåForeldrepenger(false)
                .build();
    }

    public static AnnenForelderDto utenlandskMedRettighetEØS(String annenpartFnr, String land) {
        return AnnenforelderBuilder.utenlandskForelderRettEØS(new Fødselsnummer(annenpartFnr), land)
                .medHarMorUføretrygd(false) // Fix: utenlandskForelderRettEØS
                .medErAleneOmOmsorg(false)
                .build();
    }

    public static AnnenForelderDto annenpartIkkeRettOgMorHarUføretrygd(Søker søker) {
        return AnnenforelderBuilder.annenpartIkkeRettOgMorHarUføretrygd(søker.fødselsnummer()).medHarAnnenForelderOppholdtSegIEØS(false).build();
    }

    public static AnnenForelderDto ukjentForelder() {
        return AnnenforelderBuilder.ukjentForelder();
    }
}
