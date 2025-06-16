package no.nav.foreldrepenger.generator.soknad.maler;

import no.nav.foreldrepenger.generator.familie.Søker;
import no.nav.foreldrepenger.selvbetjening.kontrakt.innsending.dto.foreldrepenger.annenpart.AnnenForelderDto;
import no.nav.foreldrepenger.selvbetjening.kontrakt.innsending.util.builder.AnnenforelderBuilder;

public class AnnenforelderMaler {

    private AnnenforelderMaler() {
    }

    public static AnnenForelderDto norskMedRettighetNorge(Søker søker) {
        return AnnenforelderBuilder.norskMedRettighetNorge(søker.fødselsnummer()).build();
    }

    public static AnnenForelderDto norskIkkeRett(Søker søker) {
        return AnnenforelderBuilder.norskIkkeRett(søker.fødselsnummer()).build();
    }

    public static AnnenForelderDto norskIkkeRettAleneomsorg(Søker søker) {
        return new AnnenforelderBuilder.NorskForelderBuilder(søker.fødselsnummer())
                .medErAleneOmOmsorg(true)
                .medHarRettPåForeldrepenger(false)
                .build();
    }

    public static AnnenForelderDto annenpartIkkeRettOgMorHarUføretrygd(Søker søker) {
        return AnnenforelderBuilder.aleneomsorgAnnenpartIkkeRettOgMorHarUføretrygd(søker.fødselsnummer()).build();
    }
}
