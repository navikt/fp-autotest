package no.nav.foreldrepenger.autotest.domain.foreldrepenger;

import java.util.Arrays;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import no.nav.foreldrepenger.autotest.util.error.UnexpectedInputException;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum AktivitetStatus {
    ARBEIDSAVKLARINGSPENGER("AAP"),
    ARBEIDSTAKER("AT"),
    DAGPENGER("DP"),
    FRILANSER("FL"),
    MILITÆR_ELLER_SIVIL("MS"),
    SELVSTENDIG_NÆRINGSDRIVENDE("SN"),
    KOMBINERT_AT_FL("AT_FL"),
    KOMBINERT_AT_SN("AT_SN"),
    KOMBINERT_FL_SN("FL_SN"),
    KOMBINERT_AT_FL_SN("AT_FL_SN"),
    BRUKERS_ANDEL("BA"),
    KUN_YTELSE("KUN_YTELSE"),
    TTLSTØTENDE_YTELSE("TY"),
    VENTELØNN_VARTPENGER("VENTELØNN_VARTPENGER"),
    UDEFINERT("-"),
    ;

    private final String kode;

    AktivitetStatus(String kode) {
        this.kode = kode;
    }

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static AktivitetStatus fraKode(@JsonProperty(value = "kode") Object node) {
        if (node == null) {
            return null;
        }
        var kode = TempAvledeKode.getVerdi(PeriodeUtfallÅrsak.class, node, "kode");
        return Arrays.stream(AktivitetStatus.values())
                .filter(value -> value.getKode().equalsIgnoreCase(kode))
                .findFirst()
                .orElseThrow(() -> new UnexpectedInputException("Ikke støttet behandlingtype " + kode));
    }

    public String getKode() {
        return kode;
    }

}
