package no.nav.foreldrepenger.generator.soknad.maler;

import no.nav.foreldrepenger.generator.familie.Søker;
import no.nav.foreldrepenger.selvbetjening.kontrakt.innsending.dto.foreldrepenger.AnnenforelderDto;
import no.nav.foreldrepenger.selvbetjening.kontrakt.innsending.util.builder.AnnenforelderBuilder;

public class AnnenforelderMaler {

    private AnnenforelderMaler() {
    }

    public static AnnenforelderDto norskMedRettighetNorge(Søker søker) {
        return AnnenforelderBuilder.norskMedRettighetNorge(søker.fødselsnummer()).build();
    }

    public static AnnenforelderDto norskIkkeRett(Søker søker) {
        return AnnenforelderBuilder.norskIkkeRett(søker.fødselsnummer()).build();
    }

    public static AnnenforelderDto annenpartIkkeRettOgMorHarUføretrygd(Søker søker) {
        return AnnenforelderBuilder.annenpartIkkeRettOgMorHarUføretrygd(søker.fødselsnummer()).build();
    }
}
