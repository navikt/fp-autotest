package no.nav.foreldrepenger.autotest.domain.foreldrepenger;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Brukes i uttaksresultat
 */
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE, fieldVisibility = JsonAutoDetect.Visibility.ANY)
public enum UttakUtsettelseÅrsak implements Serializable {

    ARBEID("ARBEID"),
    FERIE("FERIE"),
    SYKDOM_SKADE("SYKDOM_SKADE"),
    SØKER_INNLAGT("SØKER_INNLAGT"),
    BARN_INNLAGT("BARN_INNLAGT"),
    HV_OVELSE("HV_OVELSE"),
    NAV_TILTAK("NAV_TILTAK"),
    UDEFINERT("-"),
    ;

    private static final Map<String, UttakUtsettelseÅrsak> KODER = new LinkedHashMap<>();

    static {
        for (var v : values()) {
            if (KODER.putIfAbsent(v.kode, v) != null) {
                throw new IllegalArgumentException("Duplikat : " + v.kode);
            }
        }
    }

    private String kode;

    UttakUtsettelseÅrsak() {

    }

    UttakUtsettelseÅrsak(String kode) {
        this.kode = kode;
    }

    @JsonCreator
    public static UttakUtsettelseÅrsak fraKode(@JsonProperty("kode") String kode) {
        if (kode == null) {
            return null;
        }
        var ad = KODER.get(kode);
        if (ad == null) {
            throw new IllegalArgumentException("Ukjent Utsettelseårsak: " + kode);
        }
        return ad;
    }

    @JsonProperty
    public String getKode() {
        return kode;
    }

    @Override
    public String toString() {
        return getKode();
    }
}
