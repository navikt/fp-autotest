package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.beregning;

import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonValue;

public enum FaktaOmBeregningTilfelle {

    VURDER_TIDSBEGRENSET_ARBEIDSFORHOLD,
    VURDER_SN_NY_I_ARBEIDSLIVET,
    VURDER_NYOPPSTARTET_FL,
    FASTSETT_MAANEDSINNTEKT_FL,
    FASTSETT_BG_ARBEIDSTAKER_UTEN_INNTEKTSMELDING,
    VURDER_LØNNSENDRING,
    FASTSETT_MÅNEDSLØNN_ARBEIDSTAKER_UTEN_INNTEKTSMELDING,
    VURDER_AT_OG_FL_I_SAMME_ORGANISASJON,
    FASTSETT_BESTEBEREGNING_FØDENDE_KVINNE,
    VURDER_ETTERLØNN_SLUTTPAKKE,
    FASTSETT_ETTERLØNN_SLUTTPAKKE,
    VURDER_MOTTAR_YTELSE,
    VURDER_BESTEBEREGNING,
    VURDER_MILITÆR_SIVILTJENESTE,
    VURDER_REFUSJONSKRAV_SOM_HAR_KOMMET_FOR_SENT,
    FASTSETT_BG_KUN_YTELSE,
    TILSTØTENDE_YTELSE,
    UDEFINERT("-"),
    ;

    @JsonValue
    private final String kode;

    FaktaOmBeregningTilfelle() {
        this(null);
    }

    FaktaOmBeregningTilfelle(String kode) {
        this.kode = Optional.ofNullable(kode).orElse(name());
    }

    public String getKode() {
        return kode;
    }
}
