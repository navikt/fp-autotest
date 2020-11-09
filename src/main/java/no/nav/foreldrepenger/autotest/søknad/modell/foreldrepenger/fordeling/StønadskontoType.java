package no.nav.foreldrepenger.autotest.søknad.modell.foreldrepenger.fordeling;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonAutoDetect(getterVisibility= JsonAutoDetect.Visibility.ANY, setterVisibility = JsonAutoDetect.Visibility.ANY, fieldVisibility = JsonAutoDetect.Visibility.ANY)
public enum StønadskontoType {
    @JsonProperty("-")
    IKKE_SATT, FELLESPERIODE, MØDREKVOTE, FEDREKVOTE, FORELDREPENGER, FORELDREPENGER_FØR_FØDSEL
}
