package no.nav.foreldrepenger.autotest.domain.foreldrepenger;

import java.util.Arrays;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Brukes i uttaksresultat
 */
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum UttakUtsettelseÅrsak {

    ARBEID("ARBEID"),
    FERIE("FERIE"),
    SYKDOM_SKADE("SYKDOM_SKADE"),
    SØKER_INNLAGT("SØKER_INNLAGT"),
    BARN_INNLAGT("BARN_INNLAGT"),
    HV_OVELSE("HV_OVELSE"),
    NAV_TILTAK("NAV_TILTAK"),
    FRI("FRI"),
    UDEFINERT("-"),
    ;

    private final String kode;

    UttakUtsettelseÅrsak(String kode) {
        this.kode = kode;
    }

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static UttakUtsettelseÅrsak fraKode(@JsonProperty(value = "kode") Object node) {
        if (node == null) {
            return null;
        }
        var kode = TempAvledeKode.getVerdi(UttakUtsettelseÅrsak.class, node, "kode");
        return Arrays.stream(UttakUtsettelseÅrsak.values())
                .filter(value -> value.getKode().equalsIgnoreCase(kode))
                .findFirst()
                .orElse(UttakUtsettelseÅrsak.UDEFINERT);
    }

    public String getKode() {
        return kode;
    }
}
