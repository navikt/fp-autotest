package no.nav.foreldrepenger.autotest.klienter.fpsak.behandlinger.dto.behandling.svangerskapspenger;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Tilretteleggingsdato {

    protected LocalDate fom;
    protected TilretteleggingType type;
    protected BigDecimal stillingsprosent;

}
