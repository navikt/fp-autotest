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
public enum Stønadskonto implements Serializable {

    FORELDREPENGER_FØR_FØDSEL("FORELDREPENGER_FØR_FØDSEL"),
    MØDREKVOTE("MØDREKVOTE"),
    FEDREKVOTE("FEDREKVOTE"),
    FELLESPERIODE("FELLESPERIODE"),
    FORELDREPENGER("FORELDREPENGER"),
    FLERBARNSDAGER("FLERBARNSDAGER"),
    INGEN_STØNADSKONTO("-");

    private static final Map<String, Stønadskonto> KODER = new LinkedHashMap<>();

    static {
        for (var v : values()) {
            if (KODER.putIfAbsent(v.kode, v) != null) {
                throw new IllegalArgumentException("Duplikat : " + v.kode);
            }
        }
    }

    private String kode;

    Stønadskonto() {

    }

    Stønadskonto(String kode) {
        this.kode = kode;
    }

    @JsonCreator
    public static Stønadskonto fraKode(@JsonProperty("kode") String kode) {
        if (kode == null) {
            return null;
        }
        var ad = KODER.get(kode);
        if (ad == null) {
            throw new IllegalArgumentException("Ukjent Stønadskonto: " + kode);
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
