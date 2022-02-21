package no.nav.foreldrepenger.autotest.klienter.fpsoknad_mottak.innsyn.dto.persondetaljer;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public record Fødselsnummer(@JsonValue String value) {

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public Fødselsnummer {
        Objects.requireNonNull(value, "Fødselsnummer kan ikke være null");
    }

    @Override
    public String value() {
        return value;
    }

    @Override
    public String toString() {
        return "************";
    }
}
