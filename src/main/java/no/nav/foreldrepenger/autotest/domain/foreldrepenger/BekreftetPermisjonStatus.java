package no.nav.foreldrepenger.autotest.domain.foreldrepenger;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonValue;



@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum BekreftetPermisjonStatus {
    UDEFINERT("-"),
    BRUK_PERMISJON("BRUK_PERMISJON"),
    IKKE_BRUK_PERMISJON("IKKE_BRUK_PERMISJON"),
    UGYLDIGE_PERIODER("UGYLDIGE_PERIODER");

    public static final String KODEVERK = "BEKREFTET_PERMISJON_STATUS";

    @JsonValue
    private final String kode;


    BekreftetPermisjonStatus(String kode) {
        this.kode = kode;
    }

    BekreftetPermisjonStatus(String kode, String navn) {
        this.kode = kode;
    }

    public String getKode() {
        return kode;
    }

}
