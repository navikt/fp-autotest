package no.nav.foreldrepenger.autotest.domain.foreldrepenger;

import java.util.Arrays;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum OppholdÅrsak {
    MØDREKVOTE_ANNEN_FORELDER("UTTAK_MØDREKVOTE_ANNEN_FORELDER"),
    FEDREKVOTE_ANNEN_FORELDER("UTTAK_FEDREKVOTE_ANNEN_FORELDER"),
    FELLESPERIODE_ANNEN_FORELDER("UTTAK_FELLESP_ANNEN_FORELDER"),
    FORELDREPENGER_ANNEN_FORELDER("UTTAK_FORELDREPENGER_ANNEN_FORELDER"),
    INGEN("INGEN"),
    UDEFINERT("-");

    private final String kode;

    OppholdÅrsak(String kode) {
        this.kode = kode;
    }

    @JsonCreator
    public static OppholdÅrsak fraKode(@JsonProperty("kode") String kode) {
        return Arrays.stream(OppholdÅrsak.values())
                .filter(value -> value.getKode().equalsIgnoreCase(kode))
                .findFirst()
                .orElse(OppholdÅrsak.UDEFINERT);
    }

    public String getKode() {
        return kode;
    }
}
