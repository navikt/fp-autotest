package no.nav.foreldrepenger.autotest.domain.foreldrepenger;

import java.util.Arrays;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum KonsekvensForYtelsen {
    FORELDREPENGER_OPPHØRER,
    ENDRING_I_BEREGNING,
    ENDRING_I_UTTAK,
    ENDRING_I_FORDELING_AV_YTELSEN,
    INGEN_ENDRING,
    ENDRING_I_BEREGNING_OG_UTTAK,
    UDEFINERT("-"),
    ;

    private final String kode;

    KonsekvensForYtelsen() {
        this(null);
    }

    KonsekvensForYtelsen(String kode) {
        this.kode = Optional.ofNullable(kode).orElse(name());
    }

    @JsonCreator
    public static KonsekvensForYtelsen fraKode(String kode) {
        return Arrays.stream(KonsekvensForYtelsen.values())
                .filter(value -> value.getKode().equalsIgnoreCase(kode))
                .findFirst()
                .orElse(KonsekvensForYtelsen.UDEFINERT);
    }

    public String getKode() {
        return kode;
    }
}
