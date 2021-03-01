package no.nav.foreldrepenger.autotest.domain.foreldrepenger;

import java.util.Arrays;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;

import no.nav.foreldrepenger.autotest.util.error.UnexpectedInputException;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum OmsorgsovertakelseVilkårType implements Kode {

    OMSORGSVILKÅRET("FP_VK_5"),
    FORELDREANSVARSVILKÅRET_2_LEDD("FP_VK_8"),
    FORELDREANSVARSVILKÅRET_4_LEDD("FP_VK_33"),
    ;

    private final String kode;

    OmsorgsovertakelseVilkårType(String kode) {
        this.kode = kode;
    }

    @JsonCreator
    public static OmsorgsovertakelseVilkårType fraKode(String kode) {
        return Arrays.stream(OmsorgsovertakelseVilkårType.values())
                .filter(value -> value.getKode().equalsIgnoreCase(kode))
                .findFirst()
                .orElseThrow(() -> new UnexpectedInputException("Ikke støttet OmsorgsovertakelseVilkårType %s.", kode));
    }

    @Override
    public String getKode() {
        return kode;
    }
}
