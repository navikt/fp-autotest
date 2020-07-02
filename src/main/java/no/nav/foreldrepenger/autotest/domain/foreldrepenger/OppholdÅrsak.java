package no.nav.foreldrepenger.autotest.domain.foreldrepenger;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE, fieldVisibility = JsonAutoDetect.Visibility.ANY)
public enum OppholdÅrsak implements Serializable {

    MØDREKVOTE_ANNEN_FORELDER("UTTAK_MØDREKVOTE_ANNEN_FORELDER"),
    FEDREKVOTE_ANNEN_FORELDER("UTTAK_FEDREKVOTE_ANNEN_FORELDER"),
    FELLESPERIODE_ANNEN_FORELDER("UTTAK_FELLESP_ANNEN_FORELDER"),
    FORELDREPENGER_ANNEN_FORELDER("UTTAK_FORELDREPENGER_ANNEN_FORELDER"),
    UDEFINERT("-");

    private static final Map<String, OppholdÅrsak> KODER = new LinkedHashMap<>();

    static {
        for (var v : values()) {
            if (KODER.putIfAbsent(v.kode, v) != null) {
                throw new IllegalArgumentException("Duplikat : " + v.kode);
            }
        }
    }

    private String kode;

    OppholdÅrsak() {

    }

    OppholdÅrsak(String kode) {
        this.kode = kode;
    }

    @JsonCreator
    public static OppholdÅrsak fraKode(@JsonProperty("kode") String kode) {
        if (kode == null) {
            return null;
        }
        var ad = KODER.get(kode);
        if (ad == null) {
            throw new IllegalArgumentException("Ukjent OppholdÅrsak: " + kode);
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
