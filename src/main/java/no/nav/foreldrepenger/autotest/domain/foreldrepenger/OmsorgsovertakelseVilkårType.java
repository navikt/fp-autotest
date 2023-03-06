package no.nav.foreldrepenger.autotest.domain.foreldrepenger;

import com.fasterxml.jackson.annotation.JsonValue;

public enum OmsorgsovertakelseVilkårType {

    OMSORGSVILKÅRET("FP_VK_5"),
    FORELDREANSVARSVILKÅRET_2_LEDD("FP_VK_8"),
    FORELDREANSVARSVILKÅRET_4_LEDD("FP_VK_33"),
    ;

    @JsonValue
    private final String kode;

    OmsorgsovertakelseVilkårType(String kode) {
        this.kode = kode;
    }

    public String getKode() {
        return kode;
    }
}
