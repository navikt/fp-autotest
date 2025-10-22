package no.nav.foreldrepenger.generator.soknad.maler;

import com.neovisionaries.i18n.CountryCode;

import no.nav.foreldrepenger.autotest.klienter.fpsoknad.kontrakt.foreldrepenger.annenpart.AnnenForelderDto;
import no.nav.foreldrepenger.common.domain.Fødselsnummer;
import no.nav.foreldrepenger.generator.familie.Søker;
import no.nav.foreldrepenger.generator.soknad.builder.AnnenforelderBuilder;

public class AnnenforelderMaler {

    private AnnenforelderMaler() {
    }

    public static AnnenForelderDto norskMedRettighetNorge(Søker annenpart) {
        return AnnenforelderBuilder.norskMedRettighetNorge(annenpart.fødselsnummer()).build();
    }

    public static AnnenForelderDto norskIkkeRett(Søker annenpart) {
        return AnnenforelderBuilder.norskIkkeRett(annenpart.fødselsnummer()).build();
    }

    public static AnnenForelderDto norskAleneomsorg(Søker annenpart) {
        return AnnenforelderBuilder.aleneomsorg(annenpart.fødselsnummer())
                .medHarRettPåForeldrepenger(false)
                .build();
    }

    public static AnnenForelderDto utenlandskMedRettighetEØS(String annenpartFnr, CountryCode land) {
        return AnnenforelderBuilder.utenlandskForelderRettEØS(new Fødselsnummer(annenpartFnr), land)
                .medHarMorUføretrygd(false) // Fix: utenlandskForelderRettEØS
                .build();
    }

    public static AnnenForelderDto annenpartIkkeRettOgMorHarUføretrygd(Søker søker) {
        return AnnenforelderBuilder.norskIkkeRettOgMorUføretrygd(søker.fødselsnummer()).build();
    }

    public static AnnenForelderDto ukjentForelder() {
        return AnnenforelderBuilder.ukjentForelder();
    }
}
