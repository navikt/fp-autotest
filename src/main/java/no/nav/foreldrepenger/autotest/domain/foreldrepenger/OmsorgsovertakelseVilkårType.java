package no.nav.foreldrepenger.autotest.domain.foreldrepenger;

import java.util.Arrays;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import no.nav.foreldrepenger.autotest.util.error.UnexpectedInputException;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum OmsorgsovertakelseVilkårType {

    OMSORGSVILKÅRET("FP_VK_5"),
    FORELDREANSVARSVILKÅRET_2_LEDD("FP_VK_8"),
    FORELDREANSVARSVILKÅRET_4_LEDD("FP_VK_33"),
    ;

    private final String kode;

    OmsorgsovertakelseVilkårType(String kode) {
        this.kode = kode;
    }

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public static OmsorgsovertakelseVilkårType fraKode(@JsonProperty(value = "kode") Object node) {
        if (node == null) {
            return null;
        }
        var kode = TempAvledeKode.getVerdi(PeriodeUtfallÅrsak.class, node, "kode");
        return Arrays.stream(OmsorgsovertakelseVilkårType.values())
                .filter(value -> value.getKode().equalsIgnoreCase(kode))
                .findFirst()
                .orElseThrow(() -> new UnexpectedInputException("Ikke støttet OmsorgsovertakelseVilkårType " + kode));
    }

    public String getKode() {
        return kode;
    }
}
