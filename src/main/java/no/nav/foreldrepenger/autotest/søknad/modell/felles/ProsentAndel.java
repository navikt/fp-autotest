package no.nav.foreldrepenger.autotest.søknad.modell.felles;

import com.fasterxml.jackson.annotation.JsonValue;

import lombok.Data;

@Data
public class ProsentAndel {

    @JsonValue
    private final Double prosent;
}
