package no.nav.foreldrepenger.generator.soknad.api.builder;

import no.nav.foreldrepenger.generator.soknad.api.dto.foreldrepenger.AnnenforelderDto;

public class AnnenforelderBuilder {
    private Boolean kanIkkeOppgis;
    private String fornavn;
    private String etternavn;
    private String fnr;
    private String bostedsland;
    private Boolean utenlandskFnr;
    private boolean harRettPåForeldrepenger;
    private boolean erInformertOmSøknaden;
    private Boolean harMorUføretrygd;
    private Boolean harAnnenForelderOppholdtSegIEØS;
    private Boolean harAnnenForelderTilsvarendeRettEØS;

    public AnnenforelderBuilder() {
    }

    public static AnnenforelderDto ukjentForelder() {
        return new AnnenforelderBuilder().medKanIkkeOppgis(true).build();
    }


    public AnnenforelderBuilder medKanIkkeOppgis(Boolean kanIkkeOppgis) {
        this.kanIkkeOppgis = kanIkkeOppgis;
        return this;
    }

    public AnnenforelderBuilder medFornavn(String fornavn) {
        this.fornavn = fornavn;
        return this;
    }

    public AnnenforelderBuilder medEtternavn(String etternavn) {
        this.etternavn = etternavn;
        return this;
    }

    public AnnenforelderBuilder medFnr(String fnr) {
        this.fnr = fnr;
        return this;
    }

    public AnnenforelderBuilder medBostedsland(String bostedsland) {
        this.bostedsland = bostedsland;
        return this;
    }

    public AnnenforelderBuilder medUtenlandskFnr(Boolean utenlandskFnr) {
        this.utenlandskFnr = utenlandskFnr;
        return this;
    }

    public AnnenforelderBuilder medHarRettPåForeldrepenger(boolean harRettPåForeldrepenger) {
        this.harRettPåForeldrepenger = harRettPåForeldrepenger;
        return this;
    }

    public AnnenforelderBuilder medErInformertOmSøknaden(boolean erInformertOmSøknaden) {
        this.erInformertOmSøknaden = erInformertOmSøknaden;
        return this;
    }

    public AnnenforelderBuilder medHarMorUføretrygd(Boolean harMorUføretrygd) {
        this.harMorUføretrygd = harMorUføretrygd;
        return this;
    }

    public AnnenforelderBuilder medHarAnnenForelderOppholdtSegIEØS(Boolean harAnnenForelderOppholdtSegIEØS) {
        this.harAnnenForelderOppholdtSegIEØS = harAnnenForelderOppholdtSegIEØS;
        return this;
    }

    public AnnenforelderBuilder medHarAnnenForelderTilsvarendeRettEØS(Boolean harAnnenForelderTilsvarendeRettEØS) {
        this.harAnnenForelderTilsvarendeRettEØS = harAnnenForelderTilsvarendeRettEØS;
        return this;
    }

    public AnnenforelderDto build() {
        return new AnnenforelderDto(
                kanIkkeOppgis,
                fornavn,
                etternavn,
                fnr,
                bostedsland,
                utenlandskFnr,
                harRettPåForeldrepenger,
                erInformertOmSøknaden,
                harMorUføretrygd,
                harAnnenForelderOppholdtSegIEØS,
                harAnnenForelderTilsvarendeRettEØS
        );
    }
}
