package no.nav.foreldrepenger.autotest.søknad.modell.felles;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonValue;

import lombok.Data;

@Data
@JsonAutoDetect(getterVisibility= JsonAutoDetect.Visibility.ANY, setterVisibility = JsonAutoDetect.Visibility.ANY, fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class ProsentAndel {

    @JsonValue
    private final Double prosent;
}
