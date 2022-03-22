package no.nav.foreldrepenger.autotest.domain.foreldrepenger;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonValue;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum PermisjonsbeskrivelseType {
    UDEFINERT("-"),
    PERMISJON("PERMISJON"),
    UTDANNINGSPERMISJON("UTDANNINGSPERMISJON"),
    VELFERDSPERMISJON("VELFERDSPERMISJON"),
    PERMISJON_MED_FORELDREPENGER("PERMISJON_MED_FORELDREPENGER"),
    PERMITTERING("PERMITTERING"),
    PERMISJON_VED_MILITÆRTJENESTE("PERMISJON_VED_MILITÆRTJENESTE");

    public static final String KODEVERK = "PERMISJONSBESKRIVELSE_TYPE";

    @JsonValue
    private final String kode;


    PermisjonsbeskrivelseType(String kode) {
        this.kode = kode;
    }

    PermisjonsbeskrivelseType(String kode, String navn) {
        this.kode = kode;
    }

    public String getKode() {
        return kode;
    }
}
