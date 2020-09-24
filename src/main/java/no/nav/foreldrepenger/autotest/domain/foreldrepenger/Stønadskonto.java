package no.nav.foreldrepenger.autotest.domain.foreldrepenger;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonEnumDefaultValue;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE, fieldVisibility = JsonAutoDetect.Visibility.ANY)
public enum Stønadskonto {

    FELLESPERIODE,
    MØDREKVOTE,
    FEDREKVOTE,
    FORELDREPENGER,
    FLERBARNSDAGER,
    FORELDREPENGER_FØR_FØDSEL,
    @JsonEnumDefaultValue
    INGEN_STØNADSKONTO("-"),
    ;

    private static final Map<String, Stønadskonto> KODER = new LinkedHashMap<>();

    public static final String KODEVERK = "STOENADSKONTOTYPE";

    static {
        for (var v : values()) {
            if (KODER.putIfAbsent(v.kode, v) != null) {
                throw new IllegalArgumentException("Duplikat : " + v.kode);
            }
        }
    }

    private String kode;

    Stønadskonto() {
        this(null);
    }

    Stønadskonto(String kode) {
        this.kode = Optional.ofNullable(kode)
                .orElse(name());
    }

    @JsonCreator
    public static Stønadskonto fraKode(@JsonProperty("kode") String kode) {
        if (kode == null) {
            return null;
        }
        var ad = KODER.get(kode);
        if (ad == null) {
            throw new IllegalArgumentException("Ukjent StønadskontoType: " + kode);
        }
        return ad;
    }

    @JsonProperty
    public String getKode() {
        return kode;
    }

    @JsonProperty
    public String getKodeverk() {
        return KODEVERK;
    }

}
