package no.nav.foreldrepenger.autotest.domain.foreldrepenger;

import java.util.Arrays;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import no.nav.foreldrepenger.autotest.util.error.UnexpectedInputException;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum MedlemskapManuellVurderingType {

    MEDLEM("MEDLEM"),
    UNNTAK("UNNTAK"),
    IKKE_RELEVANT("IKKE_RELEVANT"),
    SAKSBEHANDLER_SETTER_OPPHØR_AV_MEDL_PGA_ENDRINGER_I_TPS("OPPHOR_PGA_ENDRING_I_TPS"),
    ;

    private final String kode;

    MedlemskapManuellVurderingType(String kode) {
        this.kode = kode;
    }

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static MedlemskapManuellVurderingType fraKode(@JsonProperty(value = "kode") Object node) {
        if (node == null) {
            return null;
        }
        var kode = TempAvledeKode.getVerdi(MedlemskapManuellVurderingType.class, node, "kode");
        return Arrays.stream(MedlemskapManuellVurderingType.values())
                .filter(value -> value.getKode().equalsIgnoreCase(kode))
                .findFirst()
                .orElseThrow(() -> new UnexpectedInputException("Ikke støttet MedlemskapManuellVurderingType " + kode));
    }

    public String getKode() {
        return kode;
    }
}
