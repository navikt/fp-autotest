package no.nav.foreldrepenger.generator.soknad.api.erketyper;

import com.neovisionaries.i18n.CountryCode;

import no.nav.foreldrepenger.generator.familie.Søker;
import no.nav.foreldrepenger.generator.soknad.api.builder.AnnenforelderBuilder;
import no.nav.foreldrepenger.generator.soknad.api.dto.foreldrepenger.AnnenforelderDto;

public class AnnenforelderErketyper {

    private AnnenforelderErketyper() {
    }

    public static AnnenforelderDto norskMedRettighetNorge(Søker søker) {
        return new AnnenforelderBuilder()
                .medUtenlandskFnr(false)
                .medHarRettPåForeldrepenger(true)
                .medErInformertOmSøknaden(true)
                .medFnr(søker.fødselsnummer().value())
                .medBostedsland(CountryCode.NO.getAlpha2())
                .build();
    }

    public static AnnenforelderDto norskIkkeRett(Søker søker) {
        return new AnnenforelderBuilder()
                .medUtenlandskFnr(false)
                .medHarRettPåForeldrepenger(false)
                .medErInformertOmSøknaden(false)
                .medHarMorUføretrygd(false)
                .medHarAnnenForelderTilsvarendeRettEØS(false)
                .medFnr(søker.fødselsnummer().value())
                .medBostedsland(CountryCode.NO.getAlpha2())
                .build();
    }

    public static AnnenforelderDto annenpartIkkeRettOgMorHarUføretrygd(Søker søker) {
        return new AnnenforelderBuilder()
                .medHarRettPåForeldrepenger(false)
                .medErInformertOmSøknaden(true)
                .medHarMorUføretrygd(true)
                .medBostedsland(CountryCode.NO.getAlpha2())
                .medFnr(søker.fødselsnummer().value())
                .build();
    }
}
