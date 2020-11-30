package no.nav.foreldrepenger.autotest.søknad.modell.felles.opptjening;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

@JsonAutoDetect(getterVisibility= JsonAutoDetect.Visibility.ANY, setterVisibility = JsonAutoDetect.Visibility.ANY, fieldVisibility = JsonAutoDetect.Visibility.ANY)
public enum Virksomhetstype {
    ANNEN,
    JORDBRUK_SKOGBRUK,
    FISKE,
    DAGMAMMA
}
