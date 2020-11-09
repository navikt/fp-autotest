package no.nav.foreldrepenger.autotest.søknad.modell.felles.medlemskap;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

@JsonAutoDetect(getterVisibility= JsonAutoDetect.Visibility.ANY, setterVisibility = JsonAutoDetect.Visibility.ANY, fieldVisibility = JsonAutoDetect.Visibility.ANY)
public enum ArbeidsInformasjon {
    ARBEIDET_I_NORGE,
    ARBEIDET_I_UTLANDET,
    IKKE_ARBEIDET
}
