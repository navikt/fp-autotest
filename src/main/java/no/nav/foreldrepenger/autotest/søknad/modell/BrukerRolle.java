package no.nav.foreldrepenger.autotest.søknad.modell;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

@JsonAutoDetect(getterVisibility= JsonAutoDetect.Visibility.ANY, setterVisibility = JsonAutoDetect.Visibility.ANY, fieldVisibility = JsonAutoDetect.Visibility.ANY)
public enum BrukerRolle {
    MOR, FAR, MEDMOR, IKKE_RELEVANT
}
