package no.nav.foreldrepenger.autotest.søknad.modell.foreldrepenger.fordeling;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

@JsonAutoDetect(getterVisibility= JsonAutoDetect.Visibility.ANY, setterVisibility = JsonAutoDetect.Visibility.ANY, fieldVisibility = JsonAutoDetect.Visibility.ANY)
public enum Overføringsårsak {
    INSTITUSJONSOPPHOLD_ANNEN_FORELDER, SYKDOM_ANNEN_FORELDER, ALENEOMSORG, IKKE_RETT_ANNEN_FORELDER
}
