package no.nav.foreldrepenger.autotest.søknad.modell.foreldrepenger.fordeling;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

@JsonAutoDetect(getterVisibility= JsonAutoDetect.Visibility.ANY, setterVisibility = JsonAutoDetect.Visibility.ANY, fieldVisibility = JsonAutoDetect.Visibility.ANY)
public enum UtsettelsesÅrsak {
    ARBEID,
    LOVBESTEMT_FERIE,
    SYKDOM,
    INSTITUSJONSOPPHOLD_SØKER,
    INSTITUSJONSOPPHOLD_BARNET,
    HV_OVELSE("periode.utsettelse.hv"),
    NAV_TILTAK("periode.utsettelse.nav");

    private final String key;

    UtsettelsesÅrsak() {
        this(null);
    }

    UtsettelsesÅrsak(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}
