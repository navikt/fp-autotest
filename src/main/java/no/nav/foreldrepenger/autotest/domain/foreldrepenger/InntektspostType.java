package no.nav.foreldrepenger.autotest.domain.foreldrepenger;

import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonValue;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum InntektspostType {

    UDEFINERT("-"),
    LØNN("LØNN"),
    YTELSE("YTELSE"),
    VANLIG("VANLIG"),
    SELVSTENDIG_NÆRINGSDRIVENDE("SELVSTENDIG_NÆRINGSDRIVENDE"),
    NÆRING_FISKE_FANGST_FAMBARNEHAGE("NÆRING_FISKE_FANGST_FAMBARNEHAGE"),
    ;
    public static final String KODEVERK = "INNTEKTSPOST_TYPE";

    @JsonValue
    private final String kode;

    InntektspostType() {
        this(null);
    }

    InntektspostType(String kode) {
        this.kode = Optional.ofNullable(kode).orElse(name());
    }

    public String getKode() {
        return kode;
    }
}
