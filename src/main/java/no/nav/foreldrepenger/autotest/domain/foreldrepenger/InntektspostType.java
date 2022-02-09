package no.nav.foreldrepenger.autotest.domain.foreldrepenger;

import java.util.Arrays;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;

import no.nav.foreldrepenger.autotest.util.error.UnexpectedInputException;

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

    private final String kode;

    InntektspostType() {
        this(null);
    }

    InntektspostType(String kode) {
        this.kode = Optional.ofNullable(kode).orElse(name());
    }

    @JsonCreator
    public static InntektspostType fraKode(String kode) {
        return Arrays.stream(InntektspostType.values())
                .filter(value -> value.getKode().equalsIgnoreCase(kode))
                .findFirst()
                .orElseThrow(() -> new UnexpectedInputException("Ikke støttet InntektspostType " + kode));
    }

    public String getKode() {
        return kode;
    }
}
