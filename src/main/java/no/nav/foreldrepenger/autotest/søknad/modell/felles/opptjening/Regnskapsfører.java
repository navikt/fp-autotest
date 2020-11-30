package no.nav.foreldrepenger.autotest.søknad.modell.felles.opptjening;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

import lombok.Data;

@Data
@JsonAutoDetect(getterVisibility= JsonAutoDetect.Visibility.ANY, setterVisibility = JsonAutoDetect.Visibility.ANY, fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class Regnskapsfører {
    private final String navn;
    private final String telefon;
}
