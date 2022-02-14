package no.nav.foreldrepenger.autotest.domain.foreldrepenger;

import java.util.Arrays;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import no.nav.foreldrepenger.autotest.util.error.UnexpectedInputException;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum UttakPeriodeVurderingType {

    PERIODE_OK,
    PERIODE_OK_ENDRET,
    PERIODE_KAN_IKKE_AVKLARES,
    PERIODE_IKKE_VURDERT,
    ;

    private final String kode;

    UttakPeriodeVurderingType() {
        this(null);
    }

    UttakPeriodeVurderingType(String kode) {
        this.kode = Optional.ofNullable(kode).orElse(name());
    }

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static UttakPeriodeVurderingType fraKode(@JsonProperty(value = "kode") Object node) {
        if (node == null) {
            return null;
        }
        var kode = TempAvledeKode.getVerdi(UttakPeriodeVurderingType.class, node, "kode");
        return Arrays.stream(UttakPeriodeVurderingType.values())
                .filter(value -> value.getKode().equalsIgnoreCase(kode))
                .findFirst()
                .orElseThrow(() -> new UnexpectedInputException("Ikke støttet UttakPeriodeVurderingType " + kode));
    }

    public String getKode() {
        return kode;
    }
}

