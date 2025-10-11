package no.nav.foreldrepenger.autotest.domain.foreldrepenger;

import com.fasterxml.jackson.annotation.JsonValue;

public enum OmsorgsovertakelseVilkårType {

    ES_ADOPSJONSVILKÅRET("FP_VK_4"),
    ES_OMSORGSVILKÅRET("FP_VK_5"),
    ES_FORELDREANSVARSVILKÅRET_2_LEDD("FP_VK_8"),
    ES_FORELDREANSVARSVILKÅRET_4_LEDD("FP_VK_33"),

    FP_ADOPSJONSVILKÅRET("FP_VK_16"),
    FP_FORELDREANSVARSVILKÅRET_2_LEDD("FP_VK_8F"),
    FP_STEBARNSADOPSJONSVILKÅRET("FP_VK_16S")
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
