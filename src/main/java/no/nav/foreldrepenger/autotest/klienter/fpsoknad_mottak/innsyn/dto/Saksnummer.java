package no.nav.foreldrepenger.autotest.klienter.fpsoknad_mottak.innsyn.dto;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

record Saksnummer(@JsonValue String value) {

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    Saksnummer {
        Objects.requireNonNull(value, "saksnummer kan ikke være null");
    }

    @Override
    public String value() {
        return value;
    }
}
