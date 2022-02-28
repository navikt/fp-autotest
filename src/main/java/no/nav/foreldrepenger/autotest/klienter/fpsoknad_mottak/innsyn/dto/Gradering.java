package no.nav.foreldrepenger.autotest.klienter.fpsoknad_mottak.innsyn.dto;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

record Gradering(Arbeidstidprosent arbeidstidprosent) {

    @JsonCreator
    Gradering(BigDecimal arbeidstidprosent) {
        this(new Arbeidstidprosent(arbeidstidprosent));
    }

    record Arbeidstidprosent(@JsonValue BigDecimal value) {
        @Override
        public BigDecimal value() {
            return value;
        }
    }
}